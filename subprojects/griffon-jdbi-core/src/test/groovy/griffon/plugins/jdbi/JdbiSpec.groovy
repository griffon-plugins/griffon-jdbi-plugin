/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2014-2020 The author and/or original authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.plugins.jdbi

import griffon.annotations.inject.BindTo
import griffon.core.GriffonApplication
import griffon.plugins.datasource.events.DataSourceConnectEndEvent
import griffon.plugins.datasource.events.DataSourceConnectStartEvent
import griffon.plugins.datasource.events.DataSourceDisconnectEndEvent
import griffon.plugins.datasource.events.DataSourceDisconnectStartEvent
import griffon.plugins.jdbi.events.JdbiConnectEndEvent
import griffon.plugins.jdbi.events.JdbiConnectStartEvent
import griffon.plugins.jdbi.events.JdbiDisconnectEndEvent
import griffon.plugins.jdbi.events.JdbiDisconnectStartEvent
import griffon.plugins.jdbi.exceptions.RuntimeJdbiException
import griffon.test.core.GriffonUnitRule
import org.junit.Rule
import org.skife.jdbi.v2.DBI
import spock.lang.Specification
import spock.lang.Unroll

import javax.application.event.EventHandler
import javax.inject.Inject

@Unroll
class JdbiSpec extends Specification {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    @Inject
    private JdbiHandler jdbiHandler

    @Inject
    private GriffonApplication application

    void 'Open and close default jdbi'() {
        given:
        List eventNames = [
            'JdbiConnectStartEvent', 'DataSourceConnectStartEvent',
            'DataSourceConnectEndEvent', 'JdbiConnectEndEvent',
            'JdbiDisconnectStartEvent', 'DataSourceDisconnectStartEvent',
            'DataSourceDisconnectEndEvent', 'JdbiDisconnectEndEvent'
        ]
        TestEventHandler testEventHandler = new TestEventHandler()
        application.eventRouter.subscribe(testEventHandler)

        when:
        jdbiHandler.withJdbi { String datasourceName, DBI dbi ->
            true
        }
        jdbiHandler.closeJdbi()
        // second call should be a NOOP
        jdbiHandler.closeJdbi()

        then:
        testEventHandler.events.size() == 8
        testEventHandler.events == eventNames
    }

    void 'Connect to default DBI'() {
        expect:
        jdbiHandler.withJdbi { String datasourceName, DBI dbi ->
            datasourceName == 'default' && dbi
        }
    }

    void 'Bootstrap init is called'() {
        given:
        assert !bootstrap.initWitness

        when:
        jdbiHandler.withJdbi { String datasourceName, DBI dbi -> }

        then:
        bootstrap.initWitness
        !bootstrap.destroyWitness
    }

    void 'Bootstrap destroy is called'() {
        given:
        assert !bootstrap.initWitness
        assert !bootstrap.destroyWitness

        when:
        jdbiHandler.withJdbi { String datasourceName, DBI dbi -> }
        jdbiHandler.closeJdbi()

        then:
        bootstrap.initWitness
        bootstrap.destroyWitness
    }

    void 'Can connect to #name DBI'() {
        expect:
        jdbiHandler.withJdbi(name) { String datasourceName, DBI dbi ->
            datasourceName == name && dbi
        }

        where:
        name       | _
        'default'  | _
        'internal' | _
        'people'   | _
    }

    void 'Bogus DBI name (#name) results in error'() {
        when:
        jdbiHandler.withJdbi(name) { String datasourceName, DBI dbi ->
            true
        }

        then:
        thrown(IllegalArgumentException)

        where:
        name    | _
        null    | _
        ''      | _
        'bogus' | _
    }

    void 'Execute statements on people table'() {
        when:
        List peopleIn = jdbiHandler.withJdbi('people') { String datasourceName, DBI dbi ->
            PersonDAO dao = dbi.open(PersonDAO)
            [[id: 1, name: 'Danno', lastname: 'Ferrin'],
             [id: 2, name: 'Andres', lastname: 'Almiray'],
             [id: 3, name: 'James', lastname: 'Williams'],
             [id: 4, name: 'Guillaume', lastname: 'Laforge'],
             [id: 5, name: 'Jim', lastname: 'Shingler'],
             [id: 6, name: 'Alexander', lastname: 'Klein'],
             [id: 7, name: 'Rene', lastname: 'Groeschke']].collect([]) { data ->
                dao.create(data.id, data.name, data.lastname)
                new Person(data)
            }
        }

        List peopleOut = jdbiHandler.withJdbi('people') { String datasourceName, DBI dbi ->
            dbi.open(PersonDAO).list()
        }

        then:
        peopleIn == peopleOut
    }

    void 'A runtime SQLException is thrown within DBI handling'() {
        when:
        jdbiHandler.withJdbi { String datasourceName, DBI dbi ->
            PersonDAO dao = dbi.open(PersonDAO)
            dao.create(0, null, null)
        }

        then:
        thrown(RuntimeJdbiException)
    }

    @BindTo(JdbiBootstrap)
    private TestJdbiBootstrap bootstrap = new TestJdbiBootstrap()

    private class TestEventHandler {
        List<String> events = []

        @EventHandler
        void handleDataSourceConnectStartEvent(DataSourceConnectStartEvent event) {
            events << event.class.simpleName
        }

        @EventHandler
        void handleDataSourceConnectEndEvent(DataSourceConnectEndEvent event) {
            events << event.class.simpleName
        }

        @EventHandler
        void handleDataSourceDisconnectStartEvent(DataSourceDisconnectStartEvent event) {
            events << event.class.simpleName
        }

        @EventHandler
        void handleDataSourceDisconnectEndEvent(DataSourceDisconnectEndEvent event) {
            events << event.class.simpleName
        }

        @EventHandler
        void handleJdbiConnectStartEvent(JdbiConnectStartEvent event) {
            events << event.class.simpleName
        }

        @EventHandler
        void handleJdbiConnectEndEvent(JdbiConnectEndEvent event) {
            events << event.class.simpleName
        }

        @EventHandler
        void handleJdbiDisconnectStartEvent(JdbiDisconnectStartEvent event) {
            events << event.class.simpleName
        }

        @EventHandler
        void handleJdbiDisconnectEndEvent(JdbiDisconnectEndEvent event) {
            events << event.class.simpleName
        }
    }
}

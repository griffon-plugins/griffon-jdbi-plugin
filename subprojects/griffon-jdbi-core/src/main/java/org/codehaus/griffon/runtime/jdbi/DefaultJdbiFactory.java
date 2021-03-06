/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2014-2021 The author and/or original authors.
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
package org.codehaus.griffon.runtime.jdbi;

import griffon.annotations.core.Nonnull;
import griffon.core.GriffonApplication;
import griffon.core.env.Metadata;
import griffon.core.injection.Injector;
import griffon.plugins.datasource.DataSourceFactory;
import griffon.plugins.datasource.DataSourceStorage;
import griffon.plugins.jdbi.JdbiBootstrap;
import griffon.plugins.jdbi.JdbiFactory;
import griffon.plugins.jdbi.events.JdbiConnectEndEvent;
import griffon.plugins.jdbi.events.JdbiConnectStartEvent;
import griffon.plugins.jdbi.events.JdbiDisconnectEndEvent;
import griffon.plugins.jdbi.events.JdbiDisconnectStartEvent;
import griffon.plugins.monitor.MBeanManager;
import org.codehaus.griffon.runtime.core.storage.AbstractObjectFactory;
import org.codehaus.griffon.runtime.jdbi.monitor.DBIMonitor;
import org.skife.jdbi.v2.DBI;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Set;

import static griffon.util.ConfigUtils.getConfigValueAsBoolean;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class DefaultJdbiFactory extends AbstractObjectFactory<DBI> implements JdbiFactory {
    @Inject
    private DataSourceFactory dataSourceFactory;

    @Inject
    private DataSourceStorage dataSourceStorage;

    @Inject
    private Injector injector;

    @Inject
    private MBeanManager mBeanManager;

    @Inject
    private Metadata metadata;

    @Inject
    public DefaultJdbiFactory(@Nonnull @Named("datasource") griffon.core.Configuration configuration, @Nonnull GriffonApplication application) {
        super(configuration, application);
    }

    @Nonnull
    @Override
    public Set<String> getDatasourceNames() {
        return dataSourceFactory.getDataSourceNames();
    }

    @Nonnull
    @Override
    public Map<String, Object> getConfigurationFor(@Nonnull String datasourceName) {
        return dataSourceFactory.getConfigurationFor(datasourceName);
    }

    @Nonnull
    @Override
    protected String getSingleKey() {
        return "dataSource";
    }

    @Nonnull
    @Override
    protected String getPluralKey() {
        return "dataSources";
    }

    @Nonnull
    @Override
    public DBI create(@Nonnull String name) {
        Map<String, Object> config = getConfigurationFor(name);
        event(JdbiConnectStartEvent.of(name, config));
        DBI dbi = createDBI(name);

        if (getConfigValueAsBoolean(config, "jmx", true)) {
            dbi = new JMXAwareDBI(getDataSource(name), dbi);
            registerMBeans(name, (JMXAwareDBI) dbi);
        }

        for (Object o : injector.getInstances(JdbiBootstrap.class)) {
            ((JdbiBootstrap) o).init(name, dbi);
        }

        event(JdbiConnectEndEvent.of(name, config, dbi));
        return dbi;
    }

    @Override
    public void destroy(@Nonnull String name, @Nonnull DBI instance) {
        requireNonNull(instance, "Argument 'instance' must not be null");
        Map<String, Object> config = getConfigurationFor(name);
        event(JdbiDisconnectStartEvent.of(name, config, instance));

        for (Object o : injector.getInstances(JdbiBootstrap.class)) {
            ((JdbiBootstrap) o).destroy(name, instance);
        }

        closeDataSource(name);

        if (getConfigValueAsBoolean(config, "jmx", true)) {
            unregisterMBeans((JMXAwareDBI) instance);
        }

        event(JdbiDisconnectEndEvent.of(name, config));
    }

    private void registerMBeans(@Nonnull String name, @Nonnull JMXAwareDBI dbi) {
        RecordingDBI recordingIDBI = (RecordingDBI) dbi.getDelegate();
        DBIMonitor dbiMonitor = new DBIMonitor(metadata, recordingIDBI, name);
        dbi.addObjectName(mBeanManager.registerMBean(dbiMonitor, false).getCanonicalName());
    }

    private void unregisterMBeans(@Nonnull JMXAwareDBI dbi) {
        for (String objectName : dbi.getObjectNames()) {
            mBeanManager.unregisterMBean(objectName);
        }
        dbi.clearObjectNames();
    }

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    protected DBI createDBI(@Nonnull String dataSourceName) {
        DataSource dataSource = getDataSource(dataSourceName);
        return new RecordingDBI(dataSource, new DBI(dataSource));
    }

    protected void closeDataSource(@Nonnull String dataSourceName) {
        DataSource dataSource = dataSourceStorage.get(dataSourceName);
        if (dataSource != null) {
            dataSourceFactory.destroy(dataSourceName, dataSource);
            dataSourceStorage.remove(dataSourceName);
        }
    }

    @Nonnull
    protected DataSource getDataSource(@Nonnull String dataSourceName) {
        DataSource dataSource = dataSourceStorage.get(dataSourceName);
        if (dataSource == null) {
            dataSource = dataSourceFactory.create(dataSourceName);
            dataSourceStorage.set(dataSourceName, dataSource);
        }
        return dataSource;
    }
}

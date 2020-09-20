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
package org.codehaus.griffon.runtime.jdbi;

import griffon.annotations.core.Nonnull;
import griffon.annotations.inject.DependsOn;
import griffon.core.GriffonApplication;
import griffon.core.env.Metadata;
import griffon.core.events.StartupStartEvent;
import griffon.plugins.jdbi.JdbiCallback;
import griffon.plugins.jdbi.JdbiFactory;
import griffon.plugins.jdbi.JdbiHandler;
import griffon.plugins.jdbi.JdbiStorage;
import griffon.plugins.monitor.MBeanManager;
import org.codehaus.griffon.runtime.core.addon.AbstractGriffonAddon;
import org.codehaus.griffon.runtime.jdbi.monitor.JdbiStorageMonitor;
import org.skife.jdbi.v2.DBI;

import javax.application.event.EventHandler;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

import static griffon.util.ConfigUtils.getConfigValueAsBoolean;

/**
 * @author Andres Almiray
 */
@DependsOn("datasource")
@Named("jdbi")
public class JdbiAddon extends AbstractGriffonAddon {
    @Inject
    private JdbiHandler jdbiHandler;

    @Inject
    private JdbiFactory jdbiFactory;

    @Inject
    private JdbiStorage jdbiStorage;

    @Inject
    private MBeanManager mbeanManager;

    @Inject
    private Metadata metadata;

    @Override
    public void init(@Nonnull GriffonApplication application) {
        mbeanManager.registerMBean(new JdbiStorageMonitor(metadata, jdbiStorage));
    }

    @EventHandler
    public void handleStartupStartEvent(@Nonnull StartupStartEvent event) {
        for (String dataSourceName : jdbiFactory.getDatasourceNames()) {
            Map<String, Object> config = jdbiFactory.getConfigurationFor(dataSourceName);
            if (getConfigValueAsBoolean(config, "connect_on_startup", false)) {
                jdbiHandler.withJdbi(dataSourceName, new JdbiCallback<Void>() {
                    @Override
                    public Void handle(@Nonnull String dataSourceName, @Nonnull DBI dbi) {
                        return null;
                    }
                });
            }
        }
    }

    @Override
    public void onShutdown(@Nonnull GriffonApplication application) {
        for (String dataSourceName : jdbiFactory.getDatasourceNames()) {
            jdbiHandler.closeJdbi(dataSourceName);
        }
    }
}

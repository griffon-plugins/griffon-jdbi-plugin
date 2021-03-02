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
import griffon.annotations.core.Nullable;
import griffon.plugins.jdbi.JdbiCallback;
import griffon.plugins.jdbi.JdbiFactory;
import griffon.plugins.jdbi.JdbiHandler;
import griffon.plugins.jdbi.JdbiStorage;
import griffon.plugins.jdbi.exceptions.RuntimeJdbiException;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class DefaultJdbiHandler implements JdbiHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultJdbiHandler.class);
    private static final String ERROR_DATASOURCE_NAME_BLANK = "Argument 'datasourceName' must not be blank";
    private static final String ERROR_CALLBACK_NULL = "Argument 'callback' must not be null";

    private final JdbiFactory jdbiFactory;
    private final JdbiStorage jdbiStorage;

    @Inject
    public DefaultJdbiHandler(@Nonnull JdbiFactory jdbiFactory, @Nonnull JdbiStorage jdbiStorage) {
        this.jdbiFactory = requireNonNull(jdbiFactory, "Argument 'jdbiFactory' must not be null");
        this.jdbiStorage = requireNonNull(jdbiStorage, "Argument 'jdbiStorage' must not be null");
    }

    @Nullable
    @Override
    public <R> R withJdbi(@Nonnull JdbiCallback<R> callback) throws RuntimeJdbiException {
        return withJdbi(DefaultJdbiFactory.KEY_DEFAULT, callback);
    }

    @Nullable
    @Override
    public <R> R withJdbi(@Nonnull String datasourceName, @Nonnull JdbiCallback<R> callback) throws RuntimeJdbiException {
        requireNonBlank(datasourceName, ERROR_DATASOURCE_NAME_BLANK);
        requireNonNull(callback, ERROR_CALLBACK_NULL);
        DBI dbi = getDBI(datasourceName);
        try {
            LOG.debug("Executing statements on datasource '{}'", datasourceName);
            return callback.handle(datasourceName, dbi);
        } catch (Exception e) {
            throw new RuntimeJdbiException(datasourceName, e);
        }
    }

    @Override
    public void closeJdbi() {
        closeJdbi(DefaultJdbiFactory.KEY_DEFAULT);
    }

    @Override
    public void closeJdbi(@Nonnull String datasourceName) {
        DBI dbi = jdbiStorage.get(datasourceName);
        if (dbi != null) {
            jdbiFactory.destroy(datasourceName, dbi);
            jdbiStorage.remove(datasourceName);
        }
    }

    @Nonnull
    private DBI getDBI(@Nonnull String datasourceName) {
        DBI dbi = jdbiStorage.get(datasourceName);
        if (dbi == null) {
            dbi = jdbiFactory.create(datasourceName);
            jdbiStorage.set(datasourceName, dbi);
        }
        return dbi;
    }
}

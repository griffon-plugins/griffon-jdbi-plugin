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
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static griffon.util.GriffonNameUtils.requireNonBlank;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public class JMXAwareDBI extends DBIDecorator {
    private static final String ERROR_OBJECT_NAME_BLANK = "Argument 'objectName' must not be blank";
    private final Set<String> objectNames = new LinkedHashSet<>();

    public JMXAwareDBI(@Nonnull DataSource dataSource, @Nonnull DBI delegate) {
        super(dataSource, delegate);
    }

    public void addObjectName(@Nonnull String objectName) {
        objectNames.add(requireNonBlank(objectName, ERROR_OBJECT_NAME_BLANK));
    }

    public void removeObjectName(@Nonnull String objectName) {
        objectNames.remove(requireNonBlank(objectName, ERROR_OBJECT_NAME_BLANK));
    }

    @Nonnull
    public Set<String> getObjectNames() {
        return Collections.unmodifiableSet(objectNames);
    }

    public void clearObjectNames() {
        objectNames.clear();
    }
}

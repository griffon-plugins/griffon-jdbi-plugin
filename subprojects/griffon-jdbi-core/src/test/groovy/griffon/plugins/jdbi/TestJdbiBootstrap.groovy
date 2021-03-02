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
package griffon.plugins.jdbi

import griffon.annotations.core.Nonnull
import org.skife.jdbi.v2.DBI

class TestJdbiBootstrap implements JdbiBootstrap {
    boolean initWitness
    boolean destroyWitness

    @Override
    void init(@Nonnull String datasourceName, @Nonnull DBI dbi) {
        initWitness = true
    }

    @Override
    void destroy(@Nonnull String datasourceName, @Nonnull DBI dbi) {
        destroyWitness = true
    }
}

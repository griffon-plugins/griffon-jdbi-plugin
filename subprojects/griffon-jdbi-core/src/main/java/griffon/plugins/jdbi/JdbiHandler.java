/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.plugins.jdbi;

import griffon.plugins.jdbi.exceptions.RuntimeJdbiException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Andres Almiray
 */
public interface JdbiHandler {
    // tag::methods[]
    @Nullable
    <R> R withJdbi(@Nonnull JdbiCallback<R> callback)
        throws RuntimeJdbiException;

    @Nullable
    <R> R withJdbi(@Nonnull String datasourceName, @Nonnull JdbiCallback<R> callback)
        throws RuntimeJdbiException;

    void closeJdbi();

    void closeJdbi(@Nonnull String datasourceName);
    // end::methods[]
}
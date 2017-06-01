/*
 * Copyright 2014-2017 the original author or authors.
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
package org.codehaus.griffon.runtime.jdbi;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public class RecordingDBI extends DBIDecorator {
    private AtomicInteger handleCount = new AtomicInteger(0);

    public RecordingDBI(@Nonnull DataSource dataSource, @Nonnull DBI delegate) {
        super(dataSource, delegate);
    }

    public int increaseHandleCount() {
        return handleCount.incrementAndGet();
    }

    public int decreaseHandleCount() {
        return handleCount.decrementAndGet();
    }

    public int getHandleCount() {
        return handleCount.get();
    }

    @Override
    public Handle open() {
        Handle handle = super.open();
        increaseHandleCount();
        return wrap(handle);
    }

    @Nonnull
    private Handle wrap(@Nonnull Handle handle) {
        return handle instanceof LinkedHandle ? handle : new LinkedHandle(handle, this);
    }
}

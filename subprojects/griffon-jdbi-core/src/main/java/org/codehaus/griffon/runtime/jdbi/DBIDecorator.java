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
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.ResultSetMapperFactory;
import org.skife.jdbi.v2.TimingCollector;
import org.skife.jdbi.v2.TransactionCallback;
import org.skife.jdbi.v2.TransactionIsolationLevel;
import org.skife.jdbi.v2.exceptions.CallbackFailedException;
import org.skife.jdbi.v2.tweak.ArgumentFactory;
import org.skife.jdbi.v2.tweak.ContainerFactory;
import org.skife.jdbi.v2.tweak.HandleCallback;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.skife.jdbi.v2.tweak.SQLLog;
import org.skife.jdbi.v2.tweak.StatementBuilderFactory;
import org.skife.jdbi.v2.tweak.StatementLocator;
import org.skife.jdbi.v2.tweak.StatementRewriter;
import org.skife.jdbi.v2.tweak.TransactionHandler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public class DBIDecorator extends DBI {
    private final DBI delegate;

    public DBIDecorator(@Nonnull DataSource dataSource, @Nonnull DBI delegate) {
        super(requireNonNull(dataSource, "Argument 'dataSource' must not be null"));
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    protected DBI getDelegate() {
        return delegate;
    }

    @Override
    public void setStatementLocator(StatementLocator locator) {
        delegate.setStatementLocator(locator);
    }

    @Override
    public StatementLocator getStatementLocator() {
        return delegate.getStatementLocator();
    }

    @Override
    public void setStatementRewriter(StatementRewriter rewriter) {
        delegate.setStatementRewriter(rewriter);
    }

    @Override
    public StatementRewriter getStatementRewriter() {
        return delegate.getStatementRewriter();
    }

    @Override
    public void setTransactionHandler(TransactionHandler handler) {
        delegate.setTransactionHandler(handler);
    }

    @Override
    public TransactionHandler getTransactionHandler() {
        return delegate.getTransactionHandler();
    }

    @Override
    public Handle open() {
        return delegate.open();
    }

    @Override
    public void registerMapper(ResultSetMapper mapper) {
        delegate.registerMapper(mapper);
    }

    @Override
    public void registerMapper(ResultSetMapperFactory factory) {
        delegate.registerMapper(factory);
    }

    @Override
    public void define(String key, Object value) {
        delegate.define(key, value);
    }

    @Override
    public <ReturnType> ReturnType withHandle(HandleCallback<ReturnType> callback) throws CallbackFailedException {
        return delegate.withHandle(callback);
    }

    @Override
    public <ReturnType> ReturnType inTransaction(TransactionCallback<ReturnType> callback) throws CallbackFailedException {
        return delegate.inTransaction(callback);
    }

    @Override
    public <ReturnType> ReturnType inTransaction(TransactionIsolationLevel isolation, TransactionCallback<ReturnType> callback) throws CallbackFailedException {
        return delegate.inTransaction(isolation, callback);
    }

    @Override
    public <SqlObjectType> SqlObjectType open(Class<SqlObjectType> sqlObjectType) {
        return delegate.open(sqlObjectType);
    }

    @Override
    public <SqlObjectType> SqlObjectType onDemand(Class<SqlObjectType> sqlObjectType) {
        return delegate.onDemand(sqlObjectType);
    }

    @Override
    public void close(Object sqlObject) {
        delegate.close(sqlObject);
    }

    public static Handle open(DataSource dataSource) {
        return DBI.open(dataSource);
    }

    public static Handle open(Connection connection) {
        return DBI.open(connection);
    }

    public static Handle open(String url) {
        return DBI.open(url);
    }

    public static Handle open(String url, String username, String password) {
        return DBI.open(url, username, password);
    }

    public static Handle open(String url, Properties props) {
        return DBI.open(url, props);
    }

    @Override
    public void setStatementBuilderFactory(StatementBuilderFactory factory) {
        delegate.setStatementBuilderFactory(factory);
    }

    @Override
    public StatementBuilderFactory getStatementBuilderFactory() {
        return delegate.getStatementBuilderFactory();
    }

    @Override
    public void setSQLLog(SQLLog log) {
        delegate.setSQLLog(log);
    }

    @Override
    public SQLLog getSQLLog() {
        return delegate.getSQLLog();
    }

    @Override
    public void setTimingCollector(TimingCollector timingCollector) {
        delegate.setTimingCollector(timingCollector);
    }

    @Override
    public TimingCollector getTimingCollector() {
        return delegate.getTimingCollector();
    }

    @Override
    public void registerArgumentFactory(ArgumentFactory<?> argumentFactory) {
        delegate.registerArgumentFactory(argumentFactory);
    }

    @Override
    public void registerContainerFactory(ContainerFactory<?> factory) {
        delegate.registerContainerFactory(factory);
    }
}

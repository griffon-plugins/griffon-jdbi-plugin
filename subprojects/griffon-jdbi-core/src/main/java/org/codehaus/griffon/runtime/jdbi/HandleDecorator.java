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
import org.skife.jdbi.v2.Batch;
import org.skife.jdbi.v2.Call;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.PreparedBatch;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.ResultColumnMapperFactory;
import org.skife.jdbi.v2.ResultSetMapperFactory;
import org.skife.jdbi.v2.Script;
import org.skife.jdbi.v2.SqlObjectContext;
import org.skife.jdbi.v2.TimingCollector;
import org.skife.jdbi.v2.TransactionCallback;
import org.skife.jdbi.v2.TransactionConsumer;
import org.skife.jdbi.v2.TransactionIsolationLevel;
import org.skife.jdbi.v2.Update;
import org.skife.jdbi.v2.exceptions.TransactionFailedException;
import org.skife.jdbi.v2.tweak.ArgumentFactory;
import org.skife.jdbi.v2.tweak.ContainerFactory;
import org.skife.jdbi.v2.tweak.ResultColumnMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.skife.jdbi.v2.tweak.SQLLog;
import org.skife.jdbi.v2.tweak.StatementBuilder;
import org.skife.jdbi.v2.tweak.StatementLocator;
import org.skife.jdbi.v2.tweak.StatementRewriter;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public class HandleDecorator implements Handle {
    private final Handle delegate;

    public HandleDecorator(@Nonnull Handle delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    protected Handle getDelegate() {
        return delegate;
    }

    @Override
    public Connection getConnection() {
        return delegate.getConnection();
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public boolean isClosed() {
        return delegate.isClosed();
    }

    @Override
    public void define(String key, Object value) {
        delegate.define(key, value);
    }

    @Override
    public Handle begin() {
        return delegate.begin();
    }

    @Override
    public Handle commit() {
        return delegate.commit();
    }

    @Override
    public Handle rollback() {
        return delegate.rollback();
    }

    @Override
    public Handle rollback(String checkpointName) {
        return delegate.rollback(checkpointName);
    }

    @Override
    public boolean isInTransaction() {
        return delegate.isInTransaction();
    }

    @Override
    public Query<Map<String, Object>> createQuery(String sql) {
        return delegate.createQuery(sql);
    }

    @Override
    public Update createStatement(String sql) {
        return delegate.createStatement(sql);
    }

    @Override
    public Call createCall(String callableSql) {
        return delegate.createCall(callableSql);
    }

    @Override
    public int insert(String sql, Object... args) {
        return delegate.insert(sql, args);
    }

    @Override
    public int update(String sql, Object... args) {
        return delegate.update(sql, args);
    }

    @Override
    public PreparedBatch prepareBatch(String sql) {
        return delegate.prepareBatch(sql);
    }

    @Override
    public Batch createBatch() {
        return delegate.createBatch();
    }

    @Override
    public <ReturnType> ReturnType inTransaction(TransactionCallback<ReturnType> callback) throws TransactionFailedException {
        return delegate.inTransaction(callback);
    }

    @Override
    public void useTransaction(TransactionConsumer callback) throws TransactionFailedException {
        delegate.useTransaction(callback);
    }

    @Override
    public <ReturnType> ReturnType inTransaction(TransactionIsolationLevel level, TransactionCallback<ReturnType> callback) throws TransactionFailedException {
        return delegate.inTransaction(level, callback);
    }

    @Override
    public void useTransaction(TransactionIsolationLevel level, TransactionConsumer callback) throws TransactionFailedException {
        delegate.useTransaction(level, callback);
    }

    @Override
    public List<Map<String, Object>> select(String sql, Object... args) {
        return delegate.select(sql, args);
    }

    @Override
    public void setStatementLocator(StatementLocator locator) {
        delegate.setStatementLocator(locator);
    }

    @Override
    public void setStatementRewriter(StatementRewriter rewriter) {
        delegate.setStatementRewriter(rewriter);
    }

    @Override
    public Script createScript(String name) {
        return delegate.createScript(name);
    }

    @Override
    public void execute(String sql, Object... args) {
        delegate.execute(sql, args);
    }

    @Override
    public Handle checkpoint(String name) {
        return delegate.checkpoint(name);
    }

    @Override
    public Handle release(String checkpointName) {
        return delegate.release(checkpointName);
    }

    @Override
    public void setStatementBuilder(StatementBuilder builder) {
        delegate.setStatementBuilder(builder);
    }

    @Override
    public void setSQLLog(SQLLog log) {
        delegate.setSQLLog(log);
    }

    @Override
    public void setTimingCollector(TimingCollector timingCollector) {
        delegate.setTimingCollector(timingCollector);
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
    public void registerColumnMapper(ResultColumnMapper mapper) {
        delegate.registerColumnMapper(mapper);
    }

    @Override
    public void registerColumnMapper(ResultColumnMapperFactory factory) {
        delegate.registerColumnMapper(factory);
    }

    @Override
    public <SqlObjectType> SqlObjectType attach(Class<SqlObjectType> sqlObjectType) {
        return delegate.attach(sqlObjectType);
    }

    @Override
    public void setTransactionIsolation(TransactionIsolationLevel level) {
        delegate.setTransactionIsolation(level);
    }

    @Override
    public void setTransactionIsolation(int level) {
        delegate.setTransactionIsolation(level);
    }

    @Override
    public TransactionIsolationLevel getTransactionIsolationLevel() {
        return delegate.getTransactionIsolationLevel();
    }

    @Override
    public void registerArgumentFactory(ArgumentFactory argumentFactory) {
        delegate.registerArgumentFactory(argumentFactory);
    }

    @Override
    public void registerContainerFactory(ContainerFactory<?> factory) {
        delegate.registerContainerFactory(factory);
    }

    @Override
    public void setSqlObjectContext(SqlObjectContext context) {
        delegate.setSqlObjectContext(context);
    }

    @Override
    public SqlObjectContext getSqlObjectContext() {
        return delegate.getSqlObjectContext();
    }
}

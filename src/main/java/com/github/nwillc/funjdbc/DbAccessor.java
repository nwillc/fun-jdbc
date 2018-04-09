/*
 * Copyright (c) 2018, nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 */

package com.github.nwillc.funjdbc;

import com.github.nwillc.funjdbc.functions.ConnectionProvider;
import com.github.nwillc.funjdbc.functions.Enricher;
import com.github.nwillc.funjdbc.functions.Extractor;
import com.github.nwillc.funjdbc.functions.ThrowingFunction;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.github.nwillc.funjdbc.utils.Closer.close;

/**
 * Interface, with default methods providing JDBC database access functionality.
 */
public interface DbAccessor extends ConnectionProvider {

    /**
     * Extract results from a SQL query designed to return multiple results. The SQL, and it's optional args
     * are formatted with String.format(String, Object ...) method. Note, Streams are Closeable, and the
     * resultant Stream should be closed when completed to insure database resources involved in the stream are freed.
     *
     * @param extractor    The extractor to process the ResultSet with
     * @param sqlStatement The SQL statement
     * @param <T>          Type extracted and returned in the stream
     * @return a stream of the extracted elements
     * @throws SQLException if the query or an extraction fails
     */
    default <T> Stream<T> dbQuery(final Extractor<T> extractor, final SqlStatement sqlStatement) throws SQLException {
        return stream(extractor, s -> s.executeQuery(sqlStatement.toString()));
    }

    /**
     * Given a map of entities, and a query that extracts details about them, then execute that query
     * and enrich the entities with the results.
     *
     * @param map          A map of entities the enrich
     * @param keyExtractor an Extractor to get the entity key from the detail records
     * @param enricher     a function to enrich an entity from the detail record
     * @param sqlStatement The SQL statement
     * @param <K>          the key type
     * @param <V>          the entity type
     * @throws SQLException may result from the query or enrichments
     */
    default <K, V> void dbEnrich(Map<K, V> map,
                                 final Extractor<K> keyExtractor, final Enricher<V> enricher,
                                 final SqlStatement sqlStatement) throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlStatement.toString())) {
            while (resultSet.next()) {
                K key = keyExtractor.extract(resultSet);
                if (key != null) {
                    V value = map.get(key);
                    if (value != null) {
                        enricher.accept(value, resultSet);
                    }
                }
            }
        }
    }

    /**
     * Extract the result from a SQL query which returns at most one result. The SQL, and it's optional args
     * are formatted with String.format(String, Object ...) method.
     *
     * @param extractor    the extractor to extract teh result
     * @param sqlStatement the SQL statement
     * @param <T>          Type extracted and optionally returned
     * @return an Optional of the data
     * @throws SQLException if the query or extraction fails, or if multiple rows returned
     */
    default <T> Optional<T> dbFind(final Extractor<T> extractor, final SqlStatement sqlStatement) throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlStatement.toString())) {

            if (!resultSet.next()) {
                return Optional.empty();
            }

            final T result = extractor.extract(resultSet);

            if (resultSet.next()) {
                throw new SQLException("Query to find single row returned multiple.");
            }

            return Optional.of(result);
        }
    }

    /**
     * Execute a SQL update or delete. The SQL, and it's optional args
     * are formatted with String.format(String, Object ...) method.
     *
     * @param sqlStatement The SQL statement
     * @return the count of rows updated.
     * @throws SQLException if the update fails
     */
    default int dbUpdate(SqlStatement sqlStatement) throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sqlStatement.toString());
        }
    }

    /**
     * Execute an insert into a table with an auto incremented key, returning a stream of the keys.
     *
     * @param <T>          key type of generated keys for each tuple.
     * @param sqlStatement The SQL statement
     * @return the stream of keys generated
     * @throws SQLException if the insert failed
     * @since 0.13.0
     */
    default <T> Stream<T> dbInsertGetGeneratedKeys(Extractor<T> keyExtractor, SqlStatement sqlStatement) throws SQLException {
        return stream(keyExtractor, s -> {
            s.executeUpdate(sqlStatement.toString(), Statement.RETURN_GENERATED_KEYS);
            return s.getGeneratedKeys();
        });
    }

    /**
     * Execute a SQL statement.
     *
     * @param sqlStatement The SQL statement
     * @return <code>true</code> if the first result is a <code>ResultSet</code>
     * object; <code>false</code> if it is an update count or there are
     * no results
     * @throws SQLException if the SQL is invalid
     * @since 0.9.3
     */
    default boolean dbExecute(SqlStatement sqlStatement) throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            return statement.execute(sqlStatement.toString());
        }
    }

    /**
     * Given an Extractor and ResultSet return a Stream of results. Note, Streams are Closeable, and the
     * resultant Stream should be closed when completed to insure database resources involved in the stream are freed.
     *
     * @param <T>       the type parameter
     * @param extractor the extractor
     * @param resultSet the result set
     * @return the stream
     */
    default <T> Stream<T> stream(final Extractor<T> extractor, final ResultSet resultSet) {
        ResultSetIterator<T> iterator = new ResultSetIterator<>(resultSet, extractor);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED | Spliterator.NONNULL), false)
                .onClose(() -> close(resultSet));
    }

    /**
     * Return the results of a sql execution as a stream of an extracted type.
     * @param extractor  Function to extract data from the ResultSet
     * @param execution  Given a Statement, execute it returning a ResultSet
     * @param <T> Type of the elements in the resultant Stream
     * @return A Stream of type T
     * @throws SQLException Should the execution have issues.
     * @since 0.13.1
     */
    default <T> Stream<T> stream(final Extractor<T> extractor, ThrowingFunction<Statement, ResultSet> execution) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        // Can not try-with-resources here because if we return the stream we don't want to close
        try {
            final Connection c = getConnection();
            connection = c;
            final Statement s = connection.createStatement();
            statement = s;
            final ResultSet rs = execution.apply(s);
            resultSet = rs;
            return stream(extractor, resultSet).onClose(() -> {
                close(s);
                close(c);
            });
        } catch (Exception e) {
            close(statement);
            close(connection);
            close(resultSet);
            throw new SQLException("Query failed", e);
        }
    }
}

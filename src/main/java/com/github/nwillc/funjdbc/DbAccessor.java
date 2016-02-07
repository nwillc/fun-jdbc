/*
 * Copyright (c) 2016, nwillc@gmail.com
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
 */

package com.github.nwillc.funjdbc;

import com.github.nwillc.funjdbc.functions.ConnectionProvider;
import com.github.nwillc.funjdbc.functions.Enricher;
import com.github.nwillc.funjdbc.functions.Extractor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterators;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.github.nwillc.funjdbc.utils.Closer.close;

/**
 * Interface, with default methods providing JDBC database access functionality.
 */
public interface DbAccessor extends ConnectionProvider {
    Logger LOGGER = Logger.getLogger(DbAccessor.class.getName());


    /**
     * Indicate if the formatted SQL should be logged. Your implementer can override this
     * to turn on logging.
     * @return false - do not log.
     */
    default boolean logSql() { return false; }

    /**
     * Extract results from a SQL query designed to return multiple results. The SQL, and it's optional args
     * are formatted with String.format(String, Object ...) method. Note, Streams are Closeable, and the
     * resultant Stream should be closed when completed to insure database resources involved in the stream are freed.
     *
     * @param extractor The extractor to process the ResultSet with
     * @param sql       The SQL being used
     * @param args      If present, used as arguments in sql = String.format(sql,args)
     * @param <T>       Type extracted and returned in the stream
     * @return a stream of the extracted elements
     * @throws SQLException if the query or an extraction fails
     */
    default <T> Stream<T> dbQuery(final Extractor<T> extractor, final String sql, Object... args) throws SQLException {
        final String formattedSql = formatSql(sql, args);
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(formattedSql);
        return stream(extractor, resultSet).onClose(() -> {
            close(statement);
            close(connection);
        });
    }

	/**
	 * Given a map of entities, and a query that extracts details about them, then execute that query
	 * and enrich the entities with the results.
	 * @param map  A map of entities the enrich
	 * @param keyExtractor an Extractor to get the entity key from the detail records
	 * @param enricher  a function to enrich an entity from the detail record
	 * @param sql the SQL to generate details - each row must contain an entity key
	 * @param args the SQL arguements to sql
	 * @param <K>  the key type
	 * @param <V>  the entity type
	 * @throws SQLException
	 */
	default <K,V> void dbEnrich(Map<K,V> map,
								final Extractor<K> keyExtractor, final Enricher<V> enricher,
								final String sql, Object... args) throws SQLException {
		final String formattedSql = formatSql(sql, args);
		try (Connection connection = getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(formattedSql)) {
			if (resultSet != null) {
				while (resultSet.next()) {
					K key = keyExtractor.extract(resultSet);
					if (key != null) {
						V value = map.get(key);
						if (value != null) {
							enricher.enrich(value, resultSet);
						}
					}
				}
			}
		}
	}

    /**
     * Given an Extractor and ResultSet return a Stream of results. Note, Streams are Closeable, and the
     * resultant Stream should be closed when completed to insure database resources involved in the stream are freed.
     *
     * @param <T>  the type parameter
     * @param extractor the extractor
     * @param resultSet the result set
     * @return the stream
     */
    default <T> Stream<T> stream(final Extractor<T> extractor, final ResultSet resultSet) {
        ResultSetIterator<T> iterator = new ResultSetIterator<>(resultSet, extractor);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false)
                .onClose(() -> close(resultSet));
    }

    /**
     * Extract the result from a SQL query which returns at most one result. The SQL, and it's optional args
     * are formatted with String.format(String, Object ...) method.
     *
     * @param extractor the extractor to extract teh result
     * @param sql       The SQL to execute
     * @param args      If present, used as arguments in sql = String.format(sql,args)
     * @param <T>       Type extracted and optionally returned
     * @return an Optional of the data
     * @throws SQLException if the query or extraction fails, or if multiple rows returned
     */
    default <T> Optional<T> dbFind(final Extractor<T> extractor, final String sql, final Object... args) throws SQLException {
        final String formattedSql = formatSql(sql, args);
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(formattedSql)) {
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
     * @param sql  The SQL.
     * @param args any optional arguments
     * @return the count of rows updated.
     * @throws SQLException if the update fails
     */
    default int dbUpdate(final String sql, final Object... args) throws SQLException {
        final String formattedSql = formatSql(sql, args);
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            return statement.executeUpdate(formattedSql);
        }
    }

    /**
     * Format the a SQL statement using String.format(). Will log the results based on logSql().
     * @param sql The string
     * @param args the args
     * @return the formatted sql.
     */
    default String formatSql(final String sql, final Object ... args) {
        final String formattedSql = String.format(sql, args);
        if (logSql()) {
            LOGGER.info(formattedSql);
        }
        return formattedSql;
    }

}

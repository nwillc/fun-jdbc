/*
 * Copyright (c) 2014,  nwillc@gmail.com
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static almost.functional.utils.LogFactory.getLogger;

public interface DbAccessor {

    /**
     * Method by which we return the database connection.
     * @return A valid database connection
     * @throws SQLException if a connection can not be returned
     */
    Connection getConnection() throws SQLException;

    /**
     * Extract results from a query designed to return multiple results.
     * @param sql The SQL being used
     * @param extractor The extractor to process the ResultSet with
     * @param <T> The type of the elements to extract
     * @return a stream of the extracted elements
     * @throws SQLException if the query or an extraction fails
     */
    default <T> Stream<T> query(final String sql, final Extractor<T> extractor) throws SQLException {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        ResultSetIterator<T> iterator = new ResultSetIterator<>(resultSet, extractor);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false)
                .onClose(() -> {
                    try {
                        resultSet.close();
                        statement.close();
                        connection.close();
                    } catch (Exception e) {
                        getLogger().info("Exception closing iterator: " + e);
                    }
                });
    }

    /**
     * Extract result from a query designed to return at most one result.
     * @param sql The SQL to execute
     * @param extractor the extractor to extract teh result
     * @param <T> the type the extractor will return
     * @return an Optional of the data
     * @throws SQLException if the query or extraction fails, or if multiple rows returned
     */
    default <T> Optional<T> find(final String sql, final Extractor<T> extractor) throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
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
}

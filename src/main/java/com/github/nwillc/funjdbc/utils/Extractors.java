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
 *
 */

package com.github.nwillc.funjdbc.utils;

import com.github.nwillc.funjdbc.UncheckedSQLException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.function.BiFunction;

/**
 * These extractors should roughly parallel the SQLType enum, if we didn't want specific type information for each
 * a EnumMap would have been a better solution.
 */
public final class Extractors {
    private Extractors() {}

    /**
     * An extractor based on {@link java.sql.ResultSet#getString(int)}.
     */
    public static final BiFunction<ResultSet, Integer, String> STRING = (r, i) -> {
        try {
            return r.getString(i);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };
    /**
     * An extractor based on {@link java.sql.ResultSet#getInt(int)}
     */
    public static final BiFunction<ResultSet, Integer, Integer> INTEGER = (r, i) -> {
        try {
            return r.getInt(i);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };
    /**
     * An extractor based on {@link java.sql.ResultSet#getBoolean(int)}
     */
    public static final BiFunction<ResultSet, Integer, Boolean> BOOLEAN = (r, i) -> {
        try {
            return r.getBoolean(i);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };
    /**
     * An extractor based on {@link java.sql.ResultSet#getLong(int)}
     */
    public static final BiFunction<ResultSet, Integer, Long> LONG = (r, i) -> {
        try {
            return r.getLong(i);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };
    /**
     * An extractor based on {@link java.sql.ResultSet#getDouble(int)}
     */
    public static final BiFunction<ResultSet, Integer, Double> DOUBLE = (r, i) -> {
        try {
            return r.getDouble(i);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };
    /**
     * An extractor based on {@link java.sql.ResultSet#getFloat(int)}
     */
    public static final BiFunction<ResultSet, Integer, Float> FLOAT = (r, i) -> {
        try {
            return r.getFloat(i);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };
    /**
     * An extractor based on {@link java.sql.ResultSet#getBigDecimal(int)}
     */
    public static final BiFunction<ResultSet, Integer, BigDecimal> BIG_DECIMAL = (r, i) -> {
        try {
            return r.getBigDecimal(i);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };
    /**
     * An extractor based on {@link java.sql.ResultSet#getTime(int)}
     */
    public static final BiFunction<ResultSet, Integer, Time> TIME = (r, i) -> {
        try {
            return r.getTime(i);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };
    /**
     * An extractor based on {@link java.sql.ResultSet#getDate(int)}
     */
    public static final BiFunction<ResultSet, Integer, Date> DATE = (r, i) -> {
        try {
            return r.getDate(i);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };
    /**
     * An extractor based on {@link java.sql.ResultSet#getTimestamp(int)}
     */
    public static final BiFunction<ResultSet, Integer, Timestamp> TIMESTAMP = (r, i) -> {
        try {
            return r.getTimestamp(i);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };
}

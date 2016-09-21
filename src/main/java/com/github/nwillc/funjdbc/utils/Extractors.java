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

// TODO: Maybe this should be an enumeration?

/**
 * These extractors should roughly parallel the SQLType enum, if we didn't want specific type information for each
 * a EnumMap would have been a better solution.
 */
public final class Extractors {
    private Extractors() {}

    /**
     * An extractor based on {@link java.sql.ResultSet#getString(int)}.
     */
    public static final BiFunction<ResultSet, Integer, String> STRING_I = (r, i) -> {
        try {
            return r.getString(i);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };

    /**
     * An extractor based on {@link java.sql.ResultSet#getString(String)}.
     */
    public static final BiFunction<ResultSet, String, String> STRING_S = (r, s) -> {
        try {
            return r.getString(s);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };

    /**
     * An extractor based on {@link java.sql.ResultSet#getInt(int)}
     */
    public static final BiFunction<ResultSet, Integer, Integer> INTEGER_I = (r, i) -> {
        try {
            return r.getInt(i);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };

    /**
     * An extractor based on {@link java.sql.ResultSet#getInt(String)}
     */
    public static final BiFunction<ResultSet, String, Integer> INTEGER_S = (r, s) -> {
        try {
            return r.getInt(s);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };

    /**
     * An extractor based on {@link java.sql.ResultSet#getBoolean(int)}
     */
    public static final BiFunction<ResultSet, Integer, Boolean> BOOLEAN_I = (r, i) -> {
        try {
            return r.getBoolean(i);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };

    /**
     * An extractor based on {@link java.sql.ResultSet#getBoolean(String)}
     */
    public static final BiFunction<ResultSet, String, Boolean> BOOLEAN_S = (r, s) -> {
        try {
            return r.getBoolean(s);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };

    /**
     * An extractor based on {@link java.sql.ResultSet#getLong(int)}
     */
    public static final BiFunction<ResultSet, Integer, Long> LONG_I = (r, i) -> {
        try {
            return r.getLong(i);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };

    /**
     * An extractor based on {@link java.sql.ResultSet#getLong(String)}
     */
    public static final BiFunction<ResultSet, String, Long> LONG_S = (r, s) -> {
        try {
            return r.getLong(s);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };

    /**
     * An extractor based on {@link java.sql.ResultSet#getDouble(int)}
     */
    public static final BiFunction<ResultSet, Integer, Double> DOUBLE_I = (r, i) -> {
        try {
            return r.getDouble(i);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };

    /**
     * An extractor based on {@link java.sql.ResultSet#getDouble(String)}
     */
    public static final BiFunction<ResultSet, String, Double> DOUBLE_S = (r, s) -> {
        try {
            return r.getDouble(s);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };

    /**
     * An extractor based on {@link java.sql.ResultSet#getFloat(int)}
     */
    public static final BiFunction<ResultSet, Integer, Float> FLOAT_I = (r, i) -> {
        try {
            return r.getFloat(i);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };

    /**
     * An extractor based on {@link java.sql.ResultSet#getFloat(String)}
     */
    public static final BiFunction<ResultSet, String, Float> FLOAT_S = (r, s) -> {
        try {
            return r.getFloat(s);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };

    /**
     * An extractor based on {@link java.sql.ResultSet#getBigDecimal(int)}
     */
    public static final BiFunction<ResultSet, Integer, BigDecimal> BIG_DECIMAL_I = (r, i) -> {
        try {
            return r.getBigDecimal(i);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };

    /**
     * An extractor based on {@link java.sql.ResultSet#getBigDecimal(String)}
     */
    public static final BiFunction<ResultSet, String, BigDecimal> BIG_DECIMAL_S = (r, s) -> {
        try {
            return r.getBigDecimal(s);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };

    /**
     * An extractor based on {@link java.sql.ResultSet#getTime(int)}
     */
    public static final BiFunction<ResultSet, Integer, Time> TIME_I = (r, i) -> {
        try {
            return r.getTime(i);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };

    /**
     * An extractor based on {@link java.sql.ResultSet#getTime(String)}
     */
    public static final BiFunction<ResultSet, String, Time> TIME_S = (r, s) -> {
        try {
            return r.getTime(s);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };

    /**
     * An extractor based on {@link java.sql.ResultSet#getDate(int)}
     */
    public static final BiFunction<ResultSet, Integer, Date> DATE_I = (r, i) -> {
        try {
            return r.getDate(i);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };

    /**
     * An extractor based on {@link java.sql.ResultSet#getDate(String)}
     */
    public static final BiFunction<ResultSet, String, Date> DATE_S = (r, s) -> {
        try {
            return r.getDate(s);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };

    /**
     * An extractor based on {@link java.sql.ResultSet#getTimestamp(int)}
     */
    public static final BiFunction<ResultSet, Integer, Timestamp> TIMESTAMP_I = (r, i) -> {
        try {
            return r.getTimestamp(i);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };

    /**
     * An extractor based on {@link java.sql.ResultSet#getTimestamp(String)}
     */
    public static final BiFunction<ResultSet, String, Timestamp> TIMESTAMP_S = (r, s) -> {
        try {
            return r.getTimestamp(s);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };
}

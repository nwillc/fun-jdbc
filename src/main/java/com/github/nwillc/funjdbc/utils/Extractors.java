package com.github.nwillc.funjdbc.utils;

import com.github.nwillc.funjdbc.UncheckedSQLException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiFunction;

/**
 * These extractors should parallel the SQLType enum somewhat, if we didn't want specific type information for each
 * a EnumMap would have been a better solution.
 */
public class Extractors {
    public static final BiFunction<ResultSet, Integer, String> STRING = (r, i) -> {
        try {
            return r.getString(i);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };
    public static final BiFunction<ResultSet, Integer, Integer> INTEGER = (r, i) -> {
        try {
            return r.getInt(i);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };
    public static final BiFunction<ResultSet, Integer, Boolean> BOOLEAN = (r, i) -> {
        try {
            return r.getBoolean(i);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    };
    // TIME
    // DATE
    // timestamp
    // BIGDECIMAL
    // long
    // float
    // double
}

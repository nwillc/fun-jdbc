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

import com.github.nwillc.funjdbc.functions.ThrowingBiFunction;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * These extractors should roughly parallel the SQLType enum, if we didn't want specific type information for each
 * a EnumMap would have been a better solution.
 */
public final class Extractors {
	private Extractors() {
	}

	/**
	 * An extractor based on {@link java.sql.ResultSet#getString(int)}.
	 */
	public static final ThrowingBiFunction<ResultSet, Integer, String> STRING_I = ResultSet::getString;

	/**
	 * An extractor based on {@link java.sql.ResultSet#getString(String)}.
	 */
	public static final ThrowingBiFunction<ResultSet, String, String> STRING_S = ResultSet::getString;

	/**
	 * An extractor based on {@link java.sql.ResultSet#getInt(int)}
	 */
	public static final ThrowingBiFunction<ResultSet, Integer, Integer> INTEGER_I = ResultSet::getInt;

	/**
	 * An extractor based on {@link java.sql.ResultSet#getInt(String)}
	 */
	public static final ThrowingBiFunction<ResultSet, String, Integer> INTEGER_S = ResultSet::getInt;

	/**
	 * An extractor based on {@link java.sql.ResultSet#getBoolean(int)}
	 */
	public static final ThrowingBiFunction<ResultSet, Integer, Boolean> BOOLEAN_I = ResultSet::getBoolean;

	/**
	 * An extractor based on {@link java.sql.ResultSet#getBoolean(String)}
	 */
	public static final ThrowingBiFunction<ResultSet, String, Boolean> BOOLEAN_S = ResultSet::getBoolean;
	/**
	 * An extractor based on {@link java.sql.ResultSet#getLong(int)}
	 */
	public static final ThrowingBiFunction<ResultSet, Integer, Long> LONG_I = ResultSet::getLong;

	/**
	 * An extractor based on {@link java.sql.ResultSet#getLong(String)}
	 */
	public static final ThrowingBiFunction<ResultSet, String, Long> LONG_S = ResultSet::getLong;

	/**
	 * An extractor based on {@link java.sql.ResultSet#getDouble(int)}
	 */
	public static final ThrowingBiFunction<ResultSet, Integer, Double> DOUBLE_I = ResultSet::getDouble;

	/**
	 * An extractor based on {@link java.sql.ResultSet#getDouble(String)}
	 */
	public static final ThrowingBiFunction<ResultSet, String, Double> DOUBLE_S = ResultSet::getDouble;

	/**
	 * An extractor based on {@link java.sql.ResultSet#getFloat(int)}
	 */
	public static final ThrowingBiFunction<ResultSet, Integer, Float> FLOAT_I = ResultSet::getFloat;

	/**
	 * An extractor based on {@link java.sql.ResultSet#getFloat(String)}
	 */
	public static final ThrowingBiFunction<ResultSet, String, Float> FLOAT_S = ResultSet::getFloat;

	/**
	 * An extractor based on {@link java.sql.ResultSet#getBigDecimal(int)}
	 */
	public static final ThrowingBiFunction<ResultSet, Integer, BigDecimal> BIG_DECIMAL_I = ResultSet::getBigDecimal;

	/**
	 * An extractor based on {@link java.sql.ResultSet#getBigDecimal(String)}
	 */
	public static final ThrowingBiFunction<ResultSet, String, BigDecimal> BIG_DECIMAL_S = ResultSet::getBigDecimal;

	/**
	 * An extractor based on {@link java.sql.ResultSet#getTime(int)}
	 */
	public static final ThrowingBiFunction<ResultSet, Integer, Time> TIME_I = ResultSet::getTime;

	/**
	 * An extractor based on {@link java.sql.ResultSet#getTime(String)}
	 */
	public static final ThrowingBiFunction<ResultSet, String, Time> TIME_S = ResultSet::getTime;

	/**
	 * An extractor based on {@link java.sql.ResultSet#getDate(int)}
	 */
	public static final ThrowingBiFunction<ResultSet, Integer, Date> DATE_I = ResultSet::getDate;

	/**
	 * An extractor based on {@link java.sql.ResultSet#getDate(String)}
	 */
	public static final ThrowingBiFunction<ResultSet, String, Date> DATE_S = ResultSet::getDate;

	/**
	 * An extractor based on {@link java.sql.ResultSet#getTimestamp(int)}
	 */
	public static final ThrowingBiFunction<ResultSet, Integer, Timestamp> TIMESTAMP_I = ResultSet::getTimestamp;

	/**
	 * An extractor based on {@link java.sql.ResultSet#getTimestamp(String)}
	 */
	public static final ThrowingBiFunction<ResultSet, String, Timestamp> TIMESTAMP_S = ResultSet::getTimestamp;
}

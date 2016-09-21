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
import com.github.nwillc.funjdbc.functions.Extractor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


public class ExtractorFactoryTest {
	private ExtractorFactory<Bean> factory;
	@Mock
	ResultSet resultSet;
	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Before
	public void setUp() throws Exception {
		factory = new ExtractorFactory<>();
	}

	@Test
	public void testConstructor() throws Exception {
		final Extractor<Bean> extractor = factory.factory(Bean::new).getExtractor();
		assertThat(extractor).isNotNull();
		final Bean bean = extractor.extract(null);
		assertThat(bean).isInstanceOf(Bean.class);
	}

	@Test
	public void testInteger() throws Exception {
		final Extractor<Bean> extractor = factory.add(Bean::setOne, Extractors.INTEGER, 1).factory(Bean::new).getExtractor();

		when(resultSet.getInt(1)).thenReturn(5);

		final Bean bean = extractor.extract(resultSet);
		assertThat(bean.one).isEqualTo(5);
	}

	@Test(expected = UncheckedSQLException.class)
	public void testIntegerException() throws Exception {
		final Extractor<Bean> extractor = factory.add(Bean::setOne, Extractors.INTEGER, 1).factory(Bean::new).getExtractor();

		when(resultSet.getInt(1)).thenThrow(SQLException.class);

		extractor.extract(resultSet);
	}

	@Test
	public void testString() throws Exception {
		final Extractor<Bean> extractor = factory.add(Bean::setTwo, Extractors.STRING, 1).factory(Bean::new).getExtractor();

		when(resultSet.getString(1)).thenReturn("two");

		final Bean bean = extractor.extract(resultSet);
		assertThat(bean.two).isEqualTo("two");
	}

	@Test
	public void testStringColumn() throws Exception {
		final Extractor<Bean> extractor = factory.add(Bean::setTwo, Extractors.STRING_S, "word").factory(Bean::new).getExtractor();

		when(resultSet.getString("word")).thenReturn("two");

		final Bean bean = extractor.extract(resultSet);
		assertThat(bean.two).isEqualTo("two");
	}

	@Test(expected = UncheckedSQLException.class)
	public void testStringException() throws Exception {
		final Extractor<Bean> extractor = factory.add(Bean::setTwo, Extractors.STRING, 1).factory(Bean::new).getExtractor();

		when(resultSet.getString(1)).thenThrow(SQLException.class);

		extractor.extract(resultSet);
	}

	@Test
	public void testBoolean() throws Exception {
		final Extractor<Bean> extractor = factory.add(Bean::setThree, Extractors.BOOLEAN, 1).factory(Bean::new).getExtractor();

		when(resultSet.getBoolean(1)).thenReturn(true);

		final Bean bean = extractor.extract(resultSet);
		assertThat(bean.three).isTrue();
	}

	@Test(expected = UncheckedSQLException.class)
	public void testBooleanException() throws Exception {
		final Extractor<Bean> extractor = factory.add(Bean::setThree, Extractors.BOOLEAN, 1).factory(Bean::new).getExtractor();

		when(resultSet.getBoolean(1)).thenThrow(SQLException.class);

		extractor.extract(resultSet);
	}

	@Test
	public void testLong() throws Exception {
		final Extractor<Bean> extractor = factory.add(Bean::setFour, Extractors.LONG, 1).factory(Bean::new).getExtractor();

		when(resultSet.getLong(1)).thenReturn(42L);

		final Bean bean = extractor.extract(resultSet);
		assertThat(bean.four).isEqualTo(42L);
	}

	@Test(expected = UncheckedSQLException.class)
	public void testLongException() throws Exception {
		final Extractor<Bean> extractor = factory.add(Bean::setFour, Extractors.LONG, 1).factory(Bean::new).getExtractor();

		when(resultSet.getLong(1)).thenThrow(SQLException.class);

		extractor.extract(resultSet);
	}

	@Test
	public void testDouble() throws Exception {
		final Extractor<Bean> extractor = factory.add(Bean::setFive, Extractors.DOUBLE, 1).factory(Bean::new).getExtractor();

		when(resultSet.getDouble(1)).thenReturn(3.142);

		final Bean bean = extractor.extract(resultSet);
		assertThat(bean.five).isEqualTo(3.142);
	}

	@Test(expected = UncheckedSQLException.class)
	public void testDoubleException() throws Exception {
		final Extractor<Bean> extractor = factory.add(Bean::setFive, Extractors.DOUBLE, 1).factory(Bean::new).getExtractor();

		when(resultSet.getDouble(1)).thenThrow(SQLException.class);

		extractor.extract(resultSet);
	}

	@Test
	public void testFloat() throws Exception {
		final Extractor<Bean> extractor = factory.add(Bean::setSix, Extractors.FLOAT, 1).factory(Bean::new).getExtractor();

		when(resultSet.getFloat(1)).thenReturn(3.142f);

		final Bean bean = extractor.extract(resultSet);
		assertThat(bean.six).isEqualTo(3.142f);
	}

	@Test(expected = UncheckedSQLException.class)
	public void testFloatException() throws Exception {
		final Extractor<Bean> extractor = factory.add(Bean::setSix, Extractors.FLOAT, 1).factory(Bean::new).getExtractor();

		when(resultSet.getFloat(1)).thenThrow(SQLException.class);

		extractor.extract(resultSet);
	}


	@Test
	public void testBigDecimal() throws Exception {
		final Extractor<Bean> extractor = factory.add(Bean::setSeven, Extractors.BIG_DECIMAL, 1).factory(Bean::new).getExtractor();

		when(resultSet.getBigDecimal(1)).thenReturn(BigDecimal.TEN);

		final Bean bean = extractor.extract(resultSet);
		assertThat(bean.seven).isEqualTo(BigDecimal.TEN);
	}

	@Test(expected = UncheckedSQLException.class)
	public void testBigDecimalException() throws Exception {
		final Extractor<Bean> extractor = factory.add(Bean::setSeven, Extractors.BIG_DECIMAL, 1).factory(Bean::new).getExtractor();

		when(resultSet.getBigDecimal(1)).thenThrow(SQLException.class);

		extractor.extract(resultSet);
	}

	@Test
	public void testTime() throws Exception {
		final Extractor<Bean> extractor = factory.add(Bean::setEight, Extractors.TIME, 1).factory(Bean::new).getExtractor();

		final long time = TimeUnit.HOURS.toMillis(1) + TimeUnit.MINUTES.toMillis(30);
		when(resultSet.getTime(1)).thenReturn(new Time(time));

		final Bean bean = extractor.extract(resultSet);
		assertThat(bean.eight.getTime()).isEqualTo(time);
	}

	@Test(expected = UncheckedSQLException.class)
	public void testTimeException() throws Exception {
		final Extractor<Bean> extractor = factory.add(Bean::setEight, Extractors.TIME, 1).factory(Bean::new).getExtractor();

		when(resultSet.getTime(1)).thenThrow(SQLException.class);

		extractor.extract(resultSet);
	}

	@Test
	public void testDate() throws Exception {
		final Extractor<Bean> extractor = factory.add(Bean::setEight, Extractors.DATE, 1).factory(Bean::new).getExtractor();

		final long date = TimeUnit.DAYS.toMillis(40);
		when(resultSet.getDate(1)).thenReturn(new java.sql.Date(date));

		final Bean bean = extractor.extract(resultSet);
		assertThat(bean.eight.getTime()).isEqualTo(date);
	}

	@Test(expected = UncheckedSQLException.class)
	public void testDateException() throws Exception {
		final Extractor<Bean> extractor = factory.add(Bean::setEight, Extractors.DATE, 1).factory(Bean::new).getExtractor();

		when(resultSet.getDate(1)).thenThrow(SQLException.class);

		extractor.extract(resultSet);
	}

	@Test
	public void testTimestamp() throws Exception {
		final Extractor<Bean> extractor = factory.add(Bean::setEight, Extractors.TIMESTAMP, 1).factory(Bean::new).getExtractor();

		final long timestamp = TimeUnit.DAYS.toMillis(40) + TimeUnit.MINUTES.toMillis(15);
		when(resultSet.getTimestamp(1)).thenReturn(new Timestamp(timestamp));

		final Bean bean = extractor.extract(resultSet);
		assertThat(bean.eight.getTime()).isEqualTo(timestamp);
	}

	@Test(expected = UncheckedSQLException.class)
	public void testTimestampException() throws Exception {
		final Extractor<Bean> extractor = factory.add(Bean::setEight, Extractors.TIMESTAMP, 1).factory(Bean::new).getExtractor();

		when(resultSet.getTimestamp(1)).thenThrow(SQLException.class);

		extractor.extract(resultSet);
	}

	@Test
	public void testMultiple() throws Exception {
		final Extractor<Bean> extractor = factory.add(Bean::setTwo, Extractors.STRING, 1)
				.add(Bean::setOne, Extractors.INTEGER, 2)
				.add(Bean::setFour, Extractors.LONG, 4)
				.factory(Bean::new)
				.getExtractor();

		when(resultSet.getString(1)).thenReturn("two");
		when(resultSet.getInt(2)).thenReturn(1);
		when(resultSet.getLong(4)).thenReturn(20L);

		final Bean bean = extractor.extract(resultSet);
		assertThat(bean.one).isEqualTo(1);
		assertThat(bean.two).isEqualTo("two");
		assertThat(bean.four).isEqualTo(20L);
	}


	private static class Bean {
		int one;
		String two;
		boolean three;
		long four;
		double five;
		float six;
		BigDecimal seven;
		Date eight;

		void setOne(int one) {
			this.one = one;
		}

		void setTwo(String two) {
			this.two = two;
		}

		void setThree(boolean three) {
			this.three = three;
		}

		void setFour(long four) {
			this.four = four;
		}

		void setFive(double five) {
			this.five = five;
		}

		void setSix(float six) {
			this.six = six;
		}

		void setSeven(BigDecimal seven) {
			this.seven = seven;
		}

		void setEight(Date eight) {
			this.eight = eight;
		}
	}
}
/*
 * Copyright (c) 2017, nwillc@gmail.com
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
import com.github.nwillc.funjdbc.functions.Enricher;
import com.github.nwillc.funjdbc.functions.Extractor;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(JMockit.class)
public class EFactoryTest {
    private EFactory<Bean> factory;
    @Mocked
    ResultSet resultSet;

    @Before
    public void setUp() {
        factory = new EFactory<>();
    }

    @Test
    public void testConstructorNoFactory() {
        assertThatThrownBy(() -> factory.getExtractor()).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testConstructorNoExtractor() {
        assertThatThrownBy(() -> factory.withFactory(Bean::new).getExtractor()).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testConstructorNo() {
        final Extractor<Bean> extractor = factory
                .withFactory(Bean::new)
                .add(Bean::setOne, ResultSet::getInt, 1)
                .getExtractor();
        assertThat(extractor).isNotNull();
    }

    @Test
    public void testInteger() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setOne, ResultSet::getInt, 1).withFactory(Bean::new).getExtractor();

        new Expectations() {{
            resultSet.getInt(1);
            result = 5;
        }};

        final Bean bean = extractor.extract(resultSet);
        assertThat(bean.one).isEqualTo(5);
    }

    @Test(expected = UncheckedSQLException.class)
    public void testIntegerException() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setOne, ResultSet::getInt, 1).withFactory(Bean::new).getExtractor();

        new Expectations() {{
            resultSet.getInt(1);
            result = new SQLException();
        }};

        extractor.extract(resultSet);
    }

    @Test
    public void testString() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setTwo, ResultSet::getString, 1).withFactory(Bean::new).getExtractor();

        new Expectations() {{
            resultSet.getString(1);
            result = "two";
        }};

        final Bean bean = extractor.extract(resultSet);
        assertThat(bean.two).isEqualTo("two");
    }

    @Test
    public void testStringColumn() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setTwo, ResultSet::getString, "word").withFactory(Bean::new).getExtractor();

        new Expectations() {{
            resultSet.getString("word");
            result = "two";
        }};

        final Bean bean = extractor.extract(resultSet);
        assertThat(bean.two).isEqualTo("two");
    }

    @Test(expected = UncheckedSQLException.class)
    public void testStringException() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setTwo, ResultSet::getString, 1).withFactory(Bean::new).getExtractor();

        new Expectations() {{
            resultSet.getString(1);
            result = new SQLException();
        }};

        extractor.extract(resultSet);
    }

    @Test
    public void testBoolean() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setThree, ResultSet::getBoolean, 1).withFactory(Bean::new).getExtractor();

        new Expectations() {{
            resultSet.getBoolean(1);
            result = true;
        }};

        final Bean bean = extractor.extract(resultSet);
        assertThat(bean.three).isTrue();
    }

    @Test(expected = UncheckedSQLException.class)
    public void testBooleanException() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setThree, ResultSet::getBoolean, 1).withFactory(Bean::new).getExtractor();

        new Expectations() {{
            resultSet.getBoolean(1);
            result = new SQLException();
        }};

        extractor.extract(resultSet);
    }

    @Test
    public void testLong() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setFour, ResultSet::getLong, 1).withFactory(Bean::new).getExtractor();

        new Expectations() {{
            resultSet.getLong(1);
            result = 42L;
        }};

        final Bean bean = extractor.extract(resultSet);
        assertThat(bean.four).isEqualTo(42L);
    }

    @Test(expected = UncheckedSQLException.class)
    public void testLongException() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setFour, ResultSet::getLong, 1).withFactory(Bean::new).getExtractor();

        new Expectations() {{
            resultSet.getLong(1);
            result = new SQLException();
        }};

        extractor.extract(resultSet);
    }

    @Test
    public void testDouble() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setFive, ResultSet::getDouble, 1).withFactory(Bean::new).getExtractor();

        new Expectations() {{
            resultSet.getDouble(1);
            result = 3.142;
        }};

        final Bean bean = extractor.extract(resultSet);
        assertThat(bean.five).isEqualTo(3.142);
    }

    @Test(expected = UncheckedSQLException.class)
    public void testDoubleException() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setFive, ResultSet::getDouble, 1).withFactory(Bean::new).getExtractor();

        new Expectations() {{
            resultSet.getDouble(1);
            result = new SQLException();
        }};

        extractor.extract(resultSet);
    }

    @Test
    public void testFloat() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setSix, ResultSet::getFloat, 1).withFactory(Bean::new).getExtractor();

        new Expectations() {{
            resultSet.getFloat(1);
            result = 3.142f;
        }};

        final Bean bean = extractor.extract(resultSet);
        assertThat(bean.six).isEqualTo(3.142f);
    }

    @Test(expected = UncheckedSQLException.class)
    public void testFloatException() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setSix, ResultSet::getFloat, 1).withFactory(Bean::new).getExtractor();

        new Expectations() {{
            resultSet.getFloat(1);
            result = new SQLException();
        }};

        extractor.extract(resultSet);
    }


    @Test
    public void testBigDecimal() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setSeven, ResultSet::getBigDecimal, 1).withFactory(Bean::new).getExtractor();

        new Expectations() {{
            resultSet.getBigDecimal(1);
            result = BigDecimal.TEN;
        }};

        final Bean bean = extractor.extract(resultSet);
        assertThat(bean.seven).isEqualTo(BigDecimal.TEN);
    }

    @Test(expected = UncheckedSQLException.class)
    public void testBigDecimalException() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setSeven, ResultSet::getBigDecimal, 1).withFactory(Bean::new).getExtractor();

        new Expectations() {{
            resultSet.getBigDecimal(1);
            result = new SQLException();
        }};

        extractor.extract(resultSet);
    }

    @Test
    public void testTime() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setEight, ResultSet::getTime, 1).withFactory(Bean::new).getExtractor();

        final long time = TimeUnit.HOURS.toMillis(1) + TimeUnit.MINUTES.toMillis(30);

        new Expectations() {{
            resultSet.getTime(1);
            result = new Time(time);
        }};

        final Bean bean = extractor.extract(resultSet);
        assertThat(bean.eight.getTime()).isEqualTo(time);
    }

    @Test(expected = UncheckedSQLException.class)
    public void testTimeException() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setEight, ResultSet::getTime, 1).withFactory(Bean::new).getExtractor();

        new Expectations() {{
            resultSet.getTime(1);
            result = new SQLException();
        }};

        extractor.extract(resultSet);
    }

    @Test
    public void testDate() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setEight, ResultSet::getDate, 1).withFactory(Bean::new).getExtractor();

        final long date = TimeUnit.DAYS.toMillis(40);

        new Expectations() {{
            resultSet.getDate(1);
            result = new java.sql.Date(date);
        }};

        final Bean bean = extractor.extract(resultSet);
        assertThat(bean.eight.getTime()).isEqualTo(date);
    }

    @Test(expected = UncheckedSQLException.class)
    public void testDateException() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setEight, ResultSet::getDate, 1).withFactory(Bean::new).getExtractor();

        new Expectations() {{
            resultSet.getDate(1);
            result = new SQLException();
        }};

        extractor.extract(resultSet);
    }

    @Test
    public void testTimestamp() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setEight, ResultSet::getTimestamp, 1).withFactory(Bean::new).getExtractor();

        final long timestamp = TimeUnit.DAYS.toMillis(40) + TimeUnit.MINUTES.toMillis(15);

        new Expectations() {{
            resultSet.getTimestamp(1);
            result = new Timestamp(timestamp);
        }};

        final Bean bean = extractor.extract(resultSet);
        assertThat(bean.eight.getTime()).isEqualTo(timestamp);
    }

    @Test(expected = UncheckedSQLException.class)
    public void testTimestampException() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setEight, ResultSet::getTimestamp, 1).withFactory(Bean::new).getExtractor();

        new Expectations() {{
            resultSet.getTimestamp(1);
            result = new SQLException();
        }};

        extractor.extract(resultSet);
    }

    @Test
    public void testEnricher() throws Exception {
        final Enricher<Bean> enricher = factory.add(Bean::setOne, ResultSet::getInt, 1).getEnricher();

        new Expectations() {{
            resultSet.getInt(1);
            result = 42;
        }};
        Bean bean = new Bean();
        bean.setOne(0);
        enricher.accept(bean, resultSet);
        assertThat(bean.one).isEqualTo(42);
    }


    @Test
    public void testMultiple() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setTwo, ResultSet::getString, 1)
                .add(Bean::setOne, ResultSet::getInt, 2)
                .add(Bean::setFour, ResultSet::getLong, 4)
                .withFactory(Bean::new)
                .getExtractor();

        new Expectations() {{
            resultSet.getString(1);
            result = "two";
            resultSet.getInt(2);
            result = 1;
            resultSet.getLong(4);
            result = 20L;
        }};

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

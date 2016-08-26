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

import com.github.nwillc.funjdbc.functions.Extractor;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ExtractorFactoryTest {
    private ExtractorFactory<Bean> factory;
    private ResultSet resultSet;

    @Before
    public void setUp() throws Exception {
        factory = new ExtractorFactory<>();
        resultSet = mock(ResultSet.class);
    }

    @Test
    public void testConstructor() throws Exception {
        final Extractor<Bean> extractor = factory.create(Bean::new);
        assertThat(extractor).isNotNull();
        final Bean bean = extractor.extract(null);
        assertThat(bean).isInstanceOf(Bean.class);
    }

    @Test
    public void testInteger() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setOne, Extractors.INTEGER, 1).create(Bean::new);

        when(resultSet.getInt(1)).thenReturn(5);

        final Bean bean = extractor.extract(resultSet);
        assertThat(bean.one).isEqualTo(5);
    }

    @Test
    public void testString() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setTwo, Extractors.STRING, 1).create(Bean::new);

        when(resultSet.getString(1)).thenReturn("two");

        final Bean bean = extractor.extract(resultSet);
        assertThat(bean.two).isEqualTo("two");
    }

    @Test
    public void testBoolean() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setThree, Extractors.BOOLEAN, 1).create(Bean::new);

        when(resultSet.getBoolean(1)).thenReturn(true);

        final Bean bean = extractor.extract(resultSet);
        assertThat(bean.three).isTrue();
    }

    @Test
    public void testLong() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setFour, Extractors.LONG, 1).create(Bean::new);

        when(resultSet.getLong(1)).thenReturn(42L);

        final Bean bean = extractor.extract(resultSet);
        assertThat(bean.four).isEqualTo(42L);
    }

    @Test
    public void testDouble() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setFive, Extractors.DOUBLE, 1).create(Bean::new);

        when(resultSet.getDouble(1)).thenReturn(3.142);

        final Bean bean = extractor.extract(resultSet);
        assertThat(bean.five).isEqualTo(3.142);
    }

    @Test
    public void testMultiple() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setTwo, Extractors.STRING, 1)
                .add(Bean::setOne, Extractors.INTEGER, 2)
                .add(Bean::setThree, Extractors.BOOLEAN, 3)
                .add(Bean::setFour, Extractors.LONG, 4)
                .create(Bean::new);

        when(resultSet.getString(1)).thenReturn("two");
        when(resultSet.getInt(2)).thenReturn(1);
        when(resultSet.getBoolean(3)).thenReturn(true);
        when(resultSet.getLong(4)).thenReturn(20L);

        final Bean bean = extractor.extract(resultSet);
        assertThat(bean.one).isEqualTo(1);
        assertThat(bean.two).isEqualTo("two");
        assertThat(bean.three).isTrue();
        assertThat(bean.four).isEqualTo(20L);
    }


    private static class Bean {
        int one;
        String two;
        boolean three;
        long four;
        double five;

        public void setFive(double five) {
            this.five = five;
        }

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
    }
}
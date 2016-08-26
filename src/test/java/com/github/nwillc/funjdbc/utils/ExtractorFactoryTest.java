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
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ExtractorFactoryTest {
    private ExtractorFactory<Bean> factory;

    @Before
    public void setUp() throws Exception {
        factory = new ExtractorFactory<>();
    }

    @Test
    public void testConstructor() throws Exception {
        final Extractor<Bean> extractor = factory.create(Bean::new);
        assertThat(extractor).isNotNull();
        final Bean bean = extractor.extract(null);
        assertThat(bean).isInstanceOf(Bean.class);
    }

    @Test
    public void testExtraction() throws Exception {
        final Extractor<Bean> extractor = factory.add(Bean::setTwo, Extractors.STRING, 1)
                .add(Bean::setOne, Extractors.INTEGER, 2)
                .add(Bean::setThree, Extractors.BOOLEAN, 2)
                .create(Bean::new);

        ResultSet rs = mock(ResultSet.class);
        when(rs.getString(1)).thenReturn("two");
        when(rs.getInt(2)).thenReturn(1);
        when(rs.getBoolean(2)).thenReturn(true);

        final Bean bean = extractor.extract(rs);
        assertThat(bean.one).isEqualTo(1);
        assertThat(bean.two).isEqualTo("two");
        assertThat(bean.three).isTrue();
    }

    private static class Bean {
        int one;
        String two;
        boolean three;

        void setOne(int one) {
            this.one = one;
        }

        void setTwo(String two) {
            this.two = two;
        }

        public void setThree(boolean three) {
            this.three = three;
        }
    }
}
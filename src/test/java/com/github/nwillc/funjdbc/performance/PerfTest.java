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

package com.github.nwillc.funjdbc.performance;

import com.github.nwillc.funjdbc.functions.Extractor;
import com.github.nwillc.funjdbc.utils.EFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class PerfTest {
    private final static Logger LOGGER = Logger.getLogger(PerfTest.class.getName());
    private static final int LOOPS = 100_000;
    @Mock
    ResultSet resultSet;
    @Rule
    public MockitoRule rule = MockitoJUnit.rule().silent();
    private Extractor<Bean> codedBeanExtractor = new BeanExtractor();
    private Extractor<Bean> generatedBeanExtractor;

    @Before
    public void setup() throws Exception {
        when(resultSet.getInt(anyInt()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(resultSet.getString(anyInt()))
                .thenAnswer(invocation -> invocation.getArgument(0).toString());
        when(resultSet.getDouble(anyInt()))
                .thenAnswer(invocation -> ((Integer) invocation.getArgument(0)).doubleValue());

        EFactory<Bean> factory = new EFactory<>();
        generatedBeanExtractor = factory
                .add(Bean::setDoubleValue, ResultSet::getDouble, 1)
                .add(Bean::setIntegerValue, ResultSet::getInt, 2)
                .add(Bean::setStringValue, ResultSet::getString, 3)
                .factory(Bean::new)
                .getExtractor();
    }

    @Test
    public void loop() throws Exception {
        System.gc();
        long start = System.currentTimeMillis();
        for (int i = 0; i < LOOPS; i++) {
            codedBeanExtractor.extract(resultSet);
        }
        LOGGER.info("Coded Extractor: " + (System.currentTimeMillis() - start));
        System.gc();
        start = System.currentTimeMillis();
        for (int i = 0; i < LOOPS; i++) {
            generatedBeanExtractor.extract(resultSet);
        }
        LOGGER.info("Generated Extractor: " + (System.currentTimeMillis() - start));
    }

    private class BeanExtractor implements Extractor<Bean> {
        @Override
        public Bean extract(ResultSet rs) throws SQLException {
            Bean b = new Bean();
            b.setDoubleValue(rs.getDouble(1));
            b.setIntegerValue(rs.getInt(2));
            b.setStringValue(rs.getString(3));
            return b;
        }
    }

    private class Bean {
        private Integer integerValue;
        private String stringValue;
        private double doubleValue;

        public Bean() {
        }

        public void setDoubleValue(double doubleValue) {
            this.doubleValue = doubleValue;
        }

        public void setIntegerValue(Integer integerValue) {
            this.integerValue = integerValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }
    }
}

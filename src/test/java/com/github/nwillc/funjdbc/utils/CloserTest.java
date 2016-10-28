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
 */

package com.github.nwillc.funjdbc.utils;

import com.github.nwillc.contracts.UtilityClassContract;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.verify;

public class CloserTest extends UtilityClassContract {
    @Mock
    AutoCloseable autoCloseable;
    @Rule
    public MockitoRule rule = MockitoJUnit.rule().silent();


    @Override
    public Class<?> getClassToTest() {
        return Closer.class;
    }

    @Test
    public void testHandlesNull() throws Exception {
        Closer.close(null);
    }

    @Test
    public void shouldClose() throws Exception {
        Closer.close(autoCloseable);
        verify(autoCloseable).close();
    }

    @Test
    public void testHandlesThrownException() throws Exception {
        Closer.close(() -> {
            throw new Exception();
        });
    }
}

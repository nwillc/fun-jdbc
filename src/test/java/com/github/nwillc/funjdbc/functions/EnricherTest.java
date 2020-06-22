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

package com.github.nwillc.funjdbc.functions;

import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(JMockit.class)
public class EnricherTest {
    @Mocked
    private ResultSet resultSet;

    @Test
    public void testAndThenArgumentCheck() {
        Enricher<Boolean> enricher = (o, r) -> {
        };

        assertThatThrownBy(() -> enricher.andThen(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testAndThen() {
        AtomicInteger i = new AtomicInteger(0);
        Enricher<AtomicInteger> enricher = (o, r) -> o.addAndGet(1);
        enricher = enricher.andThen((o, r) -> o.addAndGet(2));

        enricher.accept(i, resultSet);

        assertThat(i.get()).isEqualTo(3);
    }
}

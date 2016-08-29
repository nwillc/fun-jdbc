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

package com.github.nwillc.funjdbc.functions;

import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CascadeEnricherTest {
	@Test
	public void shouldHandleEmptyList() throws Exception {
		CascadeEnricher<Boolean> instance = new CascadeEnricher<>();

		instance.enrich(true, null);
	}

	@Test
	public void testNoEnrichers() throws Exception {
		CascadeEnricher<AtomicBoolean> instance = new CascadeEnricher<>();
		instance.enrich(null, null);
	}

	@Test
	public void shouldEnrich() throws Exception {
		FlagEnricher one = new FlagEnricher();
		FlagEnricher two = new FlagEnricher();

		assertFalse(one.flag.get());
		assertFalse(two.flag.get());

		CascadeEnricher<AtomicBoolean> instance = new CascadeEnricher<>(one, two);
		instance.enrich(null, null);

		assertTrue(one.flag.get());
		assertTrue(one.flag.get());
	}

	private class FlagEnricher implements Enricher<AtomicBoolean> {
		AtomicBoolean flag = new AtomicBoolean(false);

		@Override
		public void enrich(AtomicBoolean entity, ResultSet rs) throws SQLException {
			flag.set(true);
		}
	}
}
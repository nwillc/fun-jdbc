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

package com.github.nwillc.funjdbc.migrate;

import com.github.nwillc.contracts.ComparatorContract;
import org.junit.Before;

import java.util.Comparator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MigrationComparatorTest extends ComparatorContract<Migration> {

	@Before
	public void setUp() throws Exception {
		setNulls(Nulls.NULLS_FIRST);
	}

	@Override
	protected Comparator<Migration> getComparator() {
		return new Manager.MigrationComparator();
	}

	@Override
	protected Migration getValue() {
		Migration migration = mock(Migration.class);
		when(migration.getIdentifier()).thenReturn("1");
		return migration;
	}

	@Override
	protected Migration getLesserValue() {
		Migration migration = mock(Migration.class);
		when(migration.getIdentifier()).thenReturn("0");
		return migration;
	}

}
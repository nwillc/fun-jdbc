package com.github.nwillc.funjdbc.functions;

import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

/**
 *
 */
public class CascadeEnricherTest {

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
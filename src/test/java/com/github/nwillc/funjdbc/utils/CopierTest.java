package com.github.nwillc.funjdbc.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class CopierTest {
	@Test
	public void shouldCopy() throws Exception {
		List<String> strings = new ArrayList<>();
		strings.add("zero");
		strings.add("one");

		Bean bean = new Bean();
		Copier.copy(bean::setNumber, strings::get, 1);
		assertThat(bean.number).isEqualTo(strings.get(1));
	}

	private class Bean {
		String number;

		public void setNumber(String number) {
			this.number = number;
		}
	}
}
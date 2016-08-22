package com.github.nwillc.funjdbc.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class CopierTest {
	private List<String> strings = Arrays.asList("zero", "one", "two", "three");

	@Test
	public void shouldPrint() throws Exception {
		Copier.multi(integer -> strings.get(integer), 0, 3);
	}

	@Test
	public void shouldCopy() throws Exception {
		Bean bean = new Bean();
		wrap(bean, strings);
		assertThat(bean.str).isEqualTo(strings.get(1));
	}

	private void wrap(Bean b, List<String> l) {
		Copier.copy(b::setStr, l::get, 1);
	}

	private class Bean {
		String str;

		public void setStr(String str) {
			this.str = str;
		}
	}
}
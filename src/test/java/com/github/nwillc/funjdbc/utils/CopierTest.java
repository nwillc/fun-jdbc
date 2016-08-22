package com.github.nwillc.funjdbc.utils;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class CopierTest {
	private List<String> strings = Arrays.asList("zero", "one", "two", "three");

	@Test
	public void shouldPrint() throws Exception {
		BiFunction<List<String>, Integer, String> accessor = List::get;
		System.out.println(accessor.apply(strings,0));
	}

	@Test
	public void shouldCopy() throws Exception {
		Bean bean = new Bean();
		wrap(bean, strings);
		assertThat(bean.str).isEqualTo(strings.get(1));
	}

	@Test
	public void shouldCopy2() throws Exception {
		Bean bean = new Bean();
		Copier.copy2(strings, bean, Bean::setStr, List::get, 0);
		assertThat(bean.str).isEqualTo(strings.get(0));
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
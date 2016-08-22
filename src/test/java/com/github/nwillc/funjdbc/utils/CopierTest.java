package com.github.nwillc.funjdbc.utils;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */

public class CopierTest {
	private List<String> strings = Arrays.asList("zero", "one", "two", "three");


	@Test
	public void shouldCopy2() throws Exception {
		Bean bean = new Bean();
		Copier.copy2(strings, bean, Bean::setStr, List::get, 0);
		assertThat(bean.str).isEqualTo(strings.get(0));
	}


	private class Bean {
		String str;

		public void setStr(String str) {
			this.str = str;
		}
	}
}
package com.github.nwillc.funjdbc.utils;

import com.github.nwillc.funjdbc.UncheckedSQLException;
import org.junit.Test;
import org.mockito.internal.matchers.Any;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */

public class CopierTest {
	private List<String> strings = Arrays.asList("zero", "one", "two", "three");


	@Test
	public void shouldCopy2() throws Exception {
		Bean bean = new Bean();
		ResultSet rs = mock(ResultSet.class);
		when(rs.getString(anyInt())).thenReturn("zero");

		BiFunction<ResultSet, Integer, String> function = (r, index) -> {
			try {
				return r.getString(index);
			} catch (SQLException e) {
				throw new UncheckedSQLException("", e);
			}
		};
		Copier.copy2(bean, Bean::setStr, rs, function, 0);
		assertThat(bean.str).isEqualTo(strings.get(0));
	}


	private class Bean {
		String str;

		public void setStr(String str) {
			this.str = str;
		}
	}
}
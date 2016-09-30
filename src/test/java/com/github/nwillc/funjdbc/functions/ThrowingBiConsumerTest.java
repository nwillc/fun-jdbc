package com.github.nwillc.funjdbc.functions;

import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.mock;


public class ThrowingBiConsumerTest {
    @Test
    public void testAcceptException() throws Exception {
        Enricher<Boolean> enricher = (o,r) -> { throw new SQLException("test"); };
        ResultSet resultSet = mock(ResultSet.class);

        try {
            enricher.accept(true, resultSet);
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (Exception e) {
            assertThat(e.getCause()).isInstanceOf(SQLException.class);
        }
    }

}
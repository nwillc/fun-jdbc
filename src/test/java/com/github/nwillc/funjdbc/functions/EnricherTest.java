package com.github.nwillc.funjdbc.functions;

import org.junit.Test;

import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.mock;


public class EnricherTest {
    @Test
    public void testAndThenArgumentCheck() throws Exception {
        Enricher<Boolean> enricher = (o,r) -> {};

        try {
            enricher.andThen(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    public void testAndThen() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        AtomicInteger i = new AtomicInteger(0);
        Enricher<AtomicInteger> enricher = (o,r) -> o.addAndGet(1);
        enricher = enricher.andThen((o,r) -> o.addAndGet(2));

        enricher.accept(i, rs);

        assertThat(i.get()).isEqualTo(3);
    }
}
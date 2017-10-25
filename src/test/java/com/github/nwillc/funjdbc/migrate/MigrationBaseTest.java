/*
 * Copyright (c) 2017, nwillc@gmail.com
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
 *
 */

package com.github.nwillc.funjdbc.migrate;

import com.github.nwillc.funjdbc.functions.ConnectionProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class MigrationBaseTest {
    private Migration migration;
    private Manager manager;
    @Mock
    ConnectionProvider connectionProvider;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().silent();

    @Before
    public void setUp() throws Exception {
        migration = new DummyMigration();
        manager = Manager.getInstance();
    }

    @Test
    public void testToString() throws Exception {
        Manager managerSpy = spy(manager);
        when(managerSpy.migrated(migration.getIdentifier())).thenReturn(true);
        managerSpy.setConnectionProvider(connectionProvider);
        final String str = migration.toString();
        assertThat(str).contains(migration.getClass().getSimpleName())
                .contains(migration.getDescription())
                .contains(migration.getIdentifier());

    }

    @Test
    public void testRunAlwaysDefaultsFalse() throws Exception {
        assertThat(migration.runAlways()).isFalse();
    }

    @Test
    public void testShouldProvideConnection() throws Exception {


        manager.setConnectionProvider(connectionProvider);
        migration.getConnection();
        verify(connectionProvider).getConnection();
    }

    private static class DummyMigration extends MigrationBase {
        @Override
        public String getDescription() {
            return "dummy";
        }

        @Override
        public String getIdentifier() {
            return "1";
        }

        @Override
        public void perform() throws Exception {
            throw new RuntimeException();
        }
    }
}
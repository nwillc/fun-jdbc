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


import com.github.nwillc.contracts.SingletonContract;
import com.github.nwillc.funjdbc.DbAccessor;
import com.github.nwillc.funjdbc.TestDbInitialization;
import com.github.nwillc.funjdbc.UncheckedSQLException;
import mockit.Expectations;
import mockit.integration.junit4.JMockit;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.zapodot.junit.db.EmbeddedDatabaseRule;

import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(JMockit.class)
public class ManagerTest extends SingletonContract implements DbAccessor {
    private Manager manager;

    @Rule
    public final EmbeddedDatabaseRule embeddedDb = EmbeddedDatabaseRule
            .builder()
            .initializedByPlugin(new TestDbInitialization())
            .build();

    @Override
    public Class<?> getClassToTest() {
        return Manager.class;
    }

    @Before
    public void setUp() throws Exception {
        manager = Manager.getInstance();
        assertThat(manager).isNotNull();
        manager.setConnectionProvider(this::getConnection);
    }

    @After
    public void tearDown() throws Exception {
        manager.clear();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMigrationsEnabledException() throws Exception {
        new Expectations(Manager.class) {{
            manager.getConnection();
            result = new SQLException();
        }};
        assertThat(manager.migrationsEnabled()).isFalse();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMigratedException() throws Exception {
        new Expectations(Manager.class) {{
            manager.getConnection();
            result = new SQLException();
        }};
        assertThat(manager.migrated("foo")).isFalse();
    }

    @Test
    public void testNoConnectionProvider() throws Exception {
        manager.setConnectionProvider(null);
        assertThatThrownBy(() -> manager.getConnection()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void testRegisterClass() throws Exception {
        manager.add(DummyMigration.class);
        assertThat(manager.getMigrations()).hasAtLeastOneElementOfType(DummyMigration.class);
    }

    @Test
    public void testRegisterInstance() throws Exception {
        manager.add(new DummyMigration());
        assertThat(manager.getMigrations()).hasAtLeastOneElementOfType(DummyMigration.class);
    }

    @Test
    public void testBadMigrationClass() throws Exception {
        assertThatThrownBy(() -> manager.add(MigrationWithConstructor.class)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testEmptyAdd() throws Exception {
        manager.add();
    }

    @Test
    public void testRegistrationOrder() throws Exception {
        DummyMigration one = new DummyMigration("1");
        DummyMigration two = new DummyMigration("2");
        DummyMigration three = new DummyMigration("3");

        manager.add(two);
        manager.add(three);
        manager.add(one);
        assertThat(manager.getMigrations()).containsExactly(one, two, three);
    }

    @Test
    public void testMigrationsEnabled() throws Exception {
        assertThat(manager.migrationsEnabled()).isFalse();
        manager.enableMigrations();
        assertThat(manager.migrationsEnabled()).isTrue();
    }

    @Test
    public void testSimpleMigration() throws Exception {
        manager.enableMigrations();
        assertThat(manager.migrationsEnabled()).isTrue();
        final DummyMigration first = new DummyMigration("first");
        manager.add(first);
        assertThat(manager.migrated(first.getIdentifier())).isFalse();
        manager.doMigrations();
        assertThat(manager.migrated(first.getIdentifier())).isTrue();
        assertThat(first.completed()).isTrue();
    }

    @Test
    public void testMigrationException() throws Exception {
        manager.enableMigrations();
        assertThat(manager.migrationsEnabled()).isTrue();
        manager.add(new DummyMigration("first") {
            @Override
            public void perform() throws Exception {
                throw new RuntimeException("test");
            }
        });
        assertThatThrownBy(() -> manager.doMigrations()).isInstanceOf(UncheckedSQLException.class);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return embeddedDb.getConnection();
    }

    public static class DummyMigration extends MigrationBase {
        private String identifier;

        public DummyMigration() {
            this(null);
        }

        public DummyMigration(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public String getIdentifier() {
            return identifier;
        }

        @Override
        public void perform() throws Exception {
        }
    }

    public static class MigrationWithConstructor extends DummyMigration {
        MigrationWithConstructor(String arg) {
        }
    }
}
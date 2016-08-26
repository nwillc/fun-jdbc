/*
 * Copyright (c) 2016, nwillc@gmail.com
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


import almost.functional.utils.LogFactory;
import almost.functional.utils.Preconditions;
import com.github.nwillc.funjdbc.DbAccessor;
import com.github.nwillc.funjdbc.functions.ConnectionProvider;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The migration Manager. This singleton manages a set of Migrations, performing them as needed and persisting the
 * status of each.
 */
public class Manager implements DbAccessor {
    private static final Logger LOGGER = LogFactory.getLogger();
    private static final Manager INSTANCE = new Manager();

    private static final String CREATE = "CREATE TABLE MIGRATIONS ( IDENTIFIER CHAR(40) PRIMARY KEY, DESCRIPTION CHAR(120))";
    private static final String INSERT = "INSERT INTO MIGRATIONS (IDENTIFIER, DESCRIPTION) VALUES('%s', '%s')";
    private static final String FIND = "SELECT * FROM MIGRATIONS WHERE IDENTIFIER = '%s'";

    private final Set<Migration> migrations = new TreeSet<>(new MigrationComparator());
    private ConnectionProvider connectionProvider;

    /**
     * Gets the singleton instance.
     *
     * @return the instance
     */
    public static Manager getInstance() {
        return INSTANCE;
    }

    private Manager() {
    }

    /**
     * Gets connection provider.
     *
     * @return the connection provider
     */
    public ConnectionProvider getConnectionProvider() {
        return connectionProvider;
    }

    /**
     * Sets connection provider.
     *
     * @param connectionProvider the connection provider
     */
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connectionProvider == null) {
            throw new IllegalStateException("No ConnectionProvider available.");
        }
        return connectionProvider.getConnection();
    }

    /**
     * Add a Migration to the set.
     *
     * @param aMigration the a migration
     * @throws IllegalArgumentException the illegal argument exception
     */
    public void add(Class<? extends Migration> aMigration) throws IllegalArgumentException {
        try {
            migrations.add(aMigration.newInstance());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to add " + aMigration.getSimpleName() + " because " + e);
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Add a Migration to the set..
     *
     * @param migration the migration
     */
    public void add(Migration migration) {
        migrations.add(migration);
    }

    /**
     * Gets the set of Migrations.
     *
     * @return the migrations
     */
    public Set<Migration> getMigrations() {
        return migrations;
    }

    /**
     * Clear the set of migrations.
     */
    public void clear() {
        migrations.clear();
    }

    /**
     * Is migration management enabled in the database.
     *
     * @return the boolean
     */
    public boolean migrationsEnabled() {
        try (Connection connection = getConnection()) {
            ResultSet resultSet = connection.getMetaData().getTables(null, null, "MIGRATIONS", null);
            return stream(rs -> rs.getString(3), resultSet).count() == 1;
        } catch (SQLException e) {
            LOGGER.warning(e.toString());
        }
        return false;
    }

    /**
     * Enable migration management in the database.
     *
     * @throws java.sql.SQLException if the migration table can not be added to the database.
     */
    public void enableMigrations() throws SQLException {
        dbUpdate(CREATE);
    }

    /**
     * Check if a migration has been performed.
     *
     * @param first the first
     * @return the boolean
     */
    public boolean migrated(String first) {
        try {
            return dbFind(rs -> rs.getString(1), FIND, first).isPresent();
        } catch (SQLException e) {
            LOGGER.warning(e.toString());
        }
        return false;
    }

    /**
     * Do migrations as needed. Perform any Migration that hasn't been completed or is designated runAlways.
     */
    public void doMigrations() {
        migrations.forEach(migration -> {
            if ((!migrated(migration.getIdentifier()) || migration.runAlways())
                    && migration.perform()) {
                try {
                    dbUpdate(INSERT, migration.getIdentifier(), migration.getDescription());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static class MigrationComparator implements Comparator<Migration> {
        @Override
        public int compare(Migration o1, Migration o2) {
            Preconditions.checkNotNull(o1, "Can not compare null Migration instance");
            Preconditions.checkNotNull(o2, "Can not compare null Migration instance");

            if (o1.getIdentifier() == null ^ o2.getIdentifier() == null) {
                return (o1.getIdentifier() == null) ? -1 : 1;
            }

            if (o1.getIdentifier() == null) {
                return 0;
            }

            return o1.getIdentifier().compareToIgnoreCase(o2.getIdentifier());
        }
    }
}

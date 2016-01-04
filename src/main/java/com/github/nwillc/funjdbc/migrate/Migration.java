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
 */

package com.github.nwillc.funjdbc.migrate;


import com.github.nwillc.funjdbc.DbAccessor;

/**
 * The interface for Migrations.
 */
public interface Migration extends DbAccessor {
    /**
     * Gets the migration description.
     *
     * @return the description
     */
    String getDescription();

    /**
     * Gets the migration identifier.
     *
     * @return the identifier
     */
    String getIdentifier();

    /**
     * Run this migration every time the Manager does migrations, even if previously performed.
     *
     * @return the boolean
     */
    boolean runAlways();

    /**
     * Has this migration been successfully completed.
     *
     * @return the boolean
     */
    boolean completed();

    /**
     * Perform this migration. This method should contain the code, using JDBC, to alter the database as desired.
     *
     * @return the status of the migration. true indicates success.
     */
    boolean perform();
}

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

package com.github.nwillc.funjdbc.utils;

import almost.functional.utils.LogFactory;

import java.util.logging.Logger;

/**
 * Utility class for closing an AutoCloseable without throwing an Exceptions.
 */
public final class Closer {
    private static final Logger LOGGER = LogFactory.getLogger();

    private Closer() {}

    /**
     * Close an AutoCloseable without exception.
     *
     * @param autoCloseable resource to close.
     */
    static public void close(AutoCloseable autoCloseable) {
        if (autoCloseable == null) {
            return;
        }

        try {
            autoCloseable.close();
        } catch (Exception e) {
            LOGGER.info("Failed to close autoclosable " + e.getClass().getCanonicalName() + ": " + e.getMessage());
        }
    }
}

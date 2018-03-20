/*
 * Copyright (C) 2017 Gregory Clarke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.filevinder.core;

import static java.lang.System.err;

/**
 * A class for centralising error handling logic.
 *
 * @author Gregory Clarke
 */
public final class ErrorHandler {

    private ErrorHandler() {
    }

    /**
     * Will log details about the provided exception.
     * @param msg The exception message
     * @param e The exception object
     */
    public static void recordErr(final String msg, final Exception e) {
        err.println(msg);
        e.printStackTrace(err);
    }

    /**
     * Will log details about the provided exception and then re-throw it.
     * @param msg The exception message
     * @param e The exception object
     * @throws FileVinderException The re-thrown exception
     */
    public static void propogateErr(final String msg, final Exception e) throws FileVinderException {
        err.println(msg);
        e.printStackTrace(err);
        throw new FileVinderException(msg, e);
    }

}

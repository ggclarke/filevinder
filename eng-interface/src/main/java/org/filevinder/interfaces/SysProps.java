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
package org.filevinder.interfaces;

/**
 * An enum that maps onto this programs environment variables.
 * @author Gregory Clarke
 */
public enum SysProps {
    /**
     * The extension to use for the index file.
     */
    FV_INDEX_FILE_EXT,
    /**
     * Root directory for test files.
     */
    FV_TEST_ROOT,
    /**
     * The application classpath.
     */
    FV_CLASSPATH,
    /**
     * The class implementing the Search interface.
     */
    FV_SEARCH_CLASS,
    /**
     * The engine jar for runtime access.
     */
    FV_ENG_JAR,
    /**
     * The path of the file used to cache previous search details.
     */
    FV_SEARCH_CACHE_FILE

}

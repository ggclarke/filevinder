/*
 * Copyright (C) 2018
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
package org.filevinder.ui.usecase;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author Gregory Clarke
 */
public interface SearchHistory {

    /**
     * Persists the supplied past search.
     * @param searchData search parameters
     * @throws IOException exception
     */
    void persist(final CachedSearchData searchData) throws IOException;

    /**
     * Deletes the file used for caching searches.
     *
     * @throws IOException IO exception
     */
    void delete() throws IOException;

    /**
     * Returns the most recent cached searches. With most recent item first.
     *
     * @return list of searches
     * @throws IOException IO exceptions
     */
    List<String> retrieveRaw() throws IOException;

    /**
     * Returns the most recent cached searches. With most recent item first.
     *
     * @return list of searches
     * @throws IOException IO exceptions
     */
    List<CachedSearchData> retrieve() throws IOException;

    /**
     * Returns the most recent cached search.
     *
     * @return list of searches
     * @throws IOException IO exceptions
     * @param pos specifies how many searches back
     */
    CachedSearchData retrievePrev(final int pos) throws IOException;

    /**
     * Check is position exists in the cache.
     *
     * @return if pos is valid
     * @throws IOException IO exceptions
     * @param pos specifies how many searches back
     */
    boolean isValidPos(final int pos) throws IOException;

}

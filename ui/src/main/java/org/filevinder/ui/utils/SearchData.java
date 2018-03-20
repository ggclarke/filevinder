/*
 * Copyright (C) 2018 Gregory Clarke
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
package org.filevinder.ui.utils;

/**
 *
 * @author Gregory Clarke
 */
public class SearchData {

    /**
     * Field separator used in the cache file.
     */
    public static final String TOKEN = ",";

    private String searchText, searchLocation, searchFileTypes;

    /**
     * Constructs a data structure for caching past search Results.
     *
     * @param searchTextVal a search string
     */
    public SearchData(final String searchTextVal) {
        searchText = searchTextVal;
    }

    /**
     * Constructs a data structure for caching past search Results.
     *
     * @param searchTextVal a search string
     * @param searchLocationVal a search location string
     * @param searchFileTypesVal a search file types string
     */
    public SearchData(final String searchTextVal,
            final String searchLocationVal,
            final String searchFileTypesVal) {

        searchText = searchTextVal;
        searchLocation = searchLocationVal;
        searchFileTypes = searchFileTypesVal;
    }

    @Override
    public final String toString() {
        return (searchText == null ? "" : searchText) + TOKEN
                + (searchLocation == null ? "" : searchLocation) + TOKEN
                + (searchFileTypes == null ? "" : searchFileTypes);
    }

    /**
     * @return the searchText
     */
    public final String getSearchText() {
        return searchText;
    }

    /**
     * @param searchTextVal the searchText to set
     */
    public final void setSearchText(final String searchTextVal) {
        this.searchText = searchTextVal;
    }

    /**
     * @return the searchLocation
     */
    public final String getSearchLocation() {
        return searchLocation;
    }

    /**
     * @param searchLocationVal the searchLocation to set
     */
    public final void setSearchLocation(final String searchLocationVal) {
        this.searchLocation = searchLocation;
    }

    /**
     * @return the searchFileTypes
     */
    public final String getSearchFileTypes() {
        return searchFileTypes;
    }

    /**
     * @param searchFileTypesVal the searchFileTypes to set
     */
    public final void setSearchFileTypes(final String searchFileTypesVal) {
        this.searchFileTypes = searchFileTypesVal;
    }

}

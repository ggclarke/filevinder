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
package org.filevinder.ui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.filevinder.ui.utils.OSChecker;

/**
 * Model Parameters with two way binding to the main Application UI. The model
 * is updated by the controller. The model is value-bound to the view.
 *
 * @author Gregory Clarke
 */
public final class Model {

    /**
     * Default values assumed by UI fields.
     */
    public static final String SEARCH_TEXT_DEFAULT = "",
            SEARCH_LOCATION_DEFAULT = "", SEARCH_FILE_TYPES_DEFAULT = "";

    private static final Model SINGLETON = new Model();
    private SimpleStringProperty searchText, searchLocation, searchFileTypes,
            fileText;
    private ObservableList<SearchResult> searchResults;
    private int prevSearchPos;

    private Model() {
        searchText = new SimpleStringProperty(SEARCH_TEXT_DEFAULT);
        searchLocation = new SimpleStringProperty(SEARCH_LOCATION_DEFAULT);
        searchFileTypes = new SimpleStringProperty(SEARCH_FILE_TYPES_DEFAULT);
        fileText = new SimpleStringProperty("Hello World");
        searchResults = FXCollections.observableArrayList();
    }

    public static Model getInstance() {
        return SINGLETON;
    }

    public String getFileText() {
        return fileText.getValue();
    }

    public void setFileText(final String fileTextVal) {
        fileText.set(fileTextVal);
    }

    public SimpleStringProperty fileTextProperty() {
        return fileText;
    }

    public SimpleStringProperty searchTextProperty() {
        return searchText;
    }

    public void setSearchText(final String str) {
        searchText.set(str);
    }

    public String getSearchText() {
        return searchText.getValue();
    }

    public SimpleStringProperty searchLocationProperty() {
        return searchLocation;
    }

    public void setSearchLocation(final String str) {
        searchLocation.set(str);
    }

    public String getSearchLocation() {
        return searchLocation.getValue();
    }

    public SimpleStringProperty searchFileTypesProperty() {
        return searchFileTypes;
    }

    public void setSearchFileTypes(final String str) {
        searchFileTypes.set(str);
    }

    public String getSearchFileTypes() {
        return searchFileTypes.getValue();
    }

    public ObservableList<SearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(final ObservableList<SearchResult> searchResultsVal) {
        searchResults = searchResultsVal;
    }

    /**
     * @return the prevSearchPos
     */
    public int getPrevSearchPos() {
        return prevSearchPos;
    }

    /**
     * @param prevSearchPosVal the prevSearchPos to set
     */
    public void setPrevSearchPos(final int prevSearchPosVal) {
        this.prevSearchPos = prevSearchPosVal;
    }

    private static String sysDrv() {
        try {
            if (OSChecker.isWindows()) {
                return System.getenv("SystemDrive") + "\\";
            } else {
                return "/";
            }
        } catch (Exception e) {
            return "";
        }
    }
}

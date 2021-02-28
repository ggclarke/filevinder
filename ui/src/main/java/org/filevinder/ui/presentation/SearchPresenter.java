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
package org.filevinder.ui.presentation;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.collections.ObservableList;
import org.filevinder.ui.IOData;
import static org.filevinder.ui.UIConstants.SEARCH_FILE_TYPES_DEFAULT;
import static org.filevinder.ui.UIConstants.SEARCH_LOCATION_DEFAULT;
import static org.filevinder.ui.UIConstants.SEARCH_TEXT_DEFAULT;

/**
 * The presenter sets the output data on the view, and serves as a bridge
 * between the UI and th BL.
 *
 * @author Gregory Clarke
 */
public final class SearchPresenter {

    private final SearchModel model;
    private SearchView view;

    /**
     * Populates the UI layer with data passed from the Interactor.
     *
     * @param modelArg model
     */
    public SearchPresenter(final SearchModel modelArg) {
        model = modelArg;
    }

    public void setSearchView(final SearchView viewArg){
        view = viewArg;
    }

    /**
     * Populates the UI with search results.
     *
     * @param outputData search results
     */
    public void presentSearch(final IOData outputData) {

        ArrayList<HashMap<String, String>> searchResults = outputData.getTable("searchResults");

        if (searchResults == null) {
            System.out.println("No results Found.");
            return;
        }

        ObservableList<SearchResultModel> results = model.getSearchResults();
        //TODO: fix this so that multiple searches don't append to table in UI
        System.out.println("DEBUG-SIZE-BEFORE" + model.getSearchResults().size());
        results.clear();
        System.out.println("DEBUG-SIZE-AFTER" + model.getSearchResults().size());

        for (HashMap<String, String> row : searchResults) {
            String fileName = row.get("fileName");
            String absolutePath = row.get("absolutePath");
            String file = row.get("file");
            String fileType = row.get("fileType");
            String lastModified = row.get("lastModified");
            String creationTime = row.get("creationTime");
            String accessedTime = row.get("accessedTime");
            String permissions = row.get("permissions");

            results.add(
                    new SearchResultModel(fileName, absolutePath, file, fileType,
                            lastModified, creationTime, accessedTime, permissions)
            );
        }

        //TODO: Is this or the view object required?
        view.refreshTable(results);
    }

    /**
     * Populates the UI with search data.
     *
     * @param outputData iodata
     */
    public void presentSearchData(final IOData outputData) {
        model.searchLocationProperty().setValue(outputData.getProperty("searchLocation"));
        model.searchFileTypesProperty().setValue(outputData.getProperty("searchFileTypes"));
        model.searchTextProperty().setValue(outputData.getProperty("searchText"));
    }

    /**
     * Clears search details from the UI.
     *
     * @param outputData iodata
     */
    public void presentClear(final IOData outputData) {
        model.searchLocationProperty().setValue(SEARCH_LOCATION_DEFAULT);
        model.searchFileTypesProperty().setValue(SEARCH_FILE_TYPES_DEFAULT);
        model.searchTextProperty().setValue(SEARCH_TEXT_DEFAULT);
    }

    /**
     * Populates the UI with next search.
     *
     * @param outputData iodata
     */
    public void presentNext(final IOData outputData) {
        model.setPrevSearchPos(Integer.parseInt(outputData.getProperty("prevSearchPos")));
    }

    /**
     * Populates the UI with prev search.
     *
     * @param outputData iodata
     */
    public void presentPrev(final IOData outputData) {
        model.setPrevSearchPos(Integer.parseInt(outputData.getProperty("prevSearchPos")));
    }
}

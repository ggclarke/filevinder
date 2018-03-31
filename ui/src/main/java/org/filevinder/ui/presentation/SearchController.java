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

import org.filevinder.ui.usecase.SearchInteractor;
import org.filevinder.ui.IOData;

/**
 *
 * @author Gregory Clarke
 */
public final class SearchController {

    private final SearchModel model;
    private final SearchInteractor interactor;

    /**
     * The controller gets values from the view and passes them on to the
     * interactor using a UI agnostic datastructure.
     * @param searchModel view model
     * @param interactorArg the interactor
     */
    public SearchController(final SearchModel searchModel, final SearchInteractor interactorArg) {
        model = searchModel;
        interactor = interactorArg;
    }

    /**
     * Action that gets invoked when the search button is pressed.
     */
    public void search() {
        IOData inputData = new IOData();
        inputData.putProperty("searchLocation", model.getSearchLocation());
        inputData.putProperty("searchFileTypes", model.getSearchFileTypes());
        inputData.putProperty("searchText", model.getSearchText());
        interactor.search(inputData);
    }

    /**
     * Action that gets invoked when the search button is pressed.
     */
    public void prev() {
        IOData inputData = new IOData();
        inputData.putProperty("searchTextProperty", model.searchTextProperty().getValue());
        inputData.putProperty("prevSearchPos", Integer.toString(model.getPrevSearchPos()));
        interactor.prev(inputData);
    }

    /**
     * Action that gets invoked when the search button is pressed.
     */
    public void next() {
        IOData inputData = new IOData();
        inputData.putProperty("prevSearchPos", Integer.toString(model.getPrevSearchPos()));
        interactor.next(inputData);
    }

    /**
     * Action that gets invoked when the search button is pressed.
     */
    public void clear() {
        interactor.clear();
    }

}

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
package org.filevinder.ui.controller;

import java.io.IOException;
import static java.lang.System.err;
import static java.lang.System.out;
import org.filevinder.ui.model.Model;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.filevinder.factory.SearchFactory;
import org.filevinder.interfaces.Search;
import org.filevinder.ui.Constants;
import static org.filevinder.ui.Constants.DATE_FORMAT;
import static org.filevinder.ui.model.Model.SEARCH_FILE_TYPES_DEFAULT;
import static org.filevinder.ui.model.Model.SEARCH_LOCATION_DEFAULT;
import static org.filevinder.ui.model.Model.SEARCH_TEXT_DEFAULT;
import org.filevinder.ui.model.SearchResult;
import org.filevinder.ui.utils.FSUtils;
import org.filevinder.ui.utils.SearchData;
import org.filevinder.ui.utils.SearchHistory;
import static org.filevinder.ui.utils.SearchHistory.isValidPos;
import org.filevinder.ui.view.ViewException;

/**
 * Static actions called by command features on the UI. The controller is called
 * from the View. The SearchController updates the Model.
 *
 * @author Gregory Clarke
 */
public final class SearchController {

    private SearchController() {
    }

    /**
     * Action that gets invoked when the search button is pressed.
     */
    public static void search() {
        Model model = Model.getInstance();
        Search search = SearchFactory.getInstance();

        //TODO: validations on these
        String rootPath = model.searchLocationProperty().getValue();
        String glob = model.searchFileTypesProperty().getValue();
        String pattern = model.searchTextProperty().getValue();

        cacheSearchDetails(rootPath, glob, pattern);

        out.printf("Search parameters: %s, %s, %s%n", rootPath, glob, pattern);
        List<Path> results = search.findFile(rootPath, glob);

        out.printf("%s files to search in %s%n", results.size(), rootPath);
        List<Path> matches = results.stream().parallel()
                .filter(path -> search.searchFile(path, pattern, Constants.CHARSET))
                .collect(Collectors.toList());

        out.printf("Found matches in %s files%n", matches.size());
        matches.forEach((path) -> out.println(path.toString()));

        matches.forEach((path) -> model.getSearchResults().add(new SearchResult(
                path.getFileName().toString(),
                path.toAbsolutePath().toString(),
                path.toFile().length() / 1000 + "kB",
                FSUtils.getFileType(path.toFile()),
                (new SimpleDateFormat(DATE_FORMAT)).format(new Date(path.toFile().lastModified())),
                FSUtils.getCreationTime(path),
                FSUtils.getAccessedTime(path),
                FSUtils.getPermissions(path)
        )));
    }

    private static void cacheSearchDetails(final String rootPath, final String glob, final String pattern) {
        SearchData searchData = new SearchData(pattern, rootPath, glob);
        try {
            SearchHistory.persist(searchData);
        } catch (IOException ioe) {
            err.println("Could not persist the last search: " + searchData.toString());
            ioe.printStackTrace();
        }
    }

    /**
     * Action that gets invoked when the search button is pressed.
     */
    public static void prev() {
        Model model = Model.getInstance();
        String search = model.searchTextProperty().getValue();
        int searchPos = model.getPrevSearchPos();
        try {
            int newPos = searchPos + 1;

            if (searchPos == 0 && isEmpty(search)) {
                newPos = searchPos;
            } else if (isValidPos(newPos)) {
                model.setPrevSearchPos(newPos);
            } else {
                newPos = searchPos;
            }

            setSearchData(newPos, model);
            out.println("'Prev' button pressed (" + newPos + ")");

        } catch (IOException ioe) {
            err.println("Could not retrieve previous search.");
            ioe.printStackTrace();
        } catch (ViewException ve) {
            err.println(ve.getMessage());
        }
    }

    private static boolean isEmpty(String search) {
        return search == null || search.length() == 0;
    }

    /**
     * Action that gets invoked when the search button is pressed.
     */
    public static void next() {
        Model model = Model.getInstance();
        int searchPos = model.getPrevSearchPos();

        try {
            int newPos = searchPos - 1;
            if (SearchHistory.isValidPos(newPos)) {
                model.setPrevSearchPos(newPos);
            } else {
                newPos = searchPos;
            }

            setSearchData(newPos, model);
            out.println("'Next' button pressed (" + newPos + ")");

        } catch (IOException ioe) {
            err.println("Could not retrieve previous search.");
            ioe.printStackTrace();
        } catch (ViewException ve) {
            err.println(ve.getMessage());
        }
    }

    /**
     * Action that gets invoked when the search button is pressed.
     */
    public static void clear() {
        out.println("'Clear' button pressed");
        Model model = Model.getInstance();
        model.searchLocationProperty().setValue(SEARCH_LOCATION_DEFAULT);
        model.searchFileTypesProperty().setValue(SEARCH_FILE_TYPES_DEFAULT);
        model.searchTextProperty().setValue(SEARCH_TEXT_DEFAULT);
    }

    private static void setSearchData(final int searchPos, final Model model) throws IOException, ViewException {
        SearchData searchData = SearchHistory.retrievePrev(searchPos);
        if (searchData == null) {
            throw new ViewException("Invalid search position " + searchPos);
        }
        model.searchLocationProperty().setValue(searchData.getSearchLocation());
        model.searchFileTypesProperty().setValue(searchData.getSearchFileTypes());
        model.searchTextProperty().setValue(searchData.getSearchText());
    }

}

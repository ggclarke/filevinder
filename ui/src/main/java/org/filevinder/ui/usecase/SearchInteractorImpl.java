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
package org.filevinder.ui.usecase;

import org.filevinder.ui.presentation.SearchPresenter;
import java.io.IOException;
import static java.lang.System.err;
import static java.lang.System.out;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.filevinder.factory.SearchFactory;
import org.filevinder.interfaces.Search;
import org.filevinder.ui.UIConstants;
import org.filevinder.ui.UIException;
import static org.filevinder.ui.UIConstants.DATE_FORMAT;
import org.filevinder.ui.interactor.utils.FSUtils;
import org.filevinder.ui.IOData;
import static org.filevinder.ui.UIConstants.SEARCH_FILE_TYPES_DEFAULT;
import static org.filevinder.ui.UIConstants.SEARCH_LOCATION_DEFAULT;
import static org.filevinder.ui.UIConstants.SEARCH_TEXT_DEFAULT;
import org.filevinder.ui.presentation.SearchInteractor;

/**
 * Static actions called by command features on the UI. The interactor is called
 * from the UI. The interactor reads from InputData and writes to OutputData.
 *
 * @author Gregory Clarke
 */
public final class SearchInteractorImpl implements SearchInteractor {

    private final SearchPresenter presenter;
    private final SearchHistory hist;
    private static final int KB_DIVISOR = 1000;

    /**
     * The interactor server as a bridge between the UI layer and the BL Layer.
     *
     * @param presenterArg The presenter
     * @param searchHistory Cached searches
     */
    public SearchInteractorImpl(final SearchPresenter presenterArg,
            final SearchHistory searchHistory) {
        presenter = presenterArg;
        hist = searchHistory;
    }

    /**
     * Action that gets invoked when the search button is pressed.
     *
     * @param inputData The input data structure
     */
    @Override
    public void search(final IOData inputData) {
        Search search = SearchFactory.getInstance();

        //TODO: validations on these
        String rootPath = inputData.getProperty("searchLocation");
        String glob = inputData.getProperty("searchFileTypes");
        String pattern = inputData.getProperty("searchText");

        cacheSearchDetails(rootPath, glob, pattern);

        out.printf("Search parameters: %s, %s, %s%n", rootPath, glob, pattern);
        List<Path> results = search.findFile(rootPath, glob);

        out.printf("%s files to search in %s%n", results.size(), rootPath);
        List<Path> matches = results.stream().parallel()
                .filter(path -> search.searchFile(path, pattern, UIConstants.CHARSET))
                .collect(Collectors.toList());

        out.printf("Found matches in %s files%n", matches.size());
        matches.forEach((path) -> out.println(path.toString()));

        IOData outputData = new IOData();
        ArrayList<HashMap<String, Object>> searchResults = new ArrayList<>();
        matches.forEach((path)
                -> outputData.putRowInTable("searchResults", createSearchResult(path))
        );

        presenter.presentSearch(outputData);
    }

    /**
     * Action that gets invoked when the search button is pressed.
     *
     * @param inputData The input data structure
     */
    @Override
    public void prev(final IOData inputData) {
        String search = inputData.getProperty("searchText");
        int searchPos = Integer.parseInt(inputData.getProperty("prevSearchPos"));

        try {
            int newPos = searchPos + 1;

            if (searchPos == 0 && isEmpty(search)) {
                newPos = searchPos;
            } else if (hist.isValidPos(newPos)) {
                IOData outputData = new IOData();
                outputData.putProperty("prevSearchPos", Integer.toString(newPos));
            } else {
                newPos = searchPos;
            }

            setSearchData(newPos);
            out.println("'Prev' button pressed (" + newPos + ")");

        } catch (IOException | UIException e) {
            err.println("Could not retrieve previous search.");
            e.printStackTrace();
        }
    }

    /**
     * Action that gets invoked when the search button is pressed.
     *
     * @param inputData The input data structure
     */
    @Override
    public void next(final IOData inputData) {
        int searchPos = Integer.parseInt(inputData.getProperty("prevSearchPos"));

        try {
            int newPos = searchPos - 1;
            if (hist.isValidPos(newPos)) {
                IOData outputData = new IOData();
                outputData.putProperty("prevSearchPos", Integer.toString(newPos));
                presenter.presentNext(outputData);
            } else {
                newPos = searchPos;
            }

            setSearchData(newPos);
            out.println("'Next' button pressed (" + newPos + ")");

        } catch (IOException | UIException e) {
            err.println("Could not retrieve previous search.");
            e.printStackTrace();
        }
    }

    /**
     * Action that gets invoked when the search button is pressed.
     */
    @Override
    public void clear() {
        IOData outputData = new IOData();
        out.println("'Clear' button pressed");

        outputData.putProperty("searchLocation", SEARCH_LOCATION_DEFAULT);
        outputData.putProperty("searchFileTypes", SEARCH_FILE_TYPES_DEFAULT);
        outputData.putProperty("searchText", SEARCH_TEXT_DEFAULT);

        presenter.presentClear(outputData);
    }

    private void setSearchData(final int searchPos) throws IOException, UIException {

        IOData outputData = new IOData();
        CachedSearchData searchData = hist.retrievePrev(searchPos);

        if (searchData == null) {
            throw new UIException("Invalid search position " + searchPos);
        }

        outputData.putProperty("searchLocation", searchData.getSearchLocation());
        outputData.putProperty("searchFileTypes", searchData.getSearchFileTypes());
        outputData.putProperty("searchText", searchData.getSearchText());

        presenter.presentSearchData(outputData);
    }

    private HashMap<String, String> createSearchResult(final Path path) {
        HashMap<String, String> res = new HashMap<>();

        res.put("fileName", path.getFileName().toString());
        res.put("absolutePath", path.toAbsolutePath().toString());
        res.put("file", path.toFile().length() / KB_DIVISOR + "kB");
        res.put("fileType", FSUtils.getFileType(path.toFile()));
        res.put("lastModified", (new SimpleDateFormat(DATE_FORMAT)).format(new Date(path.toFile().lastModified())));
        res.put("creationTime", FSUtils.getCreationTime(path));
        res.put("accessedTime", FSUtils.getAccessedTime(path));
        res.put("permissions", FSUtils.getPermissions(path));

        return res;
    }

    private void cacheSearchDetails(final String rootPath, final String glob, final String pattern) {
        CachedSearchData searchData = new CachedSearchData(pattern, rootPath, glob);
        try {
            hist.persist(searchData);
        } catch (IOException ioe) {
            err.println("Could not persist the last search: " + searchData.toString());
            ioe.printStackTrace();
        }
    }

    private boolean isEmpty(final String search) {
        return search == null || search.length() == 0;
    }

}

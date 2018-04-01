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
package org.filevinder.ui.fs;

import org.filevinder.ui.usecase.CachedSearchData;
import org.filevinder.ui.usecase.SysPropsProvider;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.filevinder.interfaces.SysProps;
import static org.filevinder.ui.usecase.CachedSearchData.TOKEN;
import org.filevinder.ui.usecase.SearchHistory;

/**
 *
 * @author Gregory Clarke
 */
public final class SearchHistoryStore implements SearchHistory {

    private static final Charset CH_SET = StandardCharsets.UTF_8;
    private static final String NL = "\n";

    /**
     * The maximum number of past searches that are kept in the cache.
     */
    public static final int HIST_MAX = 10;

    /**
     * Persist a search to the cache of past searches.
     *
     * @param searchData a past search
     * @throws IOException IO exceptions
     */
    @Override
    public synchronized void persist(final CachedSearchData searchData) throws IOException {
        Path path = getFile();

        Files.write(path,
                (searchData.toString() + NL).getBytes(CH_SET),
                Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);

        truncateOld(path);
    }

    /**
     * Deletes the file used for caching searches.
     *
     * @throws IOException IO exception
     */
    @Override
    public void delete() throws IOException {
        Path path = getFile();
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }

    /**
     * Returns the most recent cached searches. With most recent item first.
     *
     * @return list of searches
     * @throws IOException IO exceptions
     */
    @Override
    public List<String> retrieveRaw() throws IOException {
        List<String> res = read();
        Collections.reverse(res);
        return res;
    }

    /**
     * Returns the most recent cached searches. With most recent item first.
     *
     * @return list of searches
     * @throws IOException IO exceptions
     */
    @Override
    public List<CachedSearchData> retrieve() throws IOException {
        List<String> res = retrieveRaw();
        ArrayList<CachedSearchData> resObj = new ArrayList<>(res.size());
        for (String s : res) {
            String[] stringArr = s.split(TOKEN, -1);
            CachedSearchData data = new CachedSearchData(stringArr[0], stringArr[1], stringArr[2]);
            resObj.add(data);
        }
        return resObj;
    }

    /**
     * Returns the most recent cached search.
     *
     * @return list of searches
     * @throws IOException IO exceptions
     * @param pos specifies how many searches back
     */
    @Override
    public CachedSearchData retrievePrev(final int pos) throws IOException {
        List<String> res = retrieveRaw();
        if (!isValidPos(pos)) {
            return null;
        }
        String[] stringArr = res.get(pos).split(TOKEN, -1);
        return new CachedSearchData(stringArr[0], stringArr[1], stringArr[2]);
    }

    /**
     * Check is position exists in the cache.
     *
     * @return if pos is valid
     * @throws IOException IO exceptions
     * @param pos specifies how many searches back
     */
    @Override
    public boolean isValidPos(final int pos) throws IOException {
        List<String> res = retrieveRaw();
        if ((res.size() <= 0) || (pos >= res.size()) || (pos < 0)) {
            return false;
        } else {
            return true;
        }
    }

    private static List<String> read() throws IOException {
        Path path = getFile();
        if (!Files.exists(path)) {
            return new ArrayList(0);
        } else {
            return Files.readAllLines(path, CH_SET);
        }
    }

    private static Path getFile() {
        String cacheFile = new SysPropsProvider().getSysProp(SysProps.FV_SEARCH_CACHE_FILE.toString());
        return Paths.get(cacheFile);
    }

    private static synchronized void truncateOld(final Path path) throws IOException {
        ArrayList<String> lines = (ArrayList) read();
        if (lines.size() > HIST_MAX) {
            String txt = truncatedCache(lines);
            Files.write(path, txt.getBytes(CH_SET), StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    private static String truncatedCache(final ArrayList<String> lines) {
        int start = 0;
        if (lines.size() > HIST_MAX) {
            start = lines.size() - HIST_MAX;
        }

        StringBuilder txt = new StringBuilder();
        for (int i = start; i < lines.size(); i++) {
            txt.append(lines.get(i)).append(NL);
        }
        return txt.toString();
    }

}

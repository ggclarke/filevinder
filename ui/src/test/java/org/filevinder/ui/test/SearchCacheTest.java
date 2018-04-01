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
package org.filevinder.ui.test;

import java.io.IOException;
import java.util.List;
import org.filevinder.interfaces.SysProps;
import org.filevinder.ui.usecase.CachedSearchData;
import static org.filevinder.ui.usecase.CachedSearchData.TOKEN;
import org.filevinder.ui.fs.SearchHistoryStore;
import static org.filevinder.ui.fs.SearchHistoryStore.HIST_MAX;
import org.filevinder.ui.usecase.SearchHistory;
import org.filevinder.ui.usecase.SysPropsProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Gregory Clarke
 */
public final class SearchCacheTest {

    @Before
    public void setUp() {
        SysPropsProvider sysProps = mock(SysPropsProvider.class);
        String tmp = System.getProperty("java.io.tmpdir");
        when(sysProps.getSysProp(SysProps.FV_SEARCH_CACHE_FILE.toString())).
                thenReturn(tmp + "\\fv_test.txt");
    }

    @Test
    public void addToSearchHistory() {
        SearchHistory hist = new SearchHistoryStore();

        try {
            hist.delete();
            String searchTxt = "search text 1";
            CachedSearchData srch = new CachedSearchData(searchTxt);
            hist.persist(srch);
            List<String> list = hist.retrieveRaw();
            Assert.assertEquals(searchTxt + TOKEN + TOKEN, list.get(0));

        } catch (IOException ioe) {
            Assert.fail();
        }
    }

    @Test
    public void exceedMaxSearchHistory() {
        SearchHistory hist = new SearchHistoryStore();
        try {
            hist.delete();
            String searchTxt = "search text";
            for (int i = 0; i < SearchHistoryStore.HIST_MAX + 5; i++) {
                hist.persist(new CachedSearchData(searchTxt));
            }
            List<String> list = hist.retrieveRaw();
            Assert.assertEquals(SearchHistoryStore.HIST_MAX, list.size());

        } catch (IOException ioe) {
            Assert.fail();
        }
    }

    @Test
    public void rollSearchHistory() {
        SearchHistory hist = new SearchHistoryStore();
        try {
            final int offset = 5;
            hist.delete();
            String searchTxt = "search text";
            for (int i = 0; i < HIST_MAX + offset; i++) {
                hist.persist(new CachedSearchData(searchTxt + i));
            }
            List<String> list = hist.retrieveRaw();

            Assert.assertEquals("search text" + (HIST_MAX + offset - 1) + ",,", list.get(0));
            Assert.assertEquals("search text" + offset + TOKEN + TOKEN, list.get(HIST_MAX - 1));

        } catch (IOException ioe) {
            Assert.fail();
        }
    }

    @Test
    public void retrieveMarshalledSearchHistory() {
        SearchHistory hist = new SearchHistoryStore();
        try {
            hist.delete();

            String searchTxt1 = "search text1", searchLoc1 = "temp1", searchTyp1 = "*.txt1";
            String searchTxt2 = "search text2", searchLoc2 = "temp2", searchTyp2 = "*.txt2";
            hist.persist(new CachedSearchData(searchTxt1, searchLoc1, searchTyp1));
            hist.persist(new CachedSearchData(searchTxt2, searchLoc2, searchTyp2));

            List<CachedSearchData> list = hist.retrieve();

            Assert.assertEquals(list.get(1).getSearchText(), searchTxt1);
            Assert.assertEquals(list.get(1).getSearchLocation(), searchLoc1);
            Assert.assertEquals(list.get(1).getSearchFileTypes(), searchTyp1);

            Assert.assertEquals(list.get(0).getSearchText(), searchTxt2);
            Assert.assertEquals(list.get(0).getSearchLocation(), searchLoc2);
            Assert.assertEquals(list.get(0).getSearchFileTypes(), searchTyp2);

            hist.persist(new CachedSearchData(searchTxt1, searchLoc1, searchTyp1));

        } catch (IOException ioe) {
            Assert.fail();
        }
    }

    @Test
    public void testMostRecentNullIsReturned() {
        SearchHistory hist = new SearchHistoryStore();
        try {
            hist.delete();
            CachedSearchData last = hist.retrievePrev(0);
            Assert.assertNull(last);

        } catch (IOException ioe) {
            Assert.fail();
        }
    }

    @Test
    public void testMostRecentIsReturned() {
        SearchHistory hist = new SearchHistoryStore();
        try {
            String str1 = "search text 1", str2 = "search text 2", str3 = "search text 3";
            hist.delete();
            hist.persist(new CachedSearchData(str1));
            hist.persist(new CachedSearchData(str2));
            hist.persist(new CachedSearchData(str3));

            Assert.assertEquals(str3, hist.retrievePrev(0).getSearchText());
            Assert.assertEquals(str2, hist.retrievePrev(1).getSearchText());
            Assert.assertEquals(str1, hist.retrievePrev(2).getSearchText());

        } catch (IOException ioe) {
            Assert.fail();
        }
    }

    @Test
    public void deleteWhenNoFilePresent() {
        SearchHistory hist = new SearchHistoryStore();
        try {
            hist.delete();
            hist.delete();
        } catch (IOException ioe) {
            Assert.fail();
        }
    }

    @Test
    public void retrieveWhenNoFilePresent() {
        SearchHistory hist = new SearchHistoryStore();
        try {
            hist.delete();
            List<String> ll = hist.retrieveRaw();
            Assert.assertEquals(0, ll.size());

        } catch (IOException ioe) {
            ioe.printStackTrace();
            Assert.fail();
        }
    }

}

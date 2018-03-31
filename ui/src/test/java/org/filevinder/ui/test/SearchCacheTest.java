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
import org.filevinder.ui.presentation.SearchDataModel;
import static org.filevinder.ui.presentation.SearchDataModel.TOKEN;
import org.filevinder.ui.interactor.utils.SearchHistory;
import static org.filevinder.ui.interactor.utils.SearchHistory.HIST_MAX;
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
        try {
            SearchHistory.delete();
            String searchTxt = "search text 1";
            SearchDataModel srch = new SearchDataModel(searchTxt);
            SearchHistory.persist(srch);
            List<String> list = SearchHistory.retrieveRaw();
            Assert.assertEquals(searchTxt + TOKEN + TOKEN, list.get(0));

        } catch (IOException ioe) {
            Assert.fail();
        }
    }

    @Test
    public void exceedMaxSearchHistory() {
        try {
            SearchHistory.delete();
            String searchTxt = "search text";
            for (int i = 0; i < SearchHistory.HIST_MAX + 5; i++) {
                SearchHistory.persist(new SearchDataModel(searchTxt));
            }
            List<String> list = SearchHistory.retrieveRaw();
            Assert.assertEquals(SearchHistory.HIST_MAX, list.size());

        } catch (IOException ioe) {
            Assert.fail();
        }
    }

    @Test
    public void rollSearchHistory() {
        try {
            final int offset = 5;
            SearchHistory.delete();
            String searchTxt = "search text";
            for (int i = 0; i < HIST_MAX + offset; i++) {
                SearchHistory.persist(new SearchDataModel(searchTxt + i));
            }
            List<String> list = SearchHistory.retrieveRaw();

            Assert.assertEquals("search text" + (HIST_MAX + offset - 1) + ",,", list.get(0));
            Assert.assertEquals("search text" + offset + TOKEN + TOKEN, list.get(HIST_MAX - 1));

        } catch (IOException ioe) {
            Assert.fail();
        }
    }

    @Test
    public void retrieveMarshalledSearchHistory() {
        try {
            SearchHistory.delete();

            String searchTxt1 = "search text1", searchLoc1 = "temp1", searchTyp1 = "*.txt1";
            String searchTxt2 = "search text2", searchLoc2 = "temp2", searchTyp2 = "*.txt2";
            SearchHistory.persist(new SearchDataModel(searchTxt1, searchLoc1, searchTyp1));
            SearchHistory.persist(new SearchDataModel(searchTxt2, searchLoc2, searchTyp2));

            List<SearchDataModel> list = SearchHistory.retrieve();

            Assert.assertEquals(list.get(1).getSearchText(), searchTxt1);
            Assert.assertEquals(list.get(1).getSearchLocation(), searchLoc1);
            Assert.assertEquals(list.get(1).getSearchFileTypes(), searchTyp1);

            Assert.assertEquals(list.get(0).getSearchText(), searchTxt2);
            Assert.assertEquals(list.get(0).getSearchLocation(), searchLoc2);
            Assert.assertEquals(list.get(0).getSearchFileTypes(), searchTyp2);

            SearchHistory.persist(new SearchDataModel(searchTxt1, searchLoc1, searchTyp1));

        } catch (IOException ioe) {
            Assert.fail();
        }
    }

    @Test
    public void testMostRecentNullIsReturned() {
        try {
            SearchHistory.delete();
            SearchDataModel last = SearchHistory.retrievePrev(0);
            Assert.assertNull(last);

        } catch (IOException ioe) {
            Assert.fail();
        }
    }

    @Test
    public void testMostRecentIsReturned() {
        try {
            String str1 = "search text 1", str2 = "search text 2", str3 = "search text 3";
            SearchHistory.delete();
            SearchHistory.persist(new SearchDataModel(str1));
            SearchHistory.persist(new SearchDataModel(str2));
            SearchHistory.persist(new SearchDataModel(str3));

            Assert.assertEquals(str3, SearchHistory.retrievePrev(0).getSearchText());
            Assert.assertEquals(str2, SearchHistory.retrievePrev(1).getSearchText());
            Assert.assertEquals(str1, SearchHistory.retrievePrev(2).getSearchText());

        } catch (IOException ioe) {
            Assert.fail();
        }
    }

    @Test
    public void deleteWhenNoFilePresent() {
        try {
            SearchHistory.delete();
            SearchHistory.delete();
        } catch (IOException ioe) {
            Assert.fail();
        }
    }

    @Test
    public void retrieveWhenNoFilePresent() {
        try {
            SearchHistory.delete();
            List<String> ll = SearchHistory.retrieveRaw();
            Assert.assertEquals(0, ll.size());

        } catch (IOException ioe) {
            ioe.printStackTrace();
            Assert.fail();
        }
    }

}

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
package org.filevinder.ui.it;

import java.util.SortedSet;
import org.junit.Assert;
import org.junit.Test;
import static org.filevinder.interfaces.SysProps.FV_TEST_ROOT;
import org.filevinder.ui.fs.FilesUtilImpl;
import org.filevinder.ui.presentation.FilesUtil;

/**
 *
 * @author Gregory Clarke
 */
public final class UtilsIT {

    @Test
    public void testFoldersInCurrentPathFmt1() {
        FilesUtil fsUtil = new FilesUtilImpl();
        String pathContext = System.getenv(FV_TEST_ROOT.toString());
        SortedSet<String> set = fsUtil.foldersInCurrentPath(pathContext);
        Assert.assertTrue(set.contains("testfiles1"));
        Assert.assertTrue(set.contains("testfiles2"));
    }

    @Test
    public void testFoldersInCurrentPathFmt2() {
        FilesUtil fsUtil = new FilesUtilImpl();
        String pathContext = removeLastSlash(System.getenv(FV_TEST_ROOT.toString()));
        SortedSet<String> set = fsUtil.foldersInCurrentPath(pathContext);
        Assert.assertTrue(set.contains("testfiles"));
        Assert.assertTrue(set.contains("eng"));
        Assert.assertTrue(set.contains("ui"));
    }

    @Test
    public void testFoldersInCurrentPathFmt3() {
        FilesUtil fsUtil = new FilesUtilImpl();
        String pathContext = toBackSlash(System.getenv(FV_TEST_ROOT.toString()));
        SortedSet<String> set = fsUtil.foldersInCurrentPath(pathContext);
        Assert.assertTrue(set.contains("testfiles1"));
        Assert.assertTrue(set.contains("testfiles2"));
    }

    @Test
    public void testFoldersInCurrentPathFmt4() {
        FilesUtil fsUtil = new FilesUtilImpl();
        String pathContext = "c:\\";
        SortedSet<String> set = fsUtil.foldersInCurrentPath(pathContext);
        Assert.assertTrue(set.contains("Windows"));
    }

    @Test
    public void testFoldersInCurrentPathFmt5() {
        FilesUtil fsUtil = new FilesUtilImpl();
        String pathContext = trimLastNChars(System.getenv(FV_TEST_ROOT.toString()), 3);
        SortedSet<String> set = fsUtil.foldersInCurrentPath(pathContext);
        Assert.assertTrue(set.contains("testfiles"));
    }

    @Test
    public void testPartialFolderName1() {
        FilesUtil fsUtil = new FilesUtilImpl();
        String s = fsUtil.partialFolderName("c:\\xxx\\foo");
        Assert.assertEquals("foo", s);
    }

    @Test
    public void testPartialFolderName2() {
        FilesUtil fsUtil = new FilesUtilImpl();
        String s = fsUtil.partialFolderName("c:/xxx/foo");
        Assert.assertEquals("foo", s);
    }

    private String removeLastSlash(final String s) {
        if (s.charAt(s.length() - 1) == '/') {
            return s.substring(0, s.length() - 1);
        } else {
            return s;
        }
    }

    private String trimLastNChars(final String s, final int n) {
        return s.substring(0, s.length() - n);
    }

    private String toBackSlash(final String s) {
        return s.replace('/', '\\');
    }
}

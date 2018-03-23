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
package org.filevinder.engine.it;

import static java.lang.System.out;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.filevinder.core.SearchImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.filevinder.engine.test.Constants;

/**
 *
 * @author Gregory Clarke
 */
public final class SearchIT implements Constants {

    @Test
    public void findAsciiInFile() {
        String pat = "foobar";
        Path path = Paths.get(Constants.ANSI_TST);

        long start = System.currentTimeMillis();
        boolean found = (new SearchImpl()).searchFile(path, pat, StandardCharsets.ISO_8859_1);
        long end = System.currentTimeMillis();

//        out.printf("Found=%s, in %s ms %n",found,(end - start));
        assertTrue(found);
    }

    @Test
    public void findTwoByteCharInFile() {
        String pat = "foobar£";
        Path path = Paths.get(Constants.ANSI_TST);

        long start = System.currentTimeMillis();
        boolean found = (new SearchImpl()).searchFile(path, pat, StandardCharsets.ISO_8859_1);
        long end = System.currentTimeMillis();

        out.printf("Found=%s, in %s ms %n", found, (end - start));

        assertTrue(found);
    }

    @Test
    public void findTwoByteCharInFileWrongEncoding() {
        String pat = "foobar£";
        Path path = Paths.get(Constants.ANSI_TST);

        long start = System.currentTimeMillis();
        boolean found = (new SearchImpl()).searchFile(path, pat, StandardCharsets.UTF_8);
        long end = System.currentTimeMillis();

        out.printf("Found=%s, in %s ms %n", found, (end - start));

        assertFalse(found);
    }

    @Test
    public void findFourByteCharInFile() {
        String pat = "foobar£🜁";
        Path path = Paths.get(Constants.UTF8_MULTI_BYTE);

        long start = System.currentTimeMillis();
        boolean found = (new SearchImpl()).searchFile(path, pat, StandardCharsets.UTF_8);
        long end = System.currentTimeMillis();

        out.printf("Found=%s, in %s ms %n", found, (end - start));

        assertTrue(found);
    }

    @Test
    public void findInLargeFile() {
        String pat = "foobar";
        Path path = Paths.get(Constants.ANSI_LRG);

        long start = System.currentTimeMillis();
        boolean found = (new SearchImpl()).searchFile(path, pat, StandardCharsets.ISO_8859_1);
        long end = System.currentTimeMillis();
        out.printf("Found=%s, in %s ms %n", found, (end - start));

        assertTrue(found);
    }

    @Test
    public void findFileByPrefix() {
        String glob = "a.*";
        List<Path> results = (new SearchImpl()).findFile(TEST_FOLDER1, glob);
        assertEquals(ROOT_FILES.size(), results.size());
    }

    @Test
    public void findFileBySuffix() {
        String glob = "*.dat";
        List<Path> results = (new SearchImpl()).findFile(TEST_FOLDER1, glob);
        assertEquals(FILES_TOTAL, results.size());
    }

    @Test
    public void findMissingFile() {
        List<Path> results = (new SearchImpl()).findFile(TEST_FOLDER1, "xxx");
        assertEquals(0, results.size());
    }

}

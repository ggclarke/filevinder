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

import java.io.File;
import java.io.IOException;
import static java.lang.System.out;
import java.nio.file.Paths;
import java.util.ArrayList;
import static org.filevinder.common.Utils.UTF8;
import org.filevinder.core.SearchImpl;
import static org.filevinder.core.BinarySearch.findLine;
import static org.filevinder.core.BinarySearch.findTrigram;
import org.filevinder.core.FileTooBigException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.filevinder.engine.test.Constants;

/**
 *
 * @author Gregory Clarke
 */
public final class BinarySearchIT implements Constants {

    private File file;

    @Before
    public void init() throws IOException {
        file = new File(INDEX_TXT);
    }

//    @Test
//    public void findMiddleOfFile() throws IOException, BinarySearch.NoMiddleLineException {
//        String pat = "[io ]";
//        long pos = sf.middle();
//        assertEquals("Middle of file not in expected location", 163908, pos);
//        String midTrigram = sf.getString(pos, new byte[pat.length()]);
//        assertEquals("Middle trigram is not the expected value", pat, midTrigram);
//    }
    @Test
    public void testSearch() throws IOException, FileTooBigException {
        String pat = "[Ita]";
        String found = findTrigram(pat, UTF8, file);
        assertEquals(pat + ", was expected but not found. ", pat, found);

        pat = "[fac]";
        found = findTrigram(pat, UTF8, file);
        assertEquals(pat + ", was expected but not found. ", pat, found);

        pat = "[fic]";
        found = findTrigram(pat, UTF8, file);
        assertEquals(pat + ", was expected but not found. ", pat, found);

        pat = "[fug]";
        found = findTrigram(pat, UTF8, file);
        assertEquals(pat + ", was expected but not found. ", pat, found);
    }

    @Test
    public void testSearchFirst() throws IOException, FileTooBigException {
        String pat = "[ Na]";
        String found = findTrigram(pat, UTF8, file);
        assertEquals(pat + ", was expected but not found. ", pat, found);
    }

    @Test
    public void testSearchSecondLast() throws IOException, FileTooBigException {
        String pat = "[vol]";
        String found = findTrigram(pat, UTF8, file);
        assertEquals(pat + ", was expected but not found. ", pat, found);
    }

    @Test
    public void testSearchLast() throws IOException, FileTooBigException {
        String pat = "[x e]";
        String found = findTrigram(pat, UTF8, file);
        assertEquals(pat + ", was expected but not found. ", pat, found);
    }

    @Test
    public void testSearchMid() throws IOException, FileTooBigException {
        String pat = "[io.]";
        String found = findTrigram(pat, UTF8, file);
        assertEquals(pat + ", was expected but not found. ", pat, found);
    }

    @Test
    public void testMissingSearch() throws IOException, FileTooBigException {
        String pat = "[XXX]";
        String found = findTrigram(pat, UTF8, file);
        assertNull(found);
    }

    @Test
    public void testGetLine() throws IOException, FileTooBigException {
        String pat = "[io.]";
        String line = findLine(pat, UTF8, file);

        assertTrue(line.startsWith(pat));
        assertTrue("Line not ended with expected value", line.endsWith("(100)405"));
    }

    @Test
    public void testGetLineFirst() throws IOException, FileTooBigException {
        String pat = "[ Na]";
        String line = findLine(pat, UTF8, file);

        assertTrue(line.startsWith(pat));
        assertTrue("Line not ended with expected value", line.endsWith("(100)406"));
    }

    @Test
    public void testGetLineLast() throws IOException, FileTooBigException {
        String pat = "[x e]";
        String line = findLine(pat, UTF8, file);

        assertTrue(line.startsWith(pat));
        assertTrue("Line not ended with expected value", line.endsWith("(100)225"));
    }

    @Test
    public void testSearchNft() throws IOException, FileTooBigException {
        double average = binarySearch();
        out.println("Binary search duration was: " + average + "ms");

        average = linearSearch();
        out.println("Linear search duration was: " + average + "ms");

    }

    private double linearSearch() {
        String pat = "[sam]";
        int iterations = 250;
        long start, end, duration;
        ArrayList<Long> deltas = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            start = System.currentTimeMillis();
            (new SearchImpl()).searchFile(Paths.get(INDEX_TXT), pat, UTF8);
            end = System.currentTimeMillis();
            duration = end - start;
            deltas.add(duration);
        }
        long sum = 0;
        for (Long delta : deltas) {
            sum += delta;
        }
        double average = (double) sum / (double) iterations;
        return average;
    }

    private double binarySearch() throws IOException, FileTooBigException {
        String pat = "[sam]";
        int iterations = 250;
        long start, end, duration;
        ArrayList<Long> deltas = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            start = System.currentTimeMillis();
            String found = findTrigram(pat, UTF8, file);
            end = System.currentTimeMillis();
            duration = end - start;
            deltas.add(duration);
        }
        long sum = 0;
        for (Long delta : deltas) {
            sum += delta;
        }
        double average = (double) sum / (double) iterations;
        return average;
    }

}

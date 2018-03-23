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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.filevinder.core.InMemoryIndex;
import static org.filevinder.engine.test.Constants.IDX_ON_DISK;
import org.filevinder.interfaces.Index;
import static org.filevinder.common.Utils.getFiles;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;
import static org.filevinder.common.Utils.readEntireFile;
import org.filevinder.core.SearchImpl;
import static org.filevinder.engine.test.Constants.TEST_FOLDER1;
import org.filevinder.engine.test.Constants;

/**
 *
 * @author Gregory Clarke
 */
@Ignore
public final class NftIT {

    @Test
    public void findInUnindexedCollectionNFT() {
        String rootPath = "c:/testfiles3";
        String pattern = "foobar";
        Charset charset = Charset.defaultCharset();
        long start = System.currentTimeMillis();
        List<Path> matches = (new SearchImpl()).findPattern(rootPath, pattern, charset);
        long end = System.currentTimeMillis();
        out.printf("Duration= %s ms %n", (end - start));

        matches.forEach((p) -> {
            out.println(p);
        });

        assertTrue("Ensure unindexed multi file search less than 15sec", (end - start) < 15000);
    }

    @Test
    public void findUnindexedInLargeFileNFT() {
        String pat = "foobar";
        Path path = Paths.get(Constants.ANSI_LRG);
        int sum = 0;
        int iters = 10;
        for (int i = 0; i < iters; i++) {
            long start = System.currentTimeMillis();
            boolean found = (new SearchImpl()).searchFile(path, pat, StandardCharsets.ISO_8859_1);
            long end = System.currentTimeMillis();
            out.printf("Found=%s, in %s ms %n", found, (end - start));
            sum += (end - start);
        }
        int avg = (sum / iters);
        out.printf("Average search time =%s ms %n", avg);
        assertTrue("Ensure average file search time under 150ms", avg < 150);
    }

    @Test
    public void nftAverageTimeToWriteNewIndex() {
        Index parser = new InMemoryIndex();
        ArrayList<Long> durations = new ArrayList<>();
        final int iterations = 10;

        for (int i = 0; i < iterations; i++) {
            long start = System.currentTimeMillis();

            getFiles(TEST_FOLDER1, true)
                    .map(path -> readEntireFile(path, Charset.defaultCharset()))
                    .filter(x -> x != null)
                    .forEach(chunk -> parser.mergeIndex(chunk));

            parser.writeCompressedIndex(Paths.get(IDX_ON_DISK), "UTF-8");
            parser.purgeIndexFromMem();

            long end = System.currentTimeMillis();

//            out.printf("%n%nIndexParser NFT Duration %s ms%n%n",end - start);
            durations.add(end - start);
        }

        long sum = 0;
        for (int i = 0; i < iterations; i++) {
            sum += durations.get(i);
        }

        long avg = sum / iterations;
        out.printf("%n%nIndexParser NFT Average Duration %s ms%n%n", avg);

        assertTrue("Ensure avg durations is under 300ms", avg < 300);
    }

}

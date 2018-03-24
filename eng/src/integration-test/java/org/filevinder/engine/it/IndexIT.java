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

import org.filevinder.types.Chunk;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.System.err;
import static java.lang.System.out;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.filevinder.core.InMemoryIndex;
import static org.filevinder.common.Utils.UTF8;
import static org.junit.Assert.assertEquals;
import org.filevinder.types.Trigram;
import org.filevinder.types.FileRef;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.filevinder.interfaces.Index;
import static org.filevinder.common.Utils.getFiles;
import static org.filevinder.common.Utils.validateFile;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.filevinder.common.Utils.readEntireFile;
import static org.filevinder.engine.it.Constants.ANSI_LRG;
import static org.filevinder.engine.it.Constants.CHINESE_UTF_16;
import static org.filevinder.engine.it.Constants.CHINESE_UTF_8;
import static org.filevinder.engine.it.Constants.FILES_TOTAL;
import static org.filevinder.engine.it.Constants.INDEX_BYTE;
import static org.filevinder.engine.it.Constants.INDEX_TXT;
import static org.filevinder.engine.it.Constants.NO_LINES;
import static org.filevinder.engine.it.Constants.ROOT_FILES;
import static org.filevinder.engine.it.Constants.RUSSIAN_UTF_16;
import static org.filevinder.engine.it.Constants.RUSSIAN_UTF_8;
import static org.filevinder.engine.it.Constants.TEST_FOLDER1;
import static org.filevinder.engine.it.Constants.TXT;
import static org.filevinder.engine.it.Constants.UTF8_MULTI_BYTE;

/**
 *
 * @author Gregory Clarke
 */
public final class IndexIT{

    private static final int TRIGRAM_CNT = getTrigrams(TXT).size();

    @BeforeClass
    public static void setUpClass() throws IOException {

        final File testFolder = new File(TEST_FOLDER1);
        testFolder.mkdir();

        if (!testFolder.exists()) {
            throw new IOException("Could not create " + testFolder.getAbsolutePath());
        }

        out.println("Setting up test infrastructure in " + testFolder.getAbsolutePath());

        ROOT_FILES.stream().forEach(subFolder -> createTestFiles(TEST_FOLDER1 + "/" + subFolder));
    }

    private static void createTestFiles(final String folder) {

        File testLoc = new File(folder);
        testLoc.mkdir();

        ROOT_FILES.stream()
                .map(fileName -> new File(testLoc.getAbsolutePath() + fileName + ".dat"))
                .filter(file -> !file.exists())
                .forEach(file -> createTextFile(file, "UTF-8"));
    }

    private static void createTextFile(final File file, final String csn) {
        try {
            try (PrintWriter writer = new PrintWriter(file, csn)) {
                TXT.stream().forEach(line -> writer.println(line));
            }

        } catch (IOException ex) {
            err.println("Could not create text file " + file.getPath());
            ex.printStackTrace(err);
        }
    }

    @Test
    public void testGetFiles() {
        Stream<Path> files = getFiles(TEST_FOLDER1, true);
        assertEquals(FILES_TOTAL, files.count());
        assertTrue(files.isParallel());
        files.close();
    }

    @Test
    public void testGetFilesNoRecurse() {
        Stream<Path> files = getFiles(TEST_FOLDER1, false);
        assertEquals(0, files.count());
        files.close();
    }

    @Test
    public void testGetFileData() {
        List<Chunk> chunks = getFiles(TEST_FOLDER1, true)
                .map(path -> readEntireFile(path, Charset.defaultCharset()))
                .filter(x -> x != null).collect(Collectors.toList());

        assertEquals(FILES_TOTAL, chunks.size());
    }

    @Test
    public void testIndexBasics() {
        Index parser = new InMemoryIndex();
        getFiles(TEST_FOLDER1, true)
                .map(path -> readEntireFile(path, Charset.defaultCharset()))
                .filter(x -> x != null)
                .forEach(chunk -> parser.mergeIndex(chunk));

        List<Trigram> index = parser.getPostingList().getIndex();

        assertEquals(TRIGRAM_CNT, index.size());

        Trigram sed = new Trigram("Sed");

        assertTrue(index.contains(sed));

        Trigram sedTrigram = index.get(index.indexOf(sed));
        List<FileRef> fileRefs = sedTrigram.getFileRefs();

        assertEquals(FILES_TOTAL, fileRefs.size());

        out.printf("Numer of trigrams in test files = %s%n", index.size());

    }

    @Test
    public void testIndexStructure() {
        Index parser = new InMemoryIndex();
        getFiles(TEST_FOLDER1, true)
                .map(path -> readEntireFile(path, Charset.defaultCharset()))
                .filter(x -> x != null)
                .forEach(chunk -> parser.mergeIndex(chunk));

        List<Trigram> index = parser.getPostingList().getIndex();

        int errCnt = checkIndexVals(getTrigrams(TXT), index);

        if (errCnt > 0) {
            fail("The index was not generated correctly, see previous errors");
        }
    }

    @Test
    public void testReadWriteIndex() {
        Index index = new InMemoryIndex();
        getFiles(TEST_FOLDER1, true)
                .map(path -> readEntireFile(path, Charset.defaultCharset()))
                .filter(x -> x != null)
                .forEach(chunk -> index.mergeIndex(chunk));

        int indexSzBefore = index.getPostingList().size();

        File idxFile = new File(INDEX_BYTE);
        idxFile.delete();

        boolean write = index.writeCompressedIndex(Paths.get(INDEX_BYTE), "UTF-8");
        assertTrue(write);
        index.purgeIndexFromMem();
        index.memoizeIndexFile(Paths.get(INDEX_BYTE), "UTF-8");

        int errCnt = checkIndexVals(getTrigrams(TXT), index.getPostingList().getIndex());

        if (errCnt > 0) {
            fail("The index was not generated correctly, see previous errors");
        }

        assertEquals("Index size after serialization not same as before.",
                indexSzBefore, index.getPostingList().size());

    }

    @Test
    public void testMemPurge() {
        Index parser = new InMemoryIndex();
        getFiles(TEST_FOLDER1, true)
                .map(path -> readEntireFile(path, Charset.defaultCharset()))
                .filter(x -> x != null)
                .forEach(chunk -> parser.mergeIndex(chunk));

        parser.purgeIndexFromMem();
        assertEquals(0, parser.getPostingList().size());
    }

    @Test
    public void testWriteAsText() {
        Index parser = new InMemoryIndex();
        getFiles(TEST_FOLDER1, true)
                .map(path -> readEntireFile(path, Charset.defaultCharset()))
                .filter(x -> x != null)
                .forEach(chunk -> parser.mergeIndex(chunk));

        File idxFile = new File(INDEX_TXT);
        idxFile.delete();

        parser.writePlainIndex(Paths.get(INDEX_TXT), "UTF-8");
        assertTrue(idxFile.length() > 10);
    }

    @Test
    public void testWriteAsBytes() {
        Index parser = new InMemoryIndex();
        getFiles(TEST_FOLDER1, true)
                .map(path -> readEntireFile(path, Charset.defaultCharset()))
                .filter(x -> x != null)
                .forEach(chunk -> parser.mergeIndex(chunk));

        File idxFile = new File(INDEX_BYTE);
        idxFile.delete();

        parser.writeCompressedIndex(Paths.get(INDEX_BYTE), "UTF-8");
        assertTrue(idxFile.length() > 10);
    }

    @Test
    public void acceptValidFiles() {
        Charset charset = UTF8;
        Path path = Paths.get(UTF8_MULTI_BYTE);
        boolean isValid = validateFile(path, charset);
        assertTrue(isValid);

        path = Paths.get(ANSI_LRG);
        isValid = validateFile(path, charset);
        assertTrue(isValid);

        path = Paths.get(RUSSIAN_UTF_8);
        isValid = validateFile(path, charset);
        assertTrue(isValid);

        path = Paths.get(CHINESE_UTF_8);
        isValid = validateFile(path, charset);
        assertTrue(isValid);
    }

    @Test
    public void ignoreInvalidFilesWithNoLineBreaks() {
        Charset charset = UTF8;
        Path path = Paths.get(NO_LINES);
        boolean isValid = validateFile(path, charset);
        assertFalse(isValid);
    }

    @Test
    public void ignoreInvalidFilesNotUtf8Encoded() {
        Charset charset = UTF8;

        Path path = Paths.get(RUSSIAN_UTF_16);
        boolean isValid = validateFile(path, charset);
        assertFalse(isValid);

        path = Paths.get(CHINESE_UTF_16);
        isValid = validateFile(path, charset);
        assertFalse(isValid);
    }

    @Test
    public void validFilesUtf16Expected() {
        Charset charset = Charset.forName("UTF-16LE");

        Path path = Paths.get(RUSSIAN_UTF_16);
        boolean isValid = validateFile(path, charset);
        assertTrue(isValid);

        path = Paths.get(CHINESE_UTF_16);
        isValid = validateFile(path, charset);
        assertTrue(isValid);
    }

    private static HashSet<String> getTrigrams(final String str) {
        final int tri = 3;
        out.println("");
        HashSet<String> set = new HashSet<>();
        for (int i = 0; i < str.length(); i += tri) {
            int end = i + tri < str.length() ? i + tri : str.length();
            String token = str.substring(i, end);

            boolean lastToken = i + tri >= str.length();
            if (lastToken) {
                if (token.length() == 1) {
                    token = token + "  ";
                }
                if (token.length() == 2) {
                    token = token + " ";
                }
            }

            set.add(token);
        }

        return set;
    }

    private static HashSet<String> getTrigrams(final List<String> strList) {
        return getTrigrams(strList.stream().collect(Collectors.joining()));
    }

    private int checkIndexVals(final HashSet<String> reference, final List<Trigram> index) {
        int errCnt = 0;
        for (String s : reference) {
            if (!index.contains(new Trigram(s))) {
                out.printf("Generated index is missing trigram '%s'%n", s);
                errCnt++;

            } else if (!reference.contains(s)) {
                out.printf("Generated index erroneously included trigram '%s'%n", s);
                errCnt++;
            }
        }
        return errCnt;
    }

}

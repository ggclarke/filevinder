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
package org.filevinder.core;

import org.filevinder.interfaces.Search;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import static org.filevinder.common.Utils.getFiles;
import static org.filevinder.core.ErrorHandler.recordErr;

/**
 *
 * @author Gregory Clarke
 */
public final class SearchImpl implements Search {

    /**
     * Search the specified file for the String specified. Uses a fwd linear
     * scan and searches char by char. Algorithms like 'boyer moore' don't seem
     * to not work well on a file map stream.
     *
     * For most operating systems, mapping a file into memory is more expensive
     * than reading or writing a few tens of kilobytes of data via the usual
     * read and write methods. From the standpoint of performance it is
     * generally only worth mapping relatively large files into memory. Here we
     * set the min size limit for mapping to 50,000 bytes
     *
     * @param filePath The file to search
     * @param pattern The text to search for in the file
     * @param charset The character set to use for reading the file content
     * @return True if the pattern is found in the file content
     */
    @Override
    public boolean searchFile(final Path filePath, final String pattern,
            final Charset charset) {

        if (filePath == null || pattern == null || charset == null) {
            throw new NullPointerException("A provided argument was null");
        }

        boolean foundPat = false;
        final byte[] pat = pattern.getBytes(charset);
        final byte firstByte = pat[0];
        final int patLen = pat.length, patlenm1 = patLen - 1;
        final int thresholdSize = 50000;

        try (FileChannel text = FileChannel.open(filePath, StandardOpenOption.READ)) {

            final long textlen = text.size();

            if (textlen < thresholdSize) {
                foundPat = bufferedSearch(textlen, text, firstByte, patLen, pat, patlenm1);
            } else {
                foundPat = mappedSearch(textlen, patLen, text, firstByte, pat, patlenm1);
            }
        } catch (IOException ioe) {
            recordErr("Error retrieving files", ioe);
        }

        return foundPat;
    }

    @SuppressWarnings("empty-statement")
    private static boolean mappedSearch(final long textlen, final int patLen,
            final FileChannel text, final byte firstByte, final byte[] pat,
            final int patlenm1) throws IOException {

        final int bufferLen = 4 * 1024 * 1000; // Use a 4MB buffer
        int inc;
        boolean foundPat = false;
        MappedByteBuffer mappedTextBuf = null;
        long pos = 0;

        while (pos < textlen - patLen && !foundPat) {
            inc = (int) Math.min(bufferLen, textlen - pos); //shift
            mappedTextBuf = text.map(MapMode.READ_ONLY, pos, inc);
            pos += inc;

            while (mappedTextBuf.hasRemaining() && !foundPat) {
                try {
                    //skip loop - search for first byte
                    while (mappedTextBuf.get() != firstByte) {
                    }
                    //match - fwd linear scan
                    for (int j = 1; j < patLen; j++) {
                        if (mappedTextBuf.get() != pat[j]) {
                            break;
                        } else if (j == patlenm1) {
                            foundPat = true;
                            break;
                        }
                    }
                } catch (BufferUnderflowException e) {
                    break;
                }
            }
        }

        closeDirectBuffer(mappedTextBuf);

        //An alternative to the approach in closeDirectBufferis to suggest
        //a Systen.gc() run which is not guaranteed to execute immediately
        //System.gc();
        return foundPat;
    }

    /**
     * A note on why this is needed: A mapped byte buffer and the file mapping
     * that it represents remain valid until the buffer itself is
     * garbage-collected. This means that on Windows the file remains locked
     * until the MappedByteBuffer instance is garbage collected. For more
     * exposition see link below: -
     * https://bugs.java.com/bugdatabase/view_bug.do?bug_id=4715154
     *
     * @param buf The buffer to be closed
     */
    private static void closeDirectBuffer(ByteBuffer buf) {
        if (buf == null || !buf.isDirect()) {
            return;
        }

        // we could use this type cast and call functions without reflection code,
        // but static import from sun.* package is risky for non-SUN virtual machine.
        //try { ((sun.nio.ch.DirectBuffer)cb).cleaner().clean(); } catch (Exception ex) { }
        try {
            Method cleaner = buf.getClass().getMethod("cleaner");
            cleaner.setAccessible(true);
            Method clean = Class.forName("sun.misc.Cleaner").getMethod("clean");
            clean.setAccessible(true);
            clean.invoke(cleaner.invoke(buf));
            buf = null;
        } catch (Exception ex) {
            System.gc();
        }
    }

    private static boolean bufferedSearch(final long textlen, final FileChannel text,
            final byte firstByte, final int patLen,
            final byte[] pat, final int patlenm1) throws IOException {

        boolean foundPat = false;
        ByteBuffer textBuf = ByteBuffer.allocate((int) textlen);
        text.read(textBuf);
        textBuf.position(0);

        while (textBuf.hasRemaining() && !foundPat) {
            try {
                //skip loop - search for first byte
                while (textBuf.get() != firstByte) {
                }

                //match - fwd linear scan
                for (int j = 1; j < patLen; j++) {
                    if (textBuf.get() != pat[j]) {
                        break;
                    } else if (j == patlenm1) {
                        foundPat = true;
                        break;
                    }
                }
            } catch (BufferUnderflowException e) {
                break;
            }
        }
        return foundPat;
    }

    /**
     * Search the files in the root path for the String specified using a file
     * map stream. Uses a fwd linear scan and searches char by char. Algorithms
     * like 'boyer moore' don't seem to work well on a file map stream.
     *
     * @param rootPath The file to search
     * @param pattern The text to search for in the file
     * @param charset The character set to use for reading the file content
     * @return True if the pattern is found in the file content
     */
    @Override
    public List<Path> findPattern(final String rootPath, final String pattern,
            final Charset charset) {

        return getFiles(rootPath, true)
                .parallel()
                .filter(path -> searchFile(path, pattern, charset))
                .collect(Collectors.toList());

    }

    /**
     * Will search for files matching the given glob pattern.
     *
     * @param rootPath The root path within which to search
     * @param glob A GLOB pattern used to match files
     * @return A list of matching files
     */
    @Override
    public List<Path> findFile(final String rootPath, final String glob) {
        FileFinder fs = new FileFinder();
        try {
            return fs.findFile(Paths.get(rootPath), glob);
        } catch (IOException ioe) {
            recordErr("Error while searching for file", ioe);
            return new ArrayList<>();
        }
    }

    private static List<Match> findIndexedPattern(final String rootPath,
            final String pattern, final Paths indexFiles) {
        //TODO: implement this
        //Extract and read chunks of the compressed file and
        //search for the relevant trigram sequences
        //using these trigrams, build a new partial in-memory posting list
        //use this posting list to get the search results
        //This might benefit from parralel streaming
        return null;
    }

}

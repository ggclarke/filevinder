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
package org.filevinder.common;

import org.filevinder.interfaces.SysProps;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import static java.lang.System.err;
import static java.lang.System.out;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.filevinder.types.Chunk;
import static org.filevinder.core.ErrorHandler.recordErr;
import org.filevinder.core.FileIdMap;

/**
 * A miscellaneous set of utility functions used by this module.
 *
 * @author Gregory Clarke
 */
public final class Utils {

    /**
     * The newline character as a string.
     */
    public static final String NL = "\n";

    /**
     * The carriage return character as a string.
     */
    public static final String CR = "\r";

    /**
     * The system line separator.
     */
    public static final String LN = System.getProperty("line.seperator");

    /**
     * The UTF8 charset.
     */
    public static final Charset UTF8 = Charset.forName("UTF-8");

    /**
     * The bytes in a Kilo Byte.
     */
    private static final int KB = 1024;

    /**
     * Utility class not to be instantiated.
     */
    private Utils() {
    }

    /**
     * Splits a string into an array of non-distinct, sequential list of 3
     * character long trigrams.
     *
     * @param str the string to be parsed
     * @return the array of trigrams
     */
    public static String[] trigrams(final String str) {

        if (str == null || str.length() == 0) {
            return new String[0];
        }

        String s = str.replace(NL, "").replace("\r", "");

        int len = s.length();
        final int tokensize = 3;
        List<String> tokens = new ArrayList<>();
        int index = 0;

        while (index < s.length()) {
            String token = s.substring(index,
                    Math.min(index + tokensize, len));
            tokens.add(token);
            index += tokensize;
        }

        int last = tokens.size() - 1;
        String lastToken = tokens.get(last);
        if (lastToken.length() == 1) {
            tokens.set(last, lastToken + "  ");
        } else if (lastToken.length() == 2) {
            tokens.set(last, lastToken + " ");
        }

        return tokens.toArray(new String[tokens.size()]);
    }

    /**
     * Compresses a byte array using the JDK's ZLIB compression library.
     *
     * @param data The data to be compressed
     * @param compression The compression level to use
     * @return The compressed data
     */
    public static byte[] compress(final byte[] data, final int compression) {

        final int readBufSize = 8192;

        try (ByteArrayOutputStream outputStream
                = new ByteArrayOutputStream(data.length);) {
            int count;
            Deflater deflater = new Deflater();
            deflater.setLevel(compression);
            deflater.setInput(data);
            deflater.finish();

            byte[] buffer = new byte[readBufSize];
            while (!deflater.finished()) {
                // returns the generated code... index
                count = deflater.deflate(buffer);
                outputStream.write(buffer, 0, count);
            }

            byte[] output = outputStream.toByteArray();
            deflater.end();
            out.println("Original: " + data.length / KB + " Kb");
            out.println("Compressed: " + output.length / KB + " Kb");
            return output;

        } catch (IOException ioe) {
            err.println("Could not compress index data");
            ioe.printStackTrace(err);
            return new byte[0];
        }
    }

    /**
     * Decompresses data that was compressed with the JDK's ZLIB compression
     * library.
     *
     * @param data The data to be decompressed
     * @return The decompressed data
     * @throws IOException on error
     * @throws DataFormatException on error
     */
    public static byte[] decompress(final byte[] data)
            throws IOException, DataFormatException {

        final int readBufSize = 8192;

        try (ByteArrayOutputStream outputStream
                = new ByteArrayOutputStream(data.length);) {

            int count;
            Inflater inflater = new Inflater();
            inflater.setInput(data);
            byte[] buffer = new byte[readBufSize];
            while (!inflater.finished()) {
                count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
            byte[] output = outputStream.toByteArray();
            inflater.end();
            out.println("Original: " + data.length / KB + " Kb");
            out.println("Decompressed: " + output.length / KB + " Kb");
            return output;

        } catch (IOException ioe) {
            err.println("Could not compress index data");
            ioe.printStackTrace(err);
            return new byte[0];
        }
    }

    /**
     * Checks if the supplied data is consistent with an ASCII block, using a
     * few heuristics.
     *
     * @param aBuf The data to inspect
     * @return whether given data resembles ASCII
     */
    public static boolean isAscii(final byte[] aBuf) {
        return isValidAscii(aBuf, aBuf.length);
    }

    /**
     * Checks if the supplied data is consistent with an ASCII block, using a
     * few heuristics.
     *
     * @param buf The data to inspect
     * @param aLen The number of bytes to use in the assessment
     * @return whether the data chunk is consistent with ASCII data
     */
    public static boolean isValidAscii(final byte[] buf, final int aLen) {

        final int hex128 = 0x0080;

        for (int i = 0; i < aLen; i++) {
            //Check if the byte is less than 128 (0X0080)
            if ((hex128 & buf[i]) != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the supplied data is consistent with a block of data encoded in
     * a specific character set using a few heuristics.
     *
     * @param input The data to inspect
     * @param charset The character set to use in the assessment
     * @return whether the provided data encoding matches supplied character set
     */
    public static boolean isValidEncoding(final byte[] input,
            final Charset charset) {
        CharsetDecoder cs = charset.newDecoder();
        try {
            cs.decode(ByteBuffer.wrap(input));
            return true;
        } catch (CharacterCodingException e) {
            return false;
        }
    }

    /**
     * Checks whether a newline character exists in the given data.
     *
     * @param buf The data to inspect
     * @param charset The character set to parse the data with
     * @return whether an newline character was found
     */
    public static boolean containsNl(final byte[] buf, final Charset charset) {
        return indexOf(buf, NL.getBytes(charset)) != -1;
    }

    /**
     * Search the data byte array for the first occurrence of the byte array
     * pattern using the Knuth-Morris-Pratt Pattern Matching Algorithm.
     *
     * @param data the array to be searched
     * @param pattern the pattern to search for
     * @return the index of pattern in data
     */
    public static int indexOf(final byte[] data, final byte[] pattern) {
        int[] failure = computeFailure(pattern);

        int j = 0;
        for (int i = 0; i < data.length; i++) {
            while (j > 0 && pattern[j] != data[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == data[i]) {
                j++;
            }
            if (j == pattern.length) {
                return i - pattern.length + 1;
            }
        }
        return -1;
    }

    /**
     * Computes the failure function using a boot-strapping process, where the
     * pattern is matched against itself.
     *
     * @param pattern The data to evaluate
     * @return the failure array
     */
    private static int[] computeFailure(final byte[] pattern) {
        int[] failure = new int[pattern.length];

        int j = 0;
        for (int i = 1; i < pattern.length; i++) {
            while (j > 0 && pattern[j] != pattern[i]) {
                j = failure[j - 1];
            }

            if (pattern[j] == pattern[i]) {
                j++;
            }
            failure[i] = j;
        }

        return failure;

    }

    /**
     * Will return a list of regular files in the given directory.
     *
     * @param path The folder to inspect
     * @param recursive Include sub-folders recursively
     * @return A set of files that need to be processed
     */
    public static Stream<Path> getFiles(final String path, final boolean recursive) {
        try {
            String indexFile = System.getenv(SysProps.FV_INDEX_FILE_EXT.toString());

            Stream<Path> files = null;
            if (!recursive) {
                files = Files.list(Paths.get(path))
                        .filter(Files::isRegularFile)
                        .filter(f -> !f.endsWith(indexFile));
            } else {
                files = Files.walk(Paths.get(path))
                        .filter(Files::isRegularFile)
                        .filter(f -> !f.endsWith(indexFile));
            }

            return files.parallel();

        } catch (IOException ioe) {
            recordErr("Error retrieving files", ioe);
            return Stream.empty();
        }
    }

    /**
     * This method will use a sample of the file's content to try and determine
     * if it's a valid text file. It'll start by checking the file content
     * corresponds with the specified character encoding and then check if the
     * file contains line breaks. TODO: Invalid file if it has a very large
     * number of distinct trigrams
     *
     * @param path The location of the file
     * @param charset The expected text encoding of he file
     * @param maxSampleSize The max number of bytes to check for the validations
     * @return Whether it is a valid text file
     */
    public static boolean validateFile(final Path path, final Charset charset, final int maxSampleSize) {

        try (FileChannel text = FileChannel.open(path, StandardOpenOption.READ)) {
            long textLen = text.size();
            int sampleSize = textLen < maxSampleSize ? (int) textLen : maxSampleSize;
            ByteBuffer bytes = ByteBuffer.allocate(sampleSize);
            text.read(bytes);
            byte[] ba = bytes.array();

            if (!Utils.isValidEncoding(ba, charset)) {
                return false;
            }

            if (!Utils.containsNl(ba, charset)) {
                return false;
            }

            return true;

        } catch (IOException ioe) {
            recordErr("Could not validate file", ioe);
            return false;
        }
    }

    /**
     * This method will use a sample of the file's content to try and determine
     * if it's a valid text file. It'll start by checking the file content
     * corresponds with the specified character encoding and then check if the
     * file contains line breaks. TODO: Invalid file if it has a very large
     * number of distinct trigrams
     *
     * @param path The location of the file
     * @param charset The expected text encoding of he file
     * @return Whether it is a valid text file
     */
    public static boolean validateFile(final Path path, final Charset charset) {
        final int maxSampleSize = 1000;
        return validateFile(path, charset, maxSampleSize);
    }

    /**
     * Return the content of the specified file as a Chunk object, this method
     * will read into memory and and return the full content of the file.
     *
     * @param path The file to read.
     * @param charset The character set of the file's content
     * @return A representation of the file contents
     */
    public static Chunk readEntireFile(final Path path, final Charset charset) {
        if (!validateFile(path, charset)) {
            return null;
        }

        try {
            byte[] encoded = Files.readAllBytes(path);
            String text = new String(encoded, charset);
            return new Chunk(text, 0, FileIdMap.getInstance().getFileId(path));

        } catch (IOException ioe) {
            recordErr("Error splitting file", ioe);
            return null;
        }
    }

}

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import java.nio.charset.Charset;
import static org.filevinder.common.Utils.NL;

/**
 * A binary search implementation to be run on large, sorted files.
 *
 * @author Gregory Clarke
 */
public abstract class BinarySearch {

    private static int middle(final int start, final int end,
            final MappedByteBuffer fileBuffer, final Charset charset)
            throws IOException, NoMatchFound {

        final int nlLen = NL.getBytes(charset).length;
        final byte[] nlArr = new byte[nlLen];
        final byte nlByte = NL.getBytes(charset)[0];
        int mid = start + (end - start) / 2;
        byte[] candidate = getBytes(mid--, nlArr, fileBuffer);

        //Search backwards from the mid spot in the file for the closest
        //newline character, use it as the mid point
        while (mid > 0 && candidate[0] != nlByte) {
            mid--;
            candidate = getBytes(mid, nlArr, fileBuffer);
        }

        if (mid != 0) {
            mid += nlLen;
        }

        //This signifies either the end of the file
        //search forward from here for the start of the last line
        if (start == mid && start != 0) {
            candidate = getBytes(++mid, nlArr, fileBuffer);
            while (mid < fileBuffer.limit() && candidate[0] != nlByte) {
                mid++;
                candidate = getBytes(mid, nlArr, fileBuffer);
            }
            mid += nlLen;
        }

        //Signifies that the search is at an end, with no match found
        if (mid == end) {
            throw new NoMatchFound();
        }

        return mid;
    }

    private static byte[] getBytes(final long pos, final byte[] bArr,
            final MappedByteBuffer fileBuffer) {

        for (int i = 0; i < bArr.length; i++) {
            try {
                bArr[i] = fileBuffer.get((int) pos + i);
            } catch (IndexOutOfBoundsException iobe) {
                break;
            }
        }
        return bArr;
    }

    private static String getString(final int pos, final byte[] bArr,
            final MappedByteBuffer fileBuffer, final Charset charset) {

        for (int i = 0; i < bArr.length; i++) {
            try {
                bArr[i] = fileBuffer.get(pos + i);
            } catch (IndexOutOfBoundsException iobe) {
                break;
            }
        }
        return new String(bArr, charset);
    }

    private static String getLine(final int pos, final MappedByteBuffer fileBuffer,
            final Charset charset) {

        final int nlLen = NL.getBytes(charset).length;
        final byte[] nlArr = new byte[nlLen];
        final byte nlByte = NL.getBytes(charset)[0];
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        for (int i = 0;; i++) {
            try {
                nlArr[0] = fileBuffer.get(pos + i);

                if (nlArr[0] == nlByte) {
                    break;
                }

                bOut.write(nlArr[0]);

            } catch (IndexOutOfBoundsException iobe) {
                break;
            }
        }

        return new String(bOut.toByteArray(), charset);
    }

    private static int findPos(final String pat, final Charset charset,
            final MappedByteBuffer fileBuffer)
            throws IOException, FileTooBigException {

        int start = 0, mid = -1, comparison;
        int end = fileBuffer.limit();
        byte[] patArr = pat.getBytes(charset);
        String txt;

        do {
            try {
                mid = middle(start, end, fileBuffer, charset);
            } catch (NoMatchFound nfe) {
                break;
            }

            txt = getString(mid, new byte[patArr.length], fileBuffer, charset);
//            out.println(start + "-" + mid + "-" + end + "-->" + txt);
            comparison = pat.compareTo(txt);

            if (comparison == 0) {
                return mid;
            } else if (comparison > 0) {
                start = mid;
            } else {
                end = mid;
            }
        } while (end > start);

        return -1;
    }

    public static String findTrigram(final String pat, final Charset charset, final File file)
            throws IOException, FileTooBigException {

        try (FileChannel fileChannel = (new FileInputStream(file)).getChannel();) {

            MappedByteBuffer fileBuffer = toBuffer(fileChannel);

            int pos = findPos(pat, charset, fileBuffer);

            if (pos == -1) {
                return null;
            } else {
                return getString(pos, new byte[pat.length()], fileBuffer, charset);
            }
        } catch (IOException | FileTooBigException e) {
            throw e;
        }
    }

    public static String findLine(final String pat, final Charset charset, final File file)
            throws IOException, FileTooBigException {

        try (FileChannel fileChannel = (new FileInputStream(file)).getChannel();) {

            MappedByteBuffer fileBuffer = toBuffer(fileChannel);

            int pos = findPos(pat, charset, fileBuffer);

            if (pos == -1) {
                return null;
            } else {
                return getLine(pos, fileBuffer, charset);
            }
        } catch (IOException | FileTooBigException e) {
            throw e;
        }
    }

    private static MappedByteBuffer toBuffer(final FileChannel fileChannel)
            throws IOException, FileTooBigException {

        long size = fileChannel.size();
        if (size >= Integer.MAX_VALUE) {
            throw new FileTooBigException();
        }
        return fileChannel.map(READ_ONLY, 0, fileChannel.size());
    }

    /**
     * Locally used exception that is thrown when no match was found during the
     * search.
     */
    static class NoMatchFound extends Exception {

        NoMatchFound() {
            super();
        }
    }

}

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
package org.filevinder.experimental;

import static java.lang.System.out;

/**
 *
 * @author Gregory Clarke
 */
public abstract class BoyerMoore {

    /**
     * Returns the index within this string of the first occurrence of the
     * specified substring. If it is not a substring, return -1.
     *
     * @param haystack The string to be scanned
     * @param needle The target string to search
     * @return The start index of the substring
     */
    public static int indexOf(final char[] haystack, final char[] needle) {
        if (needle.length == 0) {
            return 0;
        }
        int[] charTable = makeCharTable(needle);
        int[] offsetTable = makeOffsetTable(needle);
        for (int i = needle.length - 1, j; i < haystack.length;) {
            for (j = needle.length - 1; needle[j] == haystack[i]; --i, --j) {
                if (j == 0) {
                    return i;
                }
            }
            // i += needle.length - j; // For naive method
            i += Math.max(offsetTable[needle.length - 1 - j], charTable[haystack[i]]);
        }
        return -1;
    }

    /**
     * Makes the jump table based on the mismatched character information.
     */
    private static int[] makeCharTable(final char[] needle) {
        final int alphabetSize = Character.MAX_VALUE + 1; // 65536
        int[] table = new int[alphabetSize];
        for (int i = 0; i < table.length; ++i) {
            table[i] = needle.length;
        }
        for (int i = 0; i < needle.length - 1; ++i) {
            table[needle[i]] = needle.length - 1 - i;
        }
        return table;
    }

    /**
     * Makes the jump table based on the scan offset which mismatch occurs.
     */
    private static int[] makeOffsetTable(final char[] needle) {
        int[] table = new int[needle.length];
        int lastPrefixPosition = needle.length;
        for (int i = needle.length; i > 0; --i) {
            if (isPrefix(needle, i)) {
                lastPrefixPosition = i;
            }
            table[needle.length - i] = lastPrefixPosition - i + needle.length;
        }
        for (int i = 0; i < needle.length - 1; ++i) {
            int slen = suffixLength(needle, i);
            table[slen] = needle.length - 1 - i + slen;
        }
        return table;
    }

    /**
     * Is needle[p:end] a prefix of needle?
     */
    private static boolean isPrefix(final char[] needle, final int p) {
        for (int i = p, j = 0; i < needle.length; ++i, ++j) {
            if (needle[i] != needle[j]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the maximum length of the substring ends at p and is a suffix.
     */
    private static int suffixLength(final char[] needle, final int p) {
        int len = 0;
        for (int i = p, j = needle.length - 1;
                i >= 0 && needle[i] == needle[j]; --i, --j) {
            len += 1;
        }
        return len;
    }

    private static String makeStr() {
        StringBuffer sb = new StringBuffer();

        sb.append("start");

        for (int i = 0; i < 99999; i++) {
            sb.append("foomiddfoofoobarbarfoomiddfoofoobarbar");
        }

        sb.append("middle");

        for (int i = 0; i < 99999; i++) {
            sb.append("foomiddfoofoobarbarfoomiddfoofoobarbar");
        }

        sb.append("ending");

        return sb.toString();
    }

    /**
     * Run a few tests on this class.
     * @param args runtime arguments
     */
    public static void main(final String[] args) {

        char[] str = makeStr().toCharArray();
        char[] parStr = "start".toCharArray();
        char[] patMid = "middle".toCharArray();
        char[] patEnd = "ending".toCharArray();

        long beg = System.currentTimeMillis();

        int posStr = indexOf(str, parStr);
        int posMid = indexOf(str, patMid);
        int posEnd = indexOf(str, patEnd);

        long end = System.currentTimeMillis();

        out.println(posStr);
        out.println(posMid);
        out.println(posEnd);
        out.println("Elapsed Time (Boyer Moore): " + (end - beg) + " ms");

        //Try with Java string search
        String makeStr = makeStr();

        beg = System.currentTimeMillis();

//        pos_str = str_.indexOf("start");
//        pos_mid = str_.indexOf("middle");
//        pos_end = str_.indexOf("ending");
        end = System.currentTimeMillis();
        out.println("Elapsed Time (String.index): " + (end - beg) + " ms");

    }

}

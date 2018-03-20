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
package org.filevinder.interfaces;

import java.util.ArrayList;
import org.filevinder.types.Trigram;

/**
 * The core data structure used for indexing a set of files. It is structured as
 * a set of nested lists. This mimics the posting list file structure.
 *
 * @author Gregory Clarke
 */
public interface PostingList {

    /**
     * Calculates the size of the data structure by counting the number of
     * instances that a trigram is listed.
     *
     * @return the size
     */
    int size();

    /**
     * Return the index.
     * @return index
     */
    ArrayList<Trigram> getIndex();

    /**
     * This method must be synchronized in order to keep index state correct.
     * It will append the provided match information to the posting list
     * data structure.
     * @param trigram The trigram string
     * @param pos The position of the trigram
     * @param fidVal The file containing the trigram
     */
    void append(String trigram,
            int pos, int fidVal);

    /**
     * Will encode the entire posting list as a string that can be written to file.
     * The provided string is encoded according to the following format:
     *
     * [TRI](F)L(F)L{BR}
     * [TRI](F)L(F)L{BR}
     *
     * TRI - The trigram
     * F - The file ID, cross referenced with a file ID map file.
     * L - The file location. FIles larger than 'int MAX' bytes are not supported.
     * {BR} - The \n character is used as a line break.
     *
     * @return a delta encoded string of integers
     */
     String toEncodedString();

}

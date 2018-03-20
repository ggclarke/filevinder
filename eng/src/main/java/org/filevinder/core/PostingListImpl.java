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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.filevinder.interfaces.PostingList;
import org.filevinder.types.FileRef;
import org.filevinder.types.Trigram;

/**
 * The core data structure used for indexing a set of files. It is structured as
 * a set of nested lists. This mimics the posting list file structure.
 *
 * @author Gregory Clarke
 */
public class PostingListImpl implements Serializable, PostingList {

    private static final long serialVersionUID = 1L;
    private static final int FID_POS = 4;
    private static final String BR = "\n";
    private ArrayList<Trigram> index;

    /**
     * Construct an empty instance of a PostingList care should be taken to
     * ensure that the object does not grow too large.
     */
    public PostingListImpl() {
        this.index = new ArrayList<>();
    }

    /**
     * TODO: Create a FID mapping file.
     * Construct an instance of a PostingList from the provided string, care
     * should be taken to ensure that the object does not grow too large.
     * See PostingList.toEncodedString for implementation.
     * The provided string is expected to be encoded according to the following format:
     *
     * [TRI](F)L(F)L{BR}
     * [TRI](F)L(F)L{BR}
     *
     * TRI - The trigram
     * F - The file ID, cross referenced with a file ID map file.
     * L - The file location. FIles larger than 'int MAX' bytes are not supported.
     * {BR} - The \n character is used as a line break.
     *
     * @param encodedString A string representation of the posting list
     */
    public PostingListImpl(final String encodedString) {
        this.index = new ArrayList<>();
        String[] sa = encodedString.split(BR);
        int fidStart, fidEnd, fid;

        for (String line : sa) {
            fidStart = FID_POS;
            String trigStr = line.substring(1, fidStart);
            Trigram trigram = new Trigram(trigStr);
            fidStart = line.indexOf('(', fidStart);
            while (true) {
                fidEnd = line.indexOf(')', fidStart);
                fid = Integer.parseInt(line.substring(fidStart + 1, fidEnd));
                FileRef fRef = new FileRef(fid);

                fidStart = line.indexOf('(', fidStart + 1);

                //Last FID in current line
                if (fidStart < 0) {
                    fidStart = line.length();
                }

                String csv = line.substring(fidEnd + 1, fidStart);

                String[] csvArr = csv.split(",");
                for (int j = 0; j < csvArr.length; j++) {
                    fRef.getPositions().add(Integer.parseInt(csvArr[j]));
                }
                trigram.getFileRefs().add(fRef);

                if (fidStart == line.length()) {
                    break;
                }
            }
            index.add(trigram);
        }
    }

    private void sort() {

        //sort by trigram name
        index.sort((x, y) -> x.getName().compareTo(y.getName()));

        //sort by file id's
        index.stream().map((t) -> {
            t.getFileRefs().sort((x, y) -> x.getFileId().compareTo(y.getFileId()));
            return t;
        }).forEachOrdered((t) -> {
            //sort by posn's
            t.getFileRefs().forEach((fRef) -> {
                fRef.getPositions().sort((x, y) -> x.compareTo(y));
            });
        });
    }

    /**
     * Calculates the size of the data structure by counting the number of
     * instances that a trigram is listed.
     *
     * @return the size
     */
    @Override
    public final int size() {
        int cnt = 0;
        for (Trigram trig : index) {
            for (FileRef fRef : trig.getFileRefs()) {
                cnt += fRef.getPositions().size();
            }
        }
        return cnt;
    }

    @Override
    public final ArrayList<Trigram> getIndex() {
        return index;
    }

    //TODO: Once the in memory index grows too large, serialize it and
    //create a second in memory object
    /**
     * This method must be synchronized in order to keep index state correct.
     * It will append the provided match information to the posting list
     * data structure.
     * @param trigram The trigram string
     * @param pos The position of the trigram
     * @param fidVal The file containing the trigram
     */
    @Override
    public final synchronized void append(final String trigram,
            final int pos, final int fidVal) {

        //Add a new trigram if needed
        Trigram trig = new Trigram(trigram);
        if (!index.contains(trig)) {
            index.add(trig);
        } else {
            trig = index.get(index.indexOf(trig));
        }

        //Add a new fileRef if needed
        FileRef fid = new FileRef(fidVal);
        if (!trig.getFileRefs().contains(fid)) {
            trig.getFileRefs().add(fid);
        } else {
            fid = trig.getFileRefs().get(trig.getFileRefs().indexOf(fid));
        }

        //Add the position ref
        fid.getPositions().add(pos);
    }

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
    @Override
    public final synchronized String toEncodedString() {

        sort();

        StringBuilder sb = new StringBuilder();
        for (Trigram trig : index) {
            sb.append(BR + "[" + trig.getName() + "]");
            for (FileRef fRef : trig.getFileRefs()) {
                sb.append("(" + fRef.getFileId() + ")" + asDeltaEncodedCsv(fRef.getPositions()));
            }
        }
        return sb.toString().trim();
    }

    private String asDeltaEncodedCsv(final List<Integer> ints) {

        //TODO: apply delta encoding
        StringBuilder sb = new StringBuilder();
        boolean firstIter = true;
        for (Integer i : ints) {
            if (firstIter) {
                sb.append(i);
                firstIter = false;
            } else {
                sb.append("," + i);
            }
        }
        return sb.toString();
    }
}

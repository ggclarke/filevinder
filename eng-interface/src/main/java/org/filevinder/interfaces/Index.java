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

import java.nio.file.Path;
import org.filevinder.types.Chunk;

/**
 * The interface that defines utility methods and member method signatures for
 * dealing with an Index.
 * @author Gregory Clarke
 */
public interface Index {

    /**
     * Returns the in memory data structure backing the Index.
     * @return The postingList data structure
     */
    PostingList getPostingList();

    /**
     * Will append the textual data in Chunk to the index data structure.
     * @param chunk Textual data extracted from a file
     */
    void mergeIndex(Chunk chunk);

    /**
     * Will reassign and hence create an orphan reference to the index data structure.
     */
    void purgeIndexFromMem();

    /**
     * Marshall the content of the index file to memory using the index data structure.
     * @param indexFile The file to read
     * @param encoding The file's text encoding
     */
    void memoizeIndexFile(Path indexFile, String encoding);

    /**
     * Write the content of the index data structure to file.
     * @param indexFile The file to write to
     * @param encoding The file's text encoding
     * @return true if operation completed successfully
     */
    boolean writePlainIndex(Path indexFile, String encoding);

    /**
     * WIll write the posting list to file using compression.
     * @param indexFile The file to write the posting list to
     * @param encoding The file's text encoding
     * @return true if operation completed  successfully
     */
    boolean writeCompressedIndex(Path indexFile, String encoding);

}

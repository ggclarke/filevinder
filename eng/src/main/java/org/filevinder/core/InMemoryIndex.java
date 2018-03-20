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

import org.filevinder.types.Chunk;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import static java.lang.System.err;
import java.nio.charset.Charset;
import java.nio.file.Files;
import static java.nio.file.Files.readAllBytes;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import org.filevinder.common.Utils;
import org.filevinder.interfaces.Index;
import org.filevinder.interfaces.PostingList;

/**
 * An class that represents an in-memory copy of an index file.
 * @author Gregory Clarke
 */

public final class InMemoryIndex implements Index {

    private PostingListImpl postingList;

    /**
     * Constructs a new index data structure.
     */
    public InMemoryIndex() {
        postingList = new PostingListImpl();
    }

    /**
     * Constructs an instance with the provided data structure.
     * @param postingListVar The index data structure
     */
    public InMemoryIndex(final PostingListImpl postingListVar) {
        postingList = postingListVar;
    }

    @Override
    public PostingList getPostingList() {
        return (PostingList) postingList;
    }

    /* TODO: currently only supports a Chunk containing a full file, refactoring
     * required to support breaking up very large files
     */
    @Override
    public void mergeIndex(final Chunk chunk) {
        String[] trigrams = Utils.trigrams(chunk.getText());
        for (int i = 0; i < trigrams.length; i++) {
            postingList.append(trigrams[i], i, chunk.getFileId());
        }
    }

    @Override
    public void memoizeIndexFile(final Path indexFile, final String encoding) {
        try {
            byte[] ba = readAllBytes(indexFile);
            ba = Utils.decompress(ba);
            postingList = new PostingListImpl(new String(ba, Charset.forName(encoding)));

        } catch (IOException | DataFormatException e) {
            err.println("Could not de-serialize index");
            e.printStackTrace(err);
        }
    }

    @Override
    public synchronized boolean writePlainIndex(final Path indexFile, final String encoding) {

        try (PrintWriter writer = new PrintWriter(indexFile.toFile(), encoding)) {
            writer.print(postingList.toEncodedString());
            return true;

        } catch (IOException e) {
            err.println("Could not write index to file");
            e.printStackTrace(err);
            return false;
        }
    }

    @Override
    public boolean writeCompressedIndex(final Path indexFile, final String encoding) {
        try {
            byte[] ba = Utils.compress(postingList.toEncodedString().
                    getBytes(Charset.forName(encoding)),
                    Deflater.DEFAULT_COMPRESSION);
            Files.write(indexFile, ba);
            appendRegister(indexFile, encoding);
            return true;

        } catch (IOException | ClassNotFoundException e) {
            err.println("Could not write index to file");
            e.printStackTrace(err);
            return false;
        }
    }

    @Override
    public void purgeIndexFromMem() {
        postingList = new PostingListImpl();
    }

    //adds a postingist to the index register
    private void appendRegister(final Path indexFile, final String encoding)
            throws IOException, ClassNotFoundException {

        //TODO: write test for this
        final String indexRegFile = File.separator + "index.regf";
        ArrayList<String> indexReg = new ArrayList<>();

        String lineEntry = indexFile.toFile().getAbsolutePath();
        File regFile = new File(indexFile.toFile().getParent() + indexRegFile);

        //read the existing register
        if (regFile.exists()) {
            try (FileInputStream fin = new FileInputStream(regFile);
                    ObjectInputStream in = new ObjectInputStream(fin);) {

                indexReg = (ArrayList<String>) in.readObject();

            } catch (IOException | ClassNotFoundException e) {
                throw e;
            }
        }

        //append to the register if it's a new entry
        if (indexReg.isEmpty() || !indexReg.contains(lineEntry)) {

            indexReg.add(lineEntry);

            try (FileOutputStream fout = new FileOutputStream(regFile);
                    ObjectOutputStream out = new ObjectOutputStream(fout);) {

                out.writeObject(indexReg);
                out.flush();

            } catch (IOException ioe) {
                throw ioe;
            }
        }
    }
}

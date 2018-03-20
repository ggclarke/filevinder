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
package org.filevinder.types;


/**
 * A Chunk represents a portion of text in a file at a specified location.
 *
 * @author Gregory Clarke
 */
public final class Chunk {

    private final String text;
    private final int location;
    private final int fileId;

    /**
     * Constructs a Chunk object for a given file and piece of it's content.
     *
     * @param textVal The text extracted from the file
     * @param locationVal The location of the starting position of the text in the file
     * @param fileIdVal The ID of the file.
     */
    public Chunk(final String textVal, final int locationVal, final int fileIdVal) {
        text = textVal;
        location = locationVal;
        fileId = fileIdVal;
    }

    public String getText() {
        return text;
    }

    public int getLocation() {
        return location;
    }

    public int getFileId() {
        return fileId;
    }

    @Override
    public String toString() {
        return String.format("%d: %s", location, text);
    }
}

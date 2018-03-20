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

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

/**
 *
 * @author Gregory Clarke
 */
public interface Search {

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
    boolean searchFile(Path filePath, String pattern,
            Charset charset);

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
    List<Path> findPattern(String rootPath, String pattern,
            Charset charset);

    /**
     * Will search for files matching the given glob pattern.
     *
     * @param rootPath The root path within which to search
     * @param glob A GLOB pattern used to match files
     * @return A list of matching files
     */
    List<Path> findFile(String rootPath, String glob);

}

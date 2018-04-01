/*
 * Copyright (C) 2018
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
package org.filevinder.ui.presentation;

import java.util.SortedSet;

/**
 *
 * @author Gregory Clarke
 */
public interface FilesUtil {

    /**
     * Will search in the supplied path for other folders and return a list of
     * them.
     *
     * @param pathContext The path context
     * @return a list containing folders in the specified context
     */
    SortedSet<String> foldersInCurrentPath(final String pathContext);

    /**
     * Will complete a partial path.
     *
     * @param path path string
     * @param result the completed path
     * @return the path
     */
    String autoCompletePath(final String path, final String result);

    /**
     * Works out the partial folder name entered by the user.
     *
     * @param path The full path String
     * @return The partial folder name
     */
    String partialFolderName(final String path);

}

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
package org.filevinder.ui.fs;

import java.io.File;
import static java.lang.System.out;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.commons.io.FilenameUtils;
import static org.apache.commons.io.FilenameUtils.getFullPathNoEndSeparator;
import static org.apache.commons.io.FilenameUtils.getPathNoEndSeparator;
import static org.apache.commons.io.FilenameUtils.getPrefix;
import org.filevinder.ui.presentation.FilesUtil;

/**
 *
 * @author Gregory Clarke
 */
public final class FilesUtilImpl implements FilesUtil {

    /**
     * Will search in the supplied path for other folders and return a list of
     * them.
     *
     * @param pathContext The path context
     * @return a list containing folders in the specified context
     */
    @Override
    public SortedSet<String> foldersInCurrentPath(final String pathContext) {

        SortedSet<String> folders = new TreeSet<>();
        if (pathContext == null) {
            return folders;
        }

        File file;
        if (getPathNoEndSeparator(pathContext).length() == 0) { //check if root path
            file = new File("/");
        } else {
            file = new File(getFullPathNoEndSeparator(pathContext));
        }

        if (file.exists()) {
            File[] directories = file.listFiles(File::isDirectory);
            for (File f : directories) {
                folders.add(f.getName());
            }
        }

        return folders;
    }

    /**
     * Works out the partial folder name entered by the user.
     *
     * @param path The full path String
     * @return The partial folder name
     */
    @Override
    public String partialFolderName(final String path) {
        String str = getFullPathNoEndSeparator(path);
        str = path.replace(str, "");
        String prefix = getPrefix(str);
        return str.replace(prefix, "");
    }

    /**
     * Will complete a partial path.
     *
     * @param path path string
     * @param result the completed path
     * @return the path
     */
    @Override
    public String autoCompletePath(final String path, final String result) {
        String autoCompleteVal = FilenameUtils.getFullPathNoEndSeparator(path);
        autoCompleteVal = autoCompleteVal + File.separator + result;
        out.println("Auto complete path: " + autoCompleteVal);
        return autoCompleteVal;
    }

}

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
package org.filevinder.ui.utils;

import java.io.File;
import java.io.IOException;
import static java.lang.System.out;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.commons.io.FilenameUtils;
import static org.apache.commons.io.FilenameUtils.getFullPathNoEndSeparator;
import static org.apache.commons.io.FilenameUtils.getPathNoEndSeparator;
import static org.apache.commons.io.FilenameUtils.getPrefix;
import static org.filevinder.ui.Constants.DATE_FORMAT;

/**
 *
 * @author Gregory Clarke
 */
public final class FSUtils {

    private FSUtils() {
    }

    /**
     * Will search in the supplied path for other folders and return a list of
     * them.
     *
     * @param pathContext The path context
     * @return a list containing folders in the specified context
     */
    public static SortedSet<String> foldersInCurrentPath(final String pathContext) {

        SortedSet<String> folders = new TreeSet<String>();
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
    public static String partialFolderName(final String path) {
        String str = getFullPathNoEndSeparator(path);
        str = path.replace(str, "");
        String prefix = getPrefix(str);
        return str.replace(prefix, "");
    }

    /**
     * Will complete a partial path.
     * @param path path string
     * @param result the completed path
     * @return the path
     */
    public static String autoCompletePath(final String path, final String result) {
        String autoCompleteVal = FilenameUtils.getFullPathNoEndSeparator(path);
        autoCompleteVal = autoCompleteVal + File.separator + result;
        out.println("Auto complete path: " + autoCompleteVal);
        return autoCompleteVal;
    }

    /**
     * Identifies the file type.
     *
     * @param file The file for which the type is desired.
     * @return String representing identified type of file with provided name.
     */
    public static String getFileType(final File file) {
        String fileType = "Unknown";
        try {
            fileType = Files.probeContentType(file.toPath());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return fileType;
    }

    /**
     * Returns the creation time if available.
     *
     * @param path the path
     * @return the creation time
     */
    public static String getCreationTime(final Path path) {
        String creationTime = "";
        try {
            long ms = ((BasicFileAttributes) Files.readAttributes(path, BasicFileAttributes.class)).
                    creationTime().toMillis();
            creationTime = (new SimpleDateFormat(DATE_FORMAT)).format(new Date(ms));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return creationTime;
    }

    /**
     * Returns the last accessed time if available.
     *
     * @param path the path
     * @return the access time
     */
    public static String getAccessedTime(final Path path) {
        String accessedTime = "";
        try {
            long ms = ((BasicFileAttributes) Files.readAttributes(path, BasicFileAttributes.class)).
                    lastAccessTime().toMillis();
            accessedTime = (new SimpleDateFormat(DATE_FORMAT)).format(new Date(ms));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return accessedTime;
    }

    /**
     * Returns the permissions if available.
     *
     * @param path the path
     * @return the permissions
     */
    public static String getPermissions(final Path path) {
        StringBuffer permissions = new StringBuffer("");
        File f = path.toFile();
        permissions.append(f.canRead() ? "r" : "-");
        permissions.append(f.canWrite() ? "w" : "-");
        permissions.append(f.canExecute() ? "x" : "-");
        return permissions.toString();
    }

}

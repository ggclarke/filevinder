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
package org.filevinder.ui.presentation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author Gregory Clarke
 */
final class JSUtils {

    private JSUtils() {
    }

    public static String jsClean(final String str) {

        if (str == null) {
            return str;
        }

        String clean = str.replace("'", "\\'");
        clean = clean.replace(System.getProperty("line.separator"), "\\n");
        clean = clean.replace("\n", "\\n");
        clean = clean.replace("\r", "\\n");
        return clean;
    }

    public static String loadFile(final String fp) {
        String fileContent = "";
        try {
            fileContent = new String(Files.readAllBytes(Paths.get(fp)));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return fileContent;
    }
}

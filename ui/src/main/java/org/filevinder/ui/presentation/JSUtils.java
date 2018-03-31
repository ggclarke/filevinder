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
import java.io.InputStream;

/**
 *
 * @author Gregory Clarke
 */
final class JSUtils {

    private JSUtils() {
    }

    public static String jsClean(final String str) {
        String clean = str.replace("'", "\\'");
        clean = clean.replace(System.getProperty("line.separator"), "\\n");
        clean = clean.replace("\n", "\\n");
        clean = clean.replace("\r", "\\n");
        return clean;
    }

    public static String loadFile(Class clazz) {
        String str = "";
        try {
            InputStream in = clazz.getClassLoader()
                    .getResourceAsStream("sample/sample.java");
            java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
            str = s.hasNext() ? s.next() : "";
            in.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return str;
    }
}

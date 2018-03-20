/*
 * Copyright (C) 2018 Gregory Clarke
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

/**
 * A class to determine the current Operating System.
 * @author Gregory Clarke
 */
public final class OSChecker {

    private OSChecker() {
    }

    public static boolean isWindows() {
        return (os().indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (os().indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (os().indexOf("nix") >= 0 || os().indexOf("nux") >= 0 || os().indexOf("aix") > 0);
    }

    public static boolean isSolaris() {
        return (os().indexOf("sunos") >= 0);
    }

    private static String os() {
        return System.getProperty("os.name").toLowerCase();
    }

}

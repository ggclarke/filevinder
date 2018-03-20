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
package org.filevinder.ui;

import java.nio.charset.Charset;

/**
 * A set of constants used throughout the UI.
 *
 * @author Gregory Clarke
 */
public final class Constants {

    private Constants() {
    }

    //TODO: get this from an env var
    /**
     * The charset to be used for reading files.
     */
    public static final Charset CHARSET = Charset.defaultCharset();

    /**
     * UI Padding.
     */
    public static final int GAP_S = 10;

    /**
     * UI Padding.
     */
    public static final int GAP_XS = 5;

    /**
     * UI Padding.
     */
    public static final int PAD_M = 25;

    /**
     * Man app widow width.
     */
    public static final int WINDOW_WIDTH = 1000;

    /**
     * Main app window height.
     */
    public static final int WINDOW_HEIGHT = 600;

    /**
     * Main app window height.
     */
    public static final int TEXT_AREA_MIN_WIDTH = 300;

    /**
     * UI date format for displays.
     */
    public static final String DATE_FORMAT = "yyyy/mm/dd hh:mm:ss";

}

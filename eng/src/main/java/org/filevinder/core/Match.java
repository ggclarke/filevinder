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

/**
 * This class encapsulates a location in a file matching a search pattern.
 *
 * @author Gregory Clarke
 */
public final class Match {

    private String path;
    private long location;

    /**
     * Construct a new instance of the Match object.
     *
     * @param pathVar The path of the matching file
     * @param locationVar The location in the file
     */
    public Match(final String pathVar, final long locationVar) {
        path = pathVar;
        location = locationVar;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String pathVar) {
        path = pathVar;
    }

    public long getLocation() {
        return location;
    }

    public void setLocation(final long locationVar) {
        location = locationVar;
    }

}

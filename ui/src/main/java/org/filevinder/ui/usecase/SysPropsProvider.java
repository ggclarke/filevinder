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
package org.filevinder.ui.usecase;

/**
 * A wrapper around System for mocking purposes.
 *
 * @author Gregory Clarke
 */
public final class SysPropsProvider {

    /**
     * Returns the specified environment variable value.
     *
     * @param propertyName property name
     * @return String property
     */
    public String getSysProp(final String propertyName) {
        return System.getenv(propertyName);
    }
}

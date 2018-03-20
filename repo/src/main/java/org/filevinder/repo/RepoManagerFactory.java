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
package org.filevinder.repo;

/**
 *
 * @author Gregory Clarke
 */
public final class RepoManagerFactory {

    private RepoManagerFactory() {
    }

    //TODO: this class needs to be refined - can I make it pluggable w/ runtime instantiation?

    /**
     * Abstract factory for implementations of RepoManager.
     *
     * @param type the impl type
     * @return implementation object
     */
    public static RepoManager getInstance(final String type) {
        if (type.equals("git")) {
            return new GitRepoManager();
        } else {
            return null;
        }
    }
}

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
package org.filevinder.repo;

import java.net.URI;

/**
 *
 * @author Gregory Clarke
 */
public abstract class RepoManager {

    /**
     * Clones a source repository to disk.
     *
     * @param uri The URI of the remote repository
     * @param directory The local directory to clone to
     * @param branch The branch to clone
     * @param allBranches Whether all branches should be clones
     */
    abstract void clone(final String uri, final String directory, final String branch, final boolean allBranches);

    /**
     * Clones a source repository to disk.
     *
     * @param uri The URI of the remote repository
     * @param directory The local directory to clone to
     * @param branch The branch to clone
     */
    public final void clone(final String uri, final String directory, final String branch) {
        clone(uri, directory, branch, true);
    }

    /**
     * Clones a source repository to disk.
     *
     * @param uri The URI of the remote repository
     * @param directory The local directory to clone to
     * @param allBranches Whether all branches should be clones
     */
    public final void clone(final String uri, final String directory, final boolean allBranches) {
        clone(uri, directory, null, allBranches);
    }

    /**
     * Clones a source repository to disk.
     *
     * @param uri The URI of the remote repository
     * @param directory The local directory to clone to
     */
    public final void clone(final String uri, final String directory) {
        clone(uri, directory, null, true);
    }

}

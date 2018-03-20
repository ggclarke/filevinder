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

import java.io.File;
import static java.lang.System.err;
import static java.util.Collections.singleton;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.filevinder.repo.RepoManager;

/**
 *
 * @author Gregory Clarke
 */
public final class GitRepoManager extends RepoManager {

    protected GitRepoManager() {
    }

    @Override
    public void clone(final String uri, final String directory,
            final String branch, final boolean allBranches) {

        CloneCommand cmd = Git.cloneRepository();

        if (branch == null) {
            cmd = cmd.setURI(uri)
                    .setDirectory(new File(directory))
                    .setCloneAllBranches(allBranches);
        } else {
            cmd.setBranch("refs/heads/" + branch)
                    .setBranchesToClone(singleton("refs/heads/" + branch));
        }

        try {
            cmd.call();
        } catch (GitAPIException ge) {
            err.println("Could not clone GIT repo.");
            ge.printStackTrace();
        }
    }

}

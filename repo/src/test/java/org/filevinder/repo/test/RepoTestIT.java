/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.filevinder.repo.test;

import java.io.File;
import static java.lang.System.out;
import org.filevinder.repo.RepoManager;
import org.filevinder.repo.RepoManagerFactory;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Gregory Clarke
 */
public class RepoTestIT {

    @Ignore
    @Test
    public final void testDownloadingAGitRepo() {
        String tmp = System.getProperty("java.io.tmpdir") + "/repo";
        File f = new File(tmp);
        if (f.exists()) {
            out.printf("Deleting: %s%n", f.getAbsolutePath());
            deleteDir(f);
        }

        String repo = "https://github.com/github/gitignore.git";
        out.printf("Cloning: %s... %n... to: %s%n", repo, f.getAbsolutePath());
        RepoManager git = RepoManagerFactory.getInstance("git");
        git.clone(repo, f.getAbsolutePath());
        Assert.assertTrue(f.exists());
        Assert.assertTrue(f.listFiles().length > 10);

    }

    private void deleteDir(final File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }
}

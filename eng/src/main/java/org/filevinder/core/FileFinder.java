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

import java.io.IOException;
import static java.lang.System.err;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.CONTINUE;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a utility method 'FindFile' that will return files
 * matching the name criteria specified in GLOB format.
 *
 * @author Gregory Clarke
 */
public final class FileFinder {

    /**
     * Searches for files matching a GLOB pattern in subfolders.
     *
     * @param rootDir The root folder from which to search
     * @param glob The GLOB pattern identifying the files
     * @return A list of matching file paths
     * @throws IOException if FS error occurs
     */
    public List<Path> findFile(final Path rootDir, final String glob) throws IOException {
        Finder finder = new Finder(glob);
        Files.walkFileTree(rootDir, finder);
        return finder.matches;
    }

    /**
     * An implementation of the visitor from the visitor pattern.
     * A matcher is used to filter file names using the specified GLOB.
     */
    private static final class Finder extends SimpleFileVisitor<Path> {

        private List<Path> matches = new ArrayList<>();
        private final PathMatcher matcher;

        private Finder(final String pattern) {
            matcher = FileSystems.getDefault()
                    .getPathMatcher("glob:" + pattern);
        }

        public List<Path> getMatches() {
            return matches;
        }

        public void setMatches(final List<Path> matchesVar) {
            matches = matchesVar;
        }

        private void find(final Path file) {
            Path name = file.getFileName();
            if (name != null && matcher.matches(name)) {
                matches.add(file);
            }
        }

        // Invoke the pattern matching method on each file.
        @Override
        public FileVisitResult visitFile(final Path file,
                final BasicFileAttributes attrs) {
            find(file);
            return CONTINUE;
        }

        // Invoke the pattern matching method on each directory.
        @Override
        public FileVisitResult preVisitDirectory(final Path dir,
                final BasicFileAttributes attrs) {
            find(dir);
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(final Path file,
                final IOException exc) {
            err.println("Error occured in Finder");
            exc.printStackTrace(err);
            return CONTINUE;
        }
    }
}

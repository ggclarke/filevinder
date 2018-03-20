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

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A map of file ID's to file paths for use in he posting list index where
 * file ID's are stored alongside the trigram/ location data.
 * @author Gregory Clarke
 */
public final class FileIdMap {

    private static final FileIdMap SINGLETON = new FileIdMap();
    private final Map<Path, Integer> fileId;

    private FileIdMap() {
        fileId = new ConcurrentHashMap<>();
    }

    public static FileIdMap getInstance() {
        return SINGLETON;
    }

    /**
     * Returns the ID matching the specified file path.
     * Getting the ID is he more frequent operation, hence Path is used as the key.
     * @param path The path to the file
     * @return The Id matching the file
     */
    public Integer getFileId(final Path path) {
        fileId.putIfAbsent(path, fileId.size() + 1);
        return fileId.get(path);
    }

    /**
     * Returns the path to the file matching the provided file ID.
     * @param fid The file ID
     * @return The path mapped to the ID
     */
    public Path getPath(final Integer fid) {
        for (Map.Entry<Path, Integer> entry : fileId.entrySet()) {
            if (entry.getValue().equals(fid)) {
                return entry.getKey();
            }
        }
        return null;
    }
}

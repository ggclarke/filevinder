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
package org.filevinder.ui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A View Bean that represents the data in the search results table.
 *
 * @author Gregory Clarke
 */
public final class SearchResult {

    private final StringProperty name;
    private final StringProperty path;
    private final StringProperty size;
    private final StringProperty type;
    private final StringProperty modified;
    private final StringProperty created;
    private final StringProperty accessed;
    private final StringProperty permission;

    public SearchResult(final String nameVal, final String pathVal, final String sizeVal,
            final String typeVal, final String modifiedVal, final String createdVal,
            final String accessedVal, final String permissionVal) {
        this.name = new SimpleStringProperty(nameVal);
        this.path = new SimpleStringProperty(pathVal);
        this.size = new SimpleStringProperty(sizeVal);
        this.type = new SimpleStringProperty(typeVal);
        this.modified = new SimpleStringProperty(modifiedVal);
        this.created = new SimpleStringProperty(createdVal);
        this.accessed = new SimpleStringProperty(accessedVal);
        this.permission = new SimpleStringProperty(permissionVal);
    }

    /**
     * @return the name
     */
    public  String getName() {
        return name.get();
    }

    /**
     * @param nameVal the name to set
     */
    public void setName(final String nameVal) {
        name.set(nameVal);
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path.get();
    }

    /**
     * @param pathVal the path to set
     */
    public void setPath(final String pathVal) {
        path.set(pathVal);
    }

    /**
     * @return the size
     */
    public String getSize() {
        return size.get();
    }

    /**
     * @param sizeVal the size to set
     */
    public void setSize(final String sizeVal) {
        size.set(sizeVal);
    }

    /**
     * @return the type
     */
    public String getType() {
        return type.get();
    }

    /**
     * @param typeVal the type to set
     */
    public void setType(final String typeVal) {
        type.set(typeVal);
    }

    /**
     * @return the modified
     */
    public String getModified() {
        return modified.get();
    }

    /**
     * @param modifiedVal the modified to set
     */
    public void setModified(final String modifiedVal) {
        modified.set(modifiedVal);
    }

    /**
     * @return the created
     */
    public String getCreated() {
        return created.get();
    }

    /**
     * @param createdVal the created to set
     */
    public void setCreated(final String createdVal) {
        created.set(createdVal);
    }

    /**
     * @return the accessed
     */
    public String getAccessed() {
        return accessed.get();
    }

    /**
     * @param accessedVal the accessed to set
     */
    public void setAccessed(final String accessedVal) {
        accessed.set(accessedVal);
    }

    /**
     * @return the permission
     */
    public String getPermission() {
        return permission.get();
    }

    /**
     * @param permissionVal the permission to set
     */
    public void setPermission(final String permissionVal) {
        permission.set(permissionVal);
    }

}

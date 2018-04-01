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
package org.filevinder.ui.presentation;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A View Bean that represents the data in the search results table.
 *
 * @author Gregory Clarke
 */
public class SearchResultModel {

    private final StringProperty name;
    private final StringProperty path;
    private final StringProperty size;
    private final StringProperty type;
    private final StringProperty modified;
    private final StringProperty created;
    private final StringProperty accessed;
    private final StringProperty permission;

    SearchResultModel(final String nameVal, final String pathVal, final String sizeVal,
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
    public final String getName() {
        return name.get();
    }

    /**
     * @param nameVal the name to set
     */
    public final void setName(final String nameVal) {
        name.set(nameVal);
    }

    /**
     * @return the path
     */
    public final String getPath() {
        return path.get();
    }

    /**
     * @param pathVal the path to set
     */
    public final void setPath(final String pathVal) {
        path.set(pathVal);
    }

    /**
     * @return the size
     */
    public final String getSize() {
        return size.get();
    }

    /**
     * @param sizeVal the size to set
     */
    public final void setSize(final String sizeVal) {
        size.set(sizeVal);
    }

    /**
     * @return the type
     */
    public final String getType() {
        return type.get();
    }

    /**
     * @param typeVal the type to set
     */
    public final void setType(final String typeVal) {
        type.set(typeVal);
    }

    /**
     * @return the modified
     */
    public final String getModified() {
        return modified.get();
    }

    /**
     * @param modifiedVal the modified to set
     */
    public final void setModified(final String modifiedVal) {
        modified.set(modifiedVal);
    }

    /**
     * @return the created
     */
    public final String getCreated() {
        return created.get();
    }

    /**
     * @param createdVal the created to set
     */
    public final void setCreated(final String createdVal) {
        created.set(createdVal);
    }

    /**
     * @return the accessed
     */
    public final String getAccessed() {
        return accessed.get();
    }

    /**
     * @param accessedVal the accessed to set
     */
    public final void setAccessed(final String accessedVal) {
        accessed.set(accessedVal);
    }

    /**
     * @return the permission
     */
    public final String getPermission() {
        return permission.get();
    }

    /**
     * @param permissionVal the permission to set
     */
    public final void setPermission(final String permissionVal) {
        permission.set(permissionVal);
    }

}

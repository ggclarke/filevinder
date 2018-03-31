/*
 * Copyright (C) 2018 
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
package org.filevinder.ui;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Gregory Clarke
 */
public final class IOData {

    private static final HashMap<String, Object> MAP = new HashMap();

    /**
     * Creates a keyed value.
     *
     * { k: v, k: v }
     *
     * @param key key
     * @param value value
     */
    public void putProperty(final String key, final String value) {
        MAP.put(key, value);
    }

    /**
     * Returns a keyed value.
     *
     * { k: v, k: v }
     *
     * @param key key
     * @return property
     */
    public String getProperty(final String key) {
        Object o = MAP.get(key);
        if (o != null && o instanceof String) {
            return (String) o;
        } else {
            return null;
        }
    }

    /**
     * Creates a data structure that contains sets of keyed values.
     *
     * {
     * k : [k: v, k: v],
     * k : [k: v, k: v] }
     *
     * @param entityName setName
     * @param key key
     * @param value value
     */
    public void putEntityProperty(final String entityName, final String key,
            final String value) {
        MAP.putIfAbsent(entityName, new HashMap<String, String>());
        HashMap<String, String> kvList = (HashMap) MAP.get(entityName);
        kvList.put(key, value);
    }

    /**
     * Returns a Keyed property from a specified list of K-V pairs.
     *
     * {
     * k : [k: v, k: v],
     * k : [k: v, k: v] }
     *
     * @param entityName setName
     * @param key key
     * @return value
     */
    public String getEntityProperty(final String entityName, final String key) {
        HashMap<String, String> list = (HashMap) MAP.get(entityName);

        if (list == null || !(list instanceof HashMap)) {
            return null;
        }

        String value = list.get(key);

        if (value != null && value instanceof String) {
            return value;
        } else {
            return null;
        }
    }

    /**
     * Adds a row to a table-like data structure of K-V pairs.
     *
     * {
     * k : {
     *      [k:v, k:v, k:v], [k:v, k:v, k:v] },
     * k : {
     *      [k:v, k:v, k:v], [k:v, k:v, k:v] } }
     *
     * @param tableName the table name
     * @param row row
     */
    public void putRowInTable(final String tableName, final HashMap<String, String> row) {
        MAP.putIfAbsent(tableName, new ArrayList<HashMap<String, String>>());
        ArrayList<HashMap<String, String>> table = (ArrayList) MAP.get(tableName);
        table.add(row);
    }

    /**
     * Gets a table-like data structure containing K-V pairs.
     * {
     * k : {
     *      [k:v, k:v, k:v], [k:v, k:v, k:v] },
     * k : {
     *      [k:v, k:v, k:v], [k:v, k:v, k:v] } }
     *
     * @param tableName the name of the table
     * @return the table with all values
     */
    public ArrayList<HashMap<String, String>> getTable(final String tableName) {
        ArrayList<HashMap<String, String>> table = (ArrayList) MAP.get(tableName);

        if (table == null || !(table instanceof ArrayList)) {
            return null;
        } else {
            return table;
        }
    }
}

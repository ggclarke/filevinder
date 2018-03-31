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
package org.filevinder.ui.test;

import java.util.ArrayList;
import java.util.HashMap;
import org.filevinder.ui.IOData;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Gregory Clarke
 */
public final class IODataTest {

    @Test
    public void testPutAndGetProperty() {
        IOData data = new IOData();
        String val = "value";
        String key = "key";
        data.putProperty(key, val);

        String actual = data.getProperty(key);
        Assert.assertEquals(val, actual);
    }

    @Test
    public void testOverrideProperty() {
        IOData data = new IOData();
        String val = "value1";
        String key = "key";
        data.putProperty(key, val);

        val = "value2";
        data.putProperty(key, val);

        String actual = data.getProperty(key);
        Assert.assertEquals(val, actual);
    }

    @Test
    public void testNullProperty() {
        IOData data = new IOData();
        Assert.assertNull(data.getProperty("XXXXX"));
    }

    @Test
    public void testEntity() {
        IOData data = new IOData();
        String entity = "personA";

        String key1 = "name";
        String value1 = "greg";

        String key2 = "surname";
        String value2 = "clarke";

        String key3 = "age";
        String value3 = "36";

        data.putEntityProperty(entity, key1, value1);
        data.putEntityProperty(entity, key2, value2);
        data.putEntityProperty(entity, key3, value3);

        Assert.assertEquals(value1, data.getEntityProperty(entity, key1));
        Assert.assertEquals(value2, data.getEntityProperty(entity, key2));
        Assert.assertEquals(value3, data.getEntityProperty(entity, key3));
    }

    @Test
    public void testEntityOverride() {
        IOData data = new IOData();
        String entity = "personA";

        String key1 = "name";
        String value1 = "greg";

        String key2 = "surname";
        String value2 = "clarke";

        data.putEntityProperty(entity, key1, value1);
        data.putEntityProperty(entity, key1, value2);

        Assert.assertEquals(value2, data.getEntityProperty(entity, key1));
    }

    @Test
    public void testEntityNull() {
        IOData data = new IOData();
        String entity = "XXXXX";
        Assert.assertNull(data.getEntityProperty(entity, "XXXXX"));
    }

    @Test
    public void testEntityPropertyNull() {
        IOData data = new IOData();
        String entity = "personA";

        String key1 = "name";
        String value1 = "greg";

        data.putEntityProperty(entity, key1, value1);

        Assert.assertNull(data.getEntityProperty(entity, "XXXXX"));
    }

    @Test
    public void testInsertTableRow() {
        IOData data = new IOData();
        String tableName = "myTable";
        HashMap<String, String> row1 = new HashMap<>();
        row1.put("fld1", "row1-val1");
        row1.put("fld2", "row1-val2");
        row1.put("fld3", "row1-val3");
        data.putRowInTable(tableName, row1);

        HashMap<String, String> row2 = new HashMap<>();
        row2.put("fld1", "row2-val1");
        row2.put("fld2", "row2-val2");
        row2.put("fld3", "row2-val3");
        data.putRowInTable(tableName, row2);

        ArrayList<HashMap<String, String>> table = data.getTable(tableName);

        for (HashMap<String, String> row : table) {
            Assert.assertNull(row.get("XXXXX"));
        }
    }

    @Test
    public void testNullTableRow() {
        IOData data = new IOData();
        String tableName = "myTable";
        Assert.assertNull(data.getTable("XXXXX"));
    }
}

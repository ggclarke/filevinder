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
package org.filevinder.engine.test;

import java.nio.charset.Charset;
import javax.xml.bind.DatatypeConverter;
import org.filevinder.common.Utils;
import static org.filevinder.common.Utils.NL;
import static org.filevinder.common.Utils.UTF8;
import static org.filevinder.common.Utils.containsNl;
import static org.filevinder.common.Utils.indexOf;
import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.filevinder.common.Utils.isAscii;
import static org.filevinder.common.Utils.isValidEncoding;

/**
 *
 * @author Gregory Clarke
 */
public final class UtilsTest {

    private final Charset defaultCharset = Charset.defaultCharset();

    @Test
    public void testIsAscii() {
        byte[] ba = "abcdefghijkLMNOPQRSTUVWXYZ123".getBytes(UTF8);
        assertTrue(isAscii(ba));
    }

    @Test
    public void testNegIsAscii() {
        byte[] ba = "acbDEF123£".getBytes(UTF8);
        assertFalse(isAscii(ba));
    }

    @Test
    public void testBoundryIsAscii() {
        byte[] ba = new byte[0];
        assertTrue(isAscii(ba));
    }

    @Test
    public void testIsUtf8() {
        byte[] ba = "abcDEF123£🜁".getBytes(UTF8);
        assertTrue(isValidEncoding(ba, UTF8));

        ba = DatatypeConverter.parseHexBinary("ed9fbf");
        assertTrue(isValidEncoding(ba, UTF8));
    }

    @Test
    public void testNegIsUtf8() {
        byte[] ba = "abcDEF123".getBytes(Charset.forName("UTF-16"));
        assertFalse(isValidEncoding(ba, UTF8));

        //The following two bytes cannot appear in a correct UTF-8 string
        ba = DatatypeConverter.parseHexBinary("feff");
        assertFalse(isValidEncoding(ba, UTF8));

        //overlong ascii character
        ba = DatatypeConverter.parseHexBinary("c1bf");
        assertFalse(isValidEncoding(ba, UTF8));

        //Overlong representation of the NUL character
        ba = DatatypeConverter.parseHexBinary("c080");
        assertFalse(isValidEncoding(ba, UTF8));

    }

    @Test
    public void testBoundryIsUtf8() {
        byte[] ba = new byte[0];
        assertTrue(isValidEncoding(ba, UTF8));
    }

    @Test
    public void testByteSearch() {
        byte[] str = ("foobar" + NL + "foobar").getBytes(defaultCharset);
        byte[] pat = NL.getBytes(defaultCharset);
        assertTrue(indexOf(str, pat) > 0);

        str = ("" + NL + "foobar").getBytes(defaultCharset);
        pat = NL.getBytes();
        assertTrue(indexOf(str, pat) == 0);

        str = ("foobar" + NL + "").getBytes(defaultCharset);
        pat = NL.getBytes();
        assertTrue(indexOf(str, pat) > 0);

        str = ("foo" + NL + "" + NL + "bar").getBytes(defaultCharset);
        pat = NL.getBytes();
        assertTrue(indexOf(str, pat) > 0);

    }

    @Test
    public void testNegByteSearch() {
        byte[] str = ("foobar-foobar").getBytes(defaultCharset);
        byte[] pat = NL.getBytes(defaultCharset);
        assertTrue(indexOf(str, pat) == -1);

        str = "".getBytes(defaultCharset);
        pat = NL.getBytes();
        assertTrue("Pattern should not be found in string ", indexOf(str, pat) == -1);
    }

    @Test
    public void testContainsNl() {
        byte[] str = ("foobar" + NL + "foobar").getBytes(defaultCharset);
        assertTrue(containsNl(str, UTF8));
    }

    @Test
    public void testNegContainsNl() {
        byte[] str = "foobar".getBytes(defaultCharset);
        assertFalse("Incorrectly finding NL character in test string", containsNl(str, defaultCharset));
    }

    @Test
    public void testTrigram() {
        String[] trigrams = Utils.trigrams("abc    defghij - Lmn" + NL + "12345" + NL + "\r6789");
        Assert.assertEquals("abc", trigrams[0]);
        Assert.assertEquals("   ", trigrams[1]);
        Assert.assertEquals(" de", trigrams[2]);
        Assert.assertEquals("fgh", trigrams[3]);
        Assert.assertEquals("ij ", trigrams[4]);
        Assert.assertEquals("- L", trigrams[5]);
        Assert.assertEquals("mn1", trigrams[6]);
        Assert.assertEquals("234", trigrams[7]);
        Assert.assertEquals("567", trigrams[8]);
        Assert.assertEquals("89 ", trigrams[9]);

        trigrams = Utils.trigrams("a");
        Assert.assertEquals("a  ", trigrams[0]);

        trigrams = Utils.trigrams("abc");
        Assert.assertEquals("abc", trigrams[0]);

        trigrams = Utils.trigrams(null);
        Assert.assertTrue(trigrams.length == 0);

        trigrams = Utils.trigrams("");
        Assert.assertTrue(trigrams.length == 0);
    }
}

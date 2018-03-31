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
package org.filevinder.ui.it;

import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.List;
import static org.filevinder.interfaces.SysProps.FV_TEST_ROOT;

/**
 * A collection of variables used in the unit tests.
 *
 * @author Gregory Clarke
 */
public final class Constants {

    private Constants() {
    }

    static final String TEST_ROOT_VAL = System.getenv(FV_TEST_ROOT.toString());

    static final String TEST_FOLDER1 = TEST_ROOT_VAL + "testfiles1";

    static final List<String> ROOT_FILES = asList(
            "/a", "/b", "/c", "/d", "/e", "/f", "/g", "/h", "/i", "/j");

    static final int FILES_TOTAL = ROOT_FILES.size() * ROOT_FILES.size();

    static final String IDX_ON_DISK = TEST_ROOT_VAL + "testfiles2/index.dat";

    static final String ANSI_LRG = TEST_ROOT_VAL + "testfiles2/ansi_large.txt";

    static final String ANSI_TST = TEST_ROOT_VAL + "testfiles2/ansi_test.txt";

    static final String NO_LINES = TEST_ROOT_VAL + "testfiles2/no_lines.txt";

    static final String RUSSIAN_UTF_8 = TEST_ROOT_VAL + "testfiles2/russian_utf8.htm";

    static final String RUSSIAN_UTF_16 = TEST_ROOT_VAL + "testfiles2/russian_utf-16le.htm";

    static final String CHINESE_UTF_8 = TEST_ROOT_VAL + "testfiles2/chinese_utf8.htm";

    static final String CHINESE_UTF_16 = TEST_ROOT_VAL + "testfiles2/chinese_utf-16le.htm";

    static final String UTF8_MULTI_BYTE = TEST_ROOT_VAL + "testfiles2/utf8_multibyte_test.txt";

    static final String INDEX_TXT = TEST_ROOT_VAL + "index.txt";

    static final String INDEX_BYTE = TEST_ROOT_VAL + "index.byte";

    public static final List<String> TXT = Arrays.asList(
            "Sed ut perspiciatis unde omnis iste natus error sit voluptatem ",
            "accusantium doloremque laudantium, totam rem aperiam, ",
            "eaque ipsa quae ab illo inventore veritatis et quasi ",
            "architecto beatae vitae dicta sunt explicabo. Nemo enim ",
            "ipsam voluptatem quia voluptas sit aspernatur aut odit ",
            "aut fugit, sed quia consequuntur magni dolores eos qui ",
            "ratione voluptatem sequi nesciunt. Neque porro quisquam ",
            "est, qui dolorem ipsum quia dolor sit amet, ",
            "consectetur, adipisci velit, sed quia non numquam eius ",
            "modi tempora incidunt ut labore et dolore magnam ",
            "aliquam quaerat voluptatem. Ut enim ad minima veniam, ",
            "quis nostrum exercitationem ullam corporis suscipit ",
            "laboriosam, nisi ut aliquid ex ea commodi consequatur? ",
            "Quis autem vel eum iure reprehenderit qui in ea ",
            "voluptate velit esse quam nihil molestiae consequatur, ",
            "vel illum qui dolorem eum fugiat quo voluptas ",
            "nulla pariatur?",
            "At vero eos et accusamus et iusto odio dignissimos ",
            "ducimus qui blanditiis praesentium voluptatum deleniti ",
            "atque corrupti quos dolores et quas molestias excepturi ",
            "sint occaecati cupiditate non provident, similique sunt ",
            "in culpa qui officia deserunt mollitia animi, id est ",
            "laborum et dolorum fuga. Et harum quidem rerum facilis ",
            "est et expedita distinctio. Nam libero tempore, cum soluta ",
            "nobis est eligendi optio cumque nihil impedit quo minus ",
            "id quod maxime placeat facere possimus, omnis voluptas ",
            "assumenda est, omnis dolor repellendus. Temporibus autem ",
            "quibusdam et aut officiis debitis aut rerum ",
            "necessitatibus saepe eveniet ut et voluptates ",
            "repudiandae sint et molestiae non recusandae. Itaque ",
            "earum rerum hic tenetur a sapiente delectus, ut aut ",
            "reiciendis voluptatibus maiores alias consequatur aut ",
            "perferendis doloribus asperiores repellat.");
}

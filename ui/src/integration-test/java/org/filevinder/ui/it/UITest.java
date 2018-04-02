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
package org.filevinder.ui.it;

import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import org.filevinder.ui.Main;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxRobotException;
import org.testfx.framework.junit.ApplicationTest;

/**
 * TestFX tests of the UI.
 *
 * @author Gregory Clarke
 */
public final class UITest extends ApplicationTest {

    FxRobot robot = new FxRobot();

    TableView playerTable;
    Button prevButton;
    TextInputControl text, location, fileTypes;

    @BeforeClass
    public static void setUpClass() throws Exception {
        ApplicationTest.launch(Main.class);
    }

    @Before
    public void setUp() {
        prevButton = lookup("#prevButton").queryButton();
        text = lookup("#searchText").queryTextInputControl();
        location = lookup("#searchLocation").queryTextInputControl();
        fileTypes = lookup("#searchFileTypes").queryTextInputControl();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.show();
    }

    @Ignore
    @Test(expected = FxRobotException.class)
    public void clickNonexistentElement() {
        clickOn("Nonexistent Element");
    }

    @Ignore
    @Test
    public void doSearch() {
        clickOn("#searchText");
        write("foo");
        clickOn(location);
        write("c:\\temp");
        clickOn("#searchFileTypes");
        write("*.txt");
        clickOn("#searchButton");
//        press(KeyCode.TAB);
//        push(KeyCode.TAB);
//        write(KeyCode.TAB);
        clickOn(prevButton);

//        try {
//            Thread.sleep(5000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}

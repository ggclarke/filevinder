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
package org.filevinder.ui.test;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.filevinder.ui.view.MainView;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

/**
 *
 * @author Gregory Clarke
 */
public final class ViewTest extends ApplicationTest {

    @Override
    public void start(final Stage primaryStage) {
        Scene scene = (new MainView()).setTheScene(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Test
    public void shouldContainSearchButton() {
        assertEquals(lookup("#searchButton").queryButton().getText(), "Search");
    }

}

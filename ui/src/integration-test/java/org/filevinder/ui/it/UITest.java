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

import javafx.scene.Scene;
import static javafx.scene.input.MouseButton.PRIMARY;
import javafx.stage.Stage;
import org.filevinder.ui.Main;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;

/**
 *
 * @author Gregory Clarke
 * 
 */
public final class UITest extends ApplicationTest {

    @Override
    public void start(final Stage primaryStage) {
        Scene scene = (new Main()).setTheScene(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Test
    public void shouldContainSearchButton() {
        assertEquals(lookup("#searchButton").queryButton().getText(), "Search");
    }

    @Test
    public void shouldContainClearButton() {
        assertEquals(lookup("#clearButton").queryButton().getText(), "Clear");
    }

    @Test
    public void shouldContainNextButton() {
        assertEquals(lookup("#nextButton").queryButton().getText(), ">");
    }

    @Test
    public void shouldContainPrevButton() {
        assertEquals(lookup("#prevButton").queryButton().getText(), "<");
    }

    @Test
    public void searchShouldReturnResults() {

        FxRobot robot = new FxRobot();
        lookup("#searchText").queryTextInputControl().setText("accusamus");
//        lookup("#searchLocation").queryTextInputControl().setText(TEST_FOLDER1);
        lookup("#searchFileTypes").queryTextInputControl().setText("*.dat");

//        Node search = lookup("#searchButton").queryButton();
//        System.err.println(search.getTranslateX());
//        System.out.println(search.getTranslateX());
//        System.err.println(search.getTranslateY());
//        moveTo(position(Pos.TOP_LEFT, 1, 2)).clickOn(PRIMARY);

        moveTo(lookup("#searchButton").queryButton());
//        clickOn();
        clickOn(lookup("#searchButton").queryButton(), PRIMARY);

//        try{wait(5000);}catch(Exception e){}        
//        int size = lookup("#searchResultsTable").queryTableView().getItems().size();
//        Assert.assertTrue(size > 0);

    }

}

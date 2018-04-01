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
package org.filevinder.ui;

import org.filevinder.ui.presentation.SearchModel;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import static org.filevinder.ui.UIConstants.WINDOW_HEIGHT;
import static org.filevinder.ui.UIConstants.WINDOW_WIDTH;
import org.filevinder.ui.fs.FilesUtilImpl;
import org.filevinder.ui.fs.SearchHistoryStore;
import org.filevinder.ui.presentation.FilesUtil;
import org.filevinder.ui.usecase.SearchInteractorImpl;
import org.filevinder.ui.presentation.SearchPresenter;
import org.filevinder.ui.presentation.SearchView;
import org.filevinder.ui.presentation.SearchController;
import org.filevinder.ui.usecase.SearchHistory;

/**
 * The main Main is value bound to the model and invokes the controller.
 *
 * @author Gregory Clarke
 */
public final class Main extends Application {

    private final SearchModel searchModel;
    private final SearchController searchController;
    private final SearchInteractorImpl searchInteractor;
    private final SearchView searchView;
    private final SearchPresenter searchPresenter;
    private final SearchHistory searchHistory;
    private final FilesUtil filesUtil;

    /**
     * Initialise he various UI components.
     */
    public Main() {
        filesUtil = new FilesUtilImpl();
        searchModel = new SearchModel();
        searchPresenter = new SearchPresenter(searchModel);
        searchHistory = new SearchHistoryStore();
        searchInteractor = new SearchInteractorImpl(searchPresenter, searchHistory);
        searchController = new SearchController(searchModel, searchInteractor);
        searchView = new SearchView(searchModel, searchController, filesUtil);
    }

    @Override
    public void start(final Stage primaryStage) {
        Scene scene = setTheScene(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Returns the main scene object for the application.
     *
     * @param primaryStage the stage
     * @return the scene
     */
    public Scene setTheScene(final Stage primaryStage) {
        VBox root = new VBox();
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add("filevinder.css");
        addMenuBar(primaryStage, scene);

        HBox searchFields = searchView.addSearchFields();
        ((VBox) scene.getRoot()).getChildren().add(searchFields);

        HBox resultsAndDetails = searchView.addSearchResultsAndDetailPane(scene);
        ((VBox) scene.getRoot()).getChildren().add(resultsAndDetails);

        setGlobalEnterKeyEventHandler(root);

        primaryStage.setMinHeight(WINDOW_HEIGHT);
        primaryStage.setMinWidth(WINDOW_WIDTH);
        primaryStage.setTitle("filevinder");
        return scene;
    }

    private void addMenuBar(final Stage primaryStage, final Scene scene) {
        MenuBar menuBar = new MenuBar();
        Menu mainMenu = new Menu("File");
        MenuItem exitCmd = new MenuItem("Exit");
        mainMenu.getItems().addAll(exitCmd);
        menuBar.getMenus().addAll(mainMenu);

        exitCmd.setOnAction(
                (ActionEvent e) -> primaryStage.close()
        );

        exitCmd.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
        ((VBox) scene.getRoot()).getChildren().add(menuBar);
    }

    private void setGlobalEnterKeyEventHandler(final Node root) {
        root.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                searchController.search();
                ev.consume();
            }
        });
    }

    /**
     * Launch the application UI.
     *
     * @param args Runtime arguments
     */
    public static void main(final String[] args) {
        launch(args);
    }

}

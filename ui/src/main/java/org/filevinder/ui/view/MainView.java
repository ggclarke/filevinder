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
package org.filevinder.ui.view;

import org.filevinder.ui.model.Model;
import java.util.SortedSet;
import java.util.TreeSet;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import static org.filevinder.ui.Constants.GAP_S;
import static org.filevinder.ui.Constants.GAP_XS;
import static org.filevinder.ui.Constants.PAD_M;
import static org.filevinder.ui.Constants.TEXT_AREA_MIN_WIDTH;
import static org.filevinder.ui.Constants.WINDOW_HEIGHT;
import static org.filevinder.ui.Constants.WINDOW_WIDTH;
import static org.filevinder.ui.controller.SearchController.clear;
import static org.filevinder.ui.controller.SearchController.next;
import static org.filevinder.ui.controller.SearchController.prev;
import static org.filevinder.ui.controller.SearchController.search;
import org.filevinder.ui.model.SearchResult;

/**
 * The main MainView is value bound to the model and invokes the controller.
 *
 * @author Gregory Clarke
 */
public final class MainView extends Application {

    @Override
    public void start(final Stage primaryStage) {
        Scene scene = setTheScene(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Returns the main scene object for the application.
     * @param primaryStage the stage
     * @return the scene
     */
    public Scene setTheScene(final Stage primaryStage) {
        VBox root = new VBox();
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add("filevinder.css");
        addMenuBar(primaryStage, scene);
        addTopRegion(scene);
        addBottomRegion(scene);
        setGlobalEnterKeyEventHandler(root);
        primaryStage.setMinHeight(WINDOW_HEIGHT);
        primaryStage.setMinWidth(WINDOW_WIDTH);
        primaryStage.setTitle("filevinder");
        return scene;
    }

    private void addTopRegion(final Scene scene) {
        GridPane grid = createSearchGrid();
        HBox hbox = new HBox();
        hbox.getChildren().addAll(grid);
        ((VBox) scene.getRoot()).getChildren().add(hbox);
    }

    private void addBottomRegion(final Scene scene) {
        TabPane tabPane = makeSearchTabPane(scene);
        ScrollPane fileDetail = makeFileDetailPane(scene);
        HBox hbox = new HBox();
        hbox.getChildren().addAll(tabPane, fileDetail);
        ((VBox) scene.getRoot()).getChildren().add(hbox);
    }

    private TabPane makeSearchTabPane(final Scene scene) {

        TabPane tabPane = new TabPane();

        Tab plainTextTab = new Tab();
        TableView<SearchResult> resultsTable = makePlainTextResultsTable(scene);
        plainTextTab.setText("Plain Text");
        plainTextTab.setContent(resultsTable);

        Tab compressedTab = new Tab();
        compressedTab.setText("Compressed");
        compressedTab.setContent(new Region());
        tabPane.getTabs().addAll(plainTextTab, compressedTab);

        return tabPane;
    }

    private ScrollPane makeFileDetailPane(final Scene scene) {
        TextFlow textFlow = new TextFlow();
        Text fileTxt = new Text();

        //bind the displayed text to the model
        fileTxt.textProperty().bind(Bindings.convert(Model.getInstance().fileTextProperty()));

        textFlow.getChildren().addAll(fileTxt);
        VBox vBox = new VBox();
        vBox.getChildren().add(textFlow);
        textFlow.setMinWidth(TEXT_AREA_MIN_WIDTH);
        ScrollPane scrollPane = new ScrollPane(vBox);

        return scrollPane;
    }

    private TableView<SearchResult> makePlainTextResultsTable(final Scene scene) {

        TableView<SearchResult> resultsTable = new TableView<>();

        TableColumn nameCol = new TableColumn("Name");
        TableColumn pathCol = new TableColumn("Path");
        TableColumn sizeCol = new TableColumn("Size");
        TableColumn typeCol = new TableColumn("Type");
        TableColumn modifiedCol = new TableColumn("Modified");
        TableColumn createdCol = new TableColumn("Created");
        TableColumn accessedCol = new TableColumn("Accessed");
        TableColumn permissionCol = new TableColumn("Permission");

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        pathCol.setCellValueFactory(new PropertyValueFactory<>("path"));
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        modifiedCol.setCellValueFactory(new PropertyValueFactory<>("modified"));
        createdCol.setCellValueFactory(new PropertyValueFactory<>("created"));
        accessedCol.setCellValueFactory(new PropertyValueFactory<>("accessed"));
        permissionCol.setCellValueFactory(new PropertyValueFactory<>("permission"));

        resultsTable.getColumns().addAll(nameCol, pathCol, sizeCol, typeCol,
                modifiedCol, createdCol, accessedCol, permissionCol);

        //create the table binding with the model
        resultsTable.setItems(Model.getInstance().getSearchResults());

        return resultsTable;
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

    private GridPane createSearchGrid() {
        final int row4 = 4, row3 = 3, row2 = 2, row1 = 1;
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(GAP_S);
        grid.setVgap(GAP_S);
        grid.setPadding(new Insets(PAD_M, PAD_M, PAD_M, PAD_M));
        makeSearchTextInput(grid, row1);
        makeSearchLocationInput(grid, row2);
        makeFileTypesInput(grid, row3);
        grid.add(searchBox(), 0, row4);
        return grid;
    }

    private void makeFileTypesInput(final GridPane grid, final int row) {
        Tooltip tooltip = new Tooltip();
        tooltip.setText("List multiple file types in CSV form");
        Label fileName = new Label("Search File Types");
        fileName.setTooltip(tooltip);
        grid.add(fileName, 0, row);
        TextField searchFileTypes = new TextField();
        searchFileTypes.textProperty().bindBidirectional(Model.getInstance().searchFileTypesProperty());
        grid.add(searchFileTypes, 1, row);
    }

    private void makeSearchLocationInput(final GridPane grid, final int row) {
        Tooltip tooltip = new Tooltip();
        tooltip.setText("List multiple locations in CSV form");
        Label searchInLbl = new Label("Search Location");
        searchInLbl.setTooltip(tooltip);
        grid.add(searchInLbl, 0, row);
        SortedSet<String> prompts = new TreeSet<>();
        prompts.add(""); //TODO: why is this needed?
        AutoCompleteTextField fsAutoCompleter = new AutoCompleteTextField(prompts);
        //TODO: is this needed?
        fsAutoCompleter.textProperty().bindBidirectional(Model.getInstance().searchLocationProperty());
        grid.add(fsAutoCompleter, 1, row);
    }

    private void makeSearchTextInput(final GridPane grid, final int row) {
        Tooltip tooltip = new Tooltip();
        tooltip.setText("Supports file matches as a 'glob' pattern");
        Label patternLbl = new Label("Search Text");
        patternLbl.setTooltip(tooltip);
        grid.add(patternLbl, 0, row);
        TextField searchText = new TextField();
        searchText.textProperty().bindBidirectional(Model.getInstance().searchTextProperty());
        grid.add(searchText, 1, row);
    }

    private HBox searchBox() {
        HBox hBox = new HBox();
        Button search = new Button("Search");
        search.setId("searchButton");
        search.setOnAction((ActionEvent ae) -> search());

        Button prev = new Button("<");
        prev.setId("prevButton");
        prev.setOnAction((ActionEvent ae) -> prev());

        Button next = new Button(">");
        next.setId("nextButton");
        next.setOnAction((ActionEvent ae) -> next());

        Button clear = new Button("Clear");
        clear.setId("clearButton");
        clear.setOnAction((ActionEvent ae) -> clear());

        Region spacer1 = new Region();
        spacer1.setMinWidth(GAP_XS);

        Region spacer2 = new Region();
        spacer2.setMinWidth(GAP_XS);

        hBox.getChildren().addAll(search, spacer1, prev, next, spacer2, clear);
        return hBox;
    }

    private void setGlobalEnterKeyEventHandler(final Node root) {
        root.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                search();
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

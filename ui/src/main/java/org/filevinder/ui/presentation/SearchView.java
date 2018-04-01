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
package org.filevinder.ui.presentation;

import java.util.SortedSet;
import java.util.TreeSet;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import static org.filevinder.ui.presentation.JSUtils.jsClean;
import static org.filevinder.ui.presentation.JSUtils.loadFile;
import static org.filevinder.ui.UIConstants.GAP_S;
import static org.filevinder.ui.UIConstants.GAP_XS;
import static org.filevinder.ui.UIConstants.PAD_M;

/**
 * The view covering all search use case features.
 *
 * @author Gregory Clarke
 */
public final class SearchView {

    private static final int COL_0 = 0, COL_1 = 1;
    private static final int ROW_4 = 4, ROW_3 = 3, ROW_2 = 2, ROW_1 = 1;

    private final SearchModel searchModel;
    private final SearchController searchController;
    private final FilesUtil filesUtil;

    /**
     * Construct a search view object.
     *
     * @param model The search view model
     * @param controller The search view controller
     * @param fsUtil File System utility
     */
    public SearchView(final SearchModel model, final SearchController controller,
            final FilesUtil fsUtil) {
        searchModel = model;
        searchController = controller;
        filesUtil = fsUtil;
    }

    /**
     * Returns an HBox containing the input fields for a search.
     * @return hbox layout component
     */
    public HBox addSearchFields() {
        GridPane grid = createSearchGrid();
        HBox hbox = new HBox();
        hbox.getChildren().addAll(grid);
        return hbox;
    }

    /**
     * Creates a HBox with the search results and details panes.
     * @param scene the scene
     * @return HBox returned HBox
     */
    public HBox addSearchResultsAndDetailPane(final Scene scene) {
        TabPane tabPane = makeSearchTabPane(scene);
        HBox hbox = new HBox();
        VBox fileDetail = makeCodeEditor();
        hbox.getChildren().addAll(tabPane, fileDetail);
        return hbox;
    }

    private TabPane makeSearchTabPane(final Scene scene) {

        TabPane tabPane = new TabPane();

        Tab plainTextTab = new Tab();
        TableView<SearchResultModel> resultsTable = makePlainTextResultsTable(scene);
        resultsTable.setId("searchResultsTable");
        plainTextTab.setText("Plain Text");
        plainTextTab.setContent(resultsTable);

        Tab compressedTab = new Tab();
        compressedTab.setText("Compressed");
        compressedTab.setContent(new Region());
        tabPane.getTabs().addAll(plainTextTab, compressedTab);

        return tabPane;
    }

    private VBox makeCodeEditor() {
        Label title = new Label("Name: myfile.foo");
        title.setStyle("-fx-font-size: 10;");
        final CodeEditor editor = new CodeEditor();
        final WebEngine engine = editor.webview.getEngine();

        final Button undo = new Button("Undo");
        undo.setOnAction((ActionEvent actionEvent) -> {
            engine.executeScript("editor.setValue('" + jsClean(editor.getSavedTxt()) + "')");
        });

        final Button save = new Button("Save");
        save.setOnAction((ActionEvent actionEvent) -> {
            String txt = (String) engine.executeScript("editor.getValue();");
            editor.setSavedTxt(txt);
        });

        final Button load = new Button("Load");
        load.setOnAction((ActionEvent actionEvent) -> {
            engine.executeScript("editor.setValue('" + jsClean(loadFile(this.getClass())) + "')");
        });

        // layout the scene.
        final HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);
        buttonBox.getChildren().addAll(load, save, undo);

        final VBox layout = new VBox();
        layout.setSpacing(10);
        layout.getChildren().addAll(title, buttonBox, editor);
        layout.setStyle("-fx-background-color: cornsilk; -fx-padding: 10;");

        return layout;
    }

    private TableView<SearchResultModel> makePlainTextResultsTable(final Scene scene) {

        TableView<SearchResultModel> resultsTable = new TableView<>();

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
        resultsTable.setItems(searchModel.getSearchResults());

        return resultsTable;
    }

    private GridPane createSearchGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(GAP_S);
        grid.setVgap(GAP_S);
        grid.setPadding(new Insets(PAD_M, PAD_M, PAD_M, PAD_M));
        makeSearchTextInput(grid, ROW_1);
        makeSearchLocationInput(grid, ROW_2);
        makeFileTypesInput(grid, ROW_3);
        grid.add(searchBox(), COL_0, ROW_4);
        return grid;
    }

    private void makeFileTypesInput(final GridPane grid, final int row) {
        Tooltip tooltip = new Tooltip();
        tooltip.setText("List multiple file types in CSV form");
        Label fileName = new Label("Search File Types");
        fileName.setTooltip(tooltip);
        grid.add(fileName, COL_0, row);
        TextField searchFileTypes = new TextField();
        searchFileTypes.setId("searchFileTypes");
        searchFileTypes.textProperty().bindBidirectional(searchModel.searchFileTypesProperty());
        grid.add(searchFileTypes, COL_1, row);
    }

    private void makeSearchLocationInput(final GridPane grid, final int row) {
        Tooltip tooltip = new Tooltip();
        tooltip.setText("List multiple locations in CSV form");
        Label searchLocLbl = new Label("Search Location");
        searchLocLbl.setTooltip(tooltip);
        grid.add(searchLocLbl, COL_0, row);
        SortedSet<String> prompts = new TreeSet<>();
        prompts.add(""); //TODO: why is this needed?
        FilePathCompleterTextField fsAutoCompleter = new FilePathCompleterTextField(prompts, filesUtil);
        fsAutoCompleter.setId("searchLocation");
        fsAutoCompleter.textProperty().bindBidirectional(searchModel.searchLocationProperty());
        grid.add(fsAutoCompleter, COL_1, row);
    }

    private void makeSearchTextInput(final GridPane grid, final int row) {
        Tooltip tooltip = new Tooltip();
        tooltip.setText("Supports file matches as a 'glob' pattern");
        Label patternLbl = new Label("Search Text");
        patternLbl.setTooltip(tooltip);
        grid.add(patternLbl, COL_0, row);
        TextField searchText = new TextField();
        searchText.setId("searchText");
        searchText.textProperty().bindBidirectional(searchModel.searchTextProperty());
        grid.add(searchText, COL_1, row);
    }

    private HBox searchBox() {
        HBox hBox = new HBox();
        Button search = new Button("Search");
        search.setId("searchButton");
        search.setOnAction((ActionEvent ae) -> searchController.search());

        Button prev = new Button("<");
        prev.setId("prevButton");
        prev.setOnAction((ActionEvent ae) -> searchController.prev());

        Button next = new Button(">");
        next.setId("nextButton");
        next.setOnAction((ActionEvent ae) -> searchController.next());

        Button clear = new Button("Clear");
        clear.setId("clearButton");
        clear.setOnAction((ActionEvent ae) -> searchController.clear());

        Region spacer1 = new Region();
        spacer1.setMinWidth(GAP_XS);

        Region spacer2 = new Region();
        spacer2.setMinWidth(GAP_XS);

        hBox.getChildren().addAll(search, spacer1, prev, next, spacer2, clear);
        return hBox;
    }

}

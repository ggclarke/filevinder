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
package org.filevinder.ui.presentation;

import static java.lang.System.out;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.TextField;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * This class is a TextField which implements an "autocomplete" functionality,
 * based on a supplied list of menuEntries.<p>
 *
 * If the entered text matches a part of any of the supplied menuEntries these
 * are going to be displayed in a popup. Further the matching part of the entry
 * is going to be displayed in a special style, defined by
 * {@link #textOccurenceStyle textOccurenceStyle}. The maximum number of
 * displayed menuEntries in the popup is defined by
 * {@link #maxEntries maxEntries}.<br>
 * By default the pattern matching is not case-sensitive. This behaviour is
 * defined by the {@link #caseSensitive caseSensitive}
 * .<p>

 The FilePathCompleterTextField also has a List of
 {@link #filteredEntries filteredEntries} that is equal to the search results
 * if search results are not empty, or {@link #filteredEntries filteredEntries}
 * is equal to {@link #popupEntries menuEntries} otherwise. If
 * {@link #popupHidden popupHidden} is set to true no popup is going to be
 * shown. This list can be used to bind all menuEntries to another node (a
 * ListView for example) in the following way:
 * <pre>
 * <code>
 FilePathCompleterTextField auto = new FilePathCompleterTextField(menuEntries);
 auto.setPopupHidden(true);
 SimpleListProperty filteredEntries = new SimpleListProperty(auto.getFilteredEntries());
 listView.itemsProperty().bind(filteredEntries);
 </code>
 * </pre>
 *
 * @author Gregory Clarke
 */
final class FilePathCompleterTextField extends TextField {

    private static final int DEFAULT_MAX_ENTRIES = 10;

    /**
     * The existing auto complete menuEntries.
     */
    private SortedSet<String> popupEntries;
    /**
     * The set of filtered menuEntries:<br>
     * Equal to the search results if search results are not empty, equal to
     * {@link #popupEntries menuEntries} otherwise.
     */
    private final ObservableList<String> filteredEntries;

    /**
     * The popup used to select an entry.
     */
    private final ContextMenu popupMenu;

    /**
     * Indicates whether the search is case sensitive or not. <br>
     * Default: false
     */
    private boolean caseSensitive = false;

    /**
     * Indicates whether the Popup should be hidden or displayed. Use this if
     * you want to filter an existing list/set (for example values of a
     * {@link javafx.scene.control.ListView ListView}). Do this by binding
     * {@link #getFilteredEntries() getFilteredEntries()} to the list/set.
     */
    private boolean popupHidden = false;

    /**
     * The CSS style that should be applied on the parts in the popup that match
     * the entered text. <br>
     * Default: "-fx-font-weight: bold; -fx-fill: red;"
     * <p>
     * Note: This style is going to be applied on an
     * {@link javafx.scene.text.Text Text} instance. See the <i>JavaFX CSS
     * Reference Guide</i> for available CSS Propeties.
     */
    private String textOccurenceStyle = "-fx-font-weight: bold; "
            + "-fx-fill: black;";
    /**
     * The maximum Number of menuEntries displayed in the popup.<br>
     * Default: 10
     */
    private int maxEntries;

    private FilesUtil filesUtil;

    /**
     * Construct a new AutoCompleteTextField.
     * @param entrySet the entry set
     */
    FilePathCompleterTextField(final SortedSet<String> entrySet, final FilesUtil fsu) {
        super();
        this.filesUtil = fsu;
        this.maxEntries = DEFAULT_MAX_ENTRIES;
        this.filteredEntries = FXCollections.observableArrayList();

        this.popupEntries = (entrySet == null ? new TreeSet<>() : entrySet);
        this.filteredEntries.addAll(popupEntries);
        popupMenu = new ContextMenu();

        textProperty().addListener(new TextInputChangeListener(entrySet));
        addFocusListener();
    }

    private void addFocusListener() {
        focusedProperty().addListener(
                (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    out.println("Focus on " + this.getClass().getName());
                    popupMenu.hide();
                }
        );
    }

    /**
     * Populate the entry set with the given search results.
     * Display is limited to 10 menuEntries, for performance.
     *
     * @param searchResult The set of matching strings.
     */
    private void buildContextPopupMenu(final List<String> searchResult, final String textArg) {

        String text = filesUtil.partialFolderName(textArg);
        List<CustomMenuItem> menuItems = new LinkedList<>();
        int count = Math.min(searchResult.size(), getMaxEntries());
        for (int i = 0; i < count; i++) {
            final String result = searchResult.get(i);
            int occurence;

            if (isCaseSensitive()) {
                occurence = result.indexOf(text);
            } else {
                occurence = result.toLowerCase().indexOf(text.toLowerCase());
            }

            //Part before occurence (might be empty)
            Text pre = new Text(result.substring(0, occurence));
            //Part of (first) occurence
            Text in = new Text(result.substring(occurence,
                    occurence + text.length()));
            in.setStyle(getTextOccurenceStyle());
            //Part after occurence
            Text post = new Text(result.substring(occurence + text.length(),
                    result.length()));

            TextFlow entryFlow = new TextFlow(pre, in, post);
            CustomMenuItem item = new CustomMenuItem(entryFlow, true);
            setActionOnPopupItemSelect(item, textArg, result);
            menuItems.add(item);
        }
        popupMenu.getItems().clear();
        popupMenu.getItems().addAll(menuItems);

    }

    private void setActionOnPopupItemSelect(final CustomMenuItem item, final String textArg, final String result) {
        item.setOnAction(
                (ActionEvent event) -> {
                    out.println("Setting text on " + this.getClass().getName());
                    String newText = filesUtil.autoCompletePath(textArg, result);
                    setText(newText);
                    requestFocus();
                    this.positionCaret(newText.length());
                    popupMenu.hide();
                });
    }

    /**
     * A change listener for the dropdown text box.
     */
    private class TextInputChangeListener implements ChangeListener<String> {

        private final SortedSet<String> entrySet;

        TextInputChangeListener(final SortedSet<String> entrySetVal) {
            this.entrySet = entrySetVal;
        }

        @Override
        public void changed(final ObservableValue<? extends String> observableValue,
                final String s, final String s2) {

            if (getText() == null || getText().length() == 0) {
                filteredEntries.clear();
                filteredEntries.addAll(popupEntries);
                popupMenu.hide();

            } else {
                LinkedList<String> searchResult = new LinkedList<>();

                //Check if the entered Text is part of some entry
                String text = getText();

                //update menuEntries based on current folder
                popupEntries = filesUtil.foldersInCurrentPath(text);
                String partialPath = filesUtil.partialFolderName(text);

                for (String entry : popupEntries) {
                    if (entry.contains(partialPath)) {
                        searchResult.add(entry);
                    }
                }

                if (entrySet.size() > 0) {
                    filteredEntries.clear();
                    filteredEntries.addAll(searchResult);

                    //Only show popup if not in filter mode
                    if (!isPopupHidden()) {
                        buildContextPopupMenu(searchResult, text);
                        if (!popupMenu.isShowing()) {
                            popupMenu.show(FilePathCompleterTextField.this, Side.BOTTOM, 0, 0);
                        }
                    }
                } else {
                    popupMenu.hide();
                }
            }
        }
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public String getTextOccurenceStyle() {
        return textOccurenceStyle;
    }

    public void setCaseSensitive(final boolean caseSensitiveVal) {
        this.caseSensitive = caseSensitiveVal;
    }

    public void setTextOccurenceStyle(final String textOccurenceStyleVal) {
        this.textOccurenceStyle = textOccurenceStyleVal;
    }

    public boolean isPopupHidden() {
        return popupHidden;
    }

    public void setPopupHidden(final boolean popupHiddenVal) {
        this.popupHidden = popupHiddenVal;
    }

    public ObservableList<String> getFilteredEntries() {
        return filteredEntries;
    }

    public int getMaxEntries() {
        return maxEntries;
    }

    public void setMaxEntries(final int maxEntriesVal) {
        this.maxEntries = maxEntriesVal;
    }

    public SortedSet<String> getEntries() {
        return popupEntries;
    }

}

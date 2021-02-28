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

import java.net.URL;

import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

/**
 * A syntax highlighting code editor for JavaFX created by wrapping a CodeMirror
 * code editor in a WebView.
 * <p>
 * See http://codemirror.net for more information on using the 'codemirror'
 * editor.
 */
class CodeEditor extends StackPane {

    /**
     * A 'webview' used to encapsulate the CodeMirror JavaScript.
     */
    //TODO: make this private with accessors methods
    public final WebView webview = new WebView();

    /**
     * A snapshot of the code being edited.
     */
    private String savedTxt;

    /**
     * Create a new code editor.
     *
     */
    public CodeEditor() {

//        webview.setPrefSize(400, 200);
//        webview.setMinSize(400, 200);
//        webview.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);

        URL url = this.getClass().getClassLoader().getResource("editor.html");
        load(url);

        getChildren().add(webview);
    }

    private void load(URL fileToLoad) {

        WebEngine eng = webview.getEngine();

        // process page loading
        eng.getLoadWorker().stateProperty().addListener(
                (ObservableValue<? extends State> ov, State oldState,
                 State newState) -> {
                    if (newState == State.SUCCEEDED) {
                        JSObject win = (JSObject) eng.executeScript("window");
                        win.setMember("jsToJava", new JsToJavaInterface());
                        System.out.println("Created Java-JS bridge.");
                    }
                });

        eng.load(fileToLoad.toString());

    }

    public void setSavedTxt(String str) {
        this.savedTxt = str;
    }

    public String getSavedTxt() {
        return savedTxt;
    }

}

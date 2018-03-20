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
package org.filevinder.factory;

import java.io.File;
import static java.lang.System.err;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import org.filevinder.interfaces.Search;
import static org.filevinder.interfaces.SysProps.FV_CLASSPATH;
import static org.filevinder.interfaces.SysProps.FV_ENG_JAR;
import static org.filevinder.interfaces.SysProps.FV_SEARCH_CLASS;

/**
 * This factory class acts as a bridge between the UI and the engine modules.
 * Following the principle of dependency inversion, there is no compile time
 * dependency between the ui and engine modules. In order then for the interface
 * module to return a concretion, we need to rely on runtime instantiation using
 * the engine module's jar on the application classpath.
 *
 * @author Gregory Clarke
 */
public final class SearchFactory {

    private SearchFactory() {
    }

    private static final String ENG_JAR = System.getenv(FV_ENG_JAR.toString());
    private static final String CLASS_NAME = System.getenv(FV_SEARCH_CLASS.toString());
    private static final String FV_CLASSPATH_STR = System.getenv(FV_CLASSPATH.toString());

    /**
     * Returns the runtime provided implementation of this this class.
     * @return implementation
     */
    public static Search getInstance() {
        try {
            String jarPath = FV_CLASSPATH_STR + ENG_JAR;
            File jarFile = new File(jarPath);
            URLClassLoader loader = URLClassLoader.newInstance(new URL[]{jarFile.toURI().toURL()});
            Class<?> clazz = Class.forName(CLASS_NAME, true, loader);
            Object searchImpl = clazz.newInstance();
            return (Search) searchImpl;

        } catch (InstantiationException | ClassNotFoundException
                | IllegalAccessException | MalformedURLException e) {
            e.printStackTrace(err);
        }
        throw new RuntimeException("Could not instantiate " + CLASS_NAME);
    }

}

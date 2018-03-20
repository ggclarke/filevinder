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

import org.filevinder.factory.SearchFactory;
import org.filevinder.interfaces.Search;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Gregory Clarke
 */
public final class ServiceIT {

    @Test
    public void testModuleIntegration() {
        Search search = SearchFactory.getInstance();
        try {
            search.searchFile(null, null, null);
        } catch (NullPointerException npe) {
            //pass - impl modules was reached
        } catch (RuntimeException re) {
            Assert.fail("A problem occured with module integration between "
                    + "the UI layer and the engine.");
        }
    }
}

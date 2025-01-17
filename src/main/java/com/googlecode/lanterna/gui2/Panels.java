/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
 *
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

/**
 * Utility class for quickly bunching up components in a panel, arranged in a particular pattern
 *
 * @author Martin
 */
public class Panels {

    // Avoid instantiation
    private Panels() {
    }

    public static Panel border() {
        return new Panel(new BorderLayout());
    }

    /**
     * Creates a new {@code Panel} with a {@code GridLayout} layout manager and adds all the components passed in
     *
     * @param columns    Number of columns in the grid
     * @param components Components to be added to the new {@code Panel}, in order
     * @return The new {@code Panel}
     */
    public static Panel grid(int columns, Component... components) {
        return grid(columns).add(components);
    }

    private static Panel grid(int columns) {
        return new Panel(new GridLayout(columns));
    }

    /**
     * Creates a new {@code Panel} with a {@code LinearLayout} layout manager in horizontal mode and adds all the
     * components passed in
     *
     * @param components Components to be added to the new {@code Panel}, in order
     * @return The new {@code Panel}
     */
    public static Panel horizontal(Component... components) {
        return horizontal().add(components);
    }

    private static Panel horizontal() {
        return new Panel(new LinearLayout(Direction.HORIZONTAL));
    }

    /**
     * Creates a new {@code Panel} with a {@code LinearLayout} layout manager in vertical mode and adds all the
     * components passed in
     *
     * @param components Components to be added to the new {@code Panel}, in order
     * @return The new {@code Panel}
     */
    public static Panel vertical(Component... components) {
        return vertical().add(components);
    }

    private static Panel vertical() {
        return new Panel(new LinearLayout(Direction.VERTICAL));
    }
}

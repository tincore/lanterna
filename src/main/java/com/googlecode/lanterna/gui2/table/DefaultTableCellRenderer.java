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
package com.googlecode.lanterna.gui2.table;

import com.googlecode.lanterna.Dimension;
import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.gui2.TextUiGraphics;

/**
 * Default implementation of {@code TableCellRenderer}
 * @param <V> Type of data stored in each table cell
 * @author Martin
 */
public class DefaultTableCellRenderer<V> implements TableCellRenderer<V> {
    @Override
    public Dimension getPreferredSize(Table<V> table, V cell, int columnIndex, int rowIndex) {
        String[] lines = getContent(cell);
        int maxWidth = 0;
        for(String line: lines) {
            int length = TerminalTextUtils.getColumnWidth(line);
            if(maxWidth < length) {
                maxWidth = length;
            }
        }
        return new Dimension(maxWidth, lines.length);
    }

    @Override
    public void drawCell(Table<V> table, V cell, int columnIndex, int rowIndex, TextUiGraphics textUiGraphics) {
        boolean isSelected = (table.getSelectedColumn() == columnIndex && table.getSelectedRow() == rowIndex) ||
                (table.getSelectedRow() == rowIndex && !table.isCellSelection());
        applyStyle(table, cell, columnIndex, rowIndex, isSelected, textUiGraphics);
        beforeRender(table, cell, columnIndex, rowIndex, isSelected, textUiGraphics);
        render(table, cell, columnIndex, rowIndex, isSelected, textUiGraphics);
        afterRender(table, cell, columnIndex, rowIndex, isSelected, textUiGraphics);
    }

    /**
     * Called by the cell renderer to setup all the styling (colors and SGRs) before rendering the cell. This method
     * exists as protected in order to make it easier to extend and customize {@link DefaultTableCellRenderer}. Unless
     * {@link DefaultTableCellRenderer#drawCell(Table, Object, int, int, TextUiGraphics)} it overridden, it will be
     * called when the cell is rendered.
     * @param table Table the cell belongs to
     * @param cell Cell being rendered
     * @param columnIndex Column index of the cell being rendered
     * @param rowIndex Row index of the cell being rendered
     * @param isSelected Set to {@code true} if the cell is currently selected by the user
     * @param textUiGraphics {@link TextUiGraphics} object to set the style on, this will be used in the rendering later
     */
    protected void applyStyle(Table<V> table, V cell, int columnIndex, int rowIndex, boolean isSelected, TextUiGraphics textUiGraphics) {
        ThemeDefinition themeDefinition = table.getThemeDefinition();
        if(isSelected) {
            if(table.isFocused()) {
                textUiGraphics.applyThemeStyle(themeDefinition.getActive());
            }
            else {
                textUiGraphics.applyThemeStyle(themeDefinition.getSelected());
            }
        }
        else {
            textUiGraphics.applyThemeStyle(themeDefinition.getNormal());
        }
    }

    /**
     * Called by the cell renderer to prepare the cell area before rendering the cell. In the default implementation
     * it will clear the area with whitespaces. This method exists as protected in order to make it easier to extend and
     * customize {@link DefaultTableCellRenderer}. Unless
     * {@link DefaultTableCellRenderer#drawCell(Table, Object, int, int, TextUiGraphics)} it overridden, it will be
     * called when the cell is rendered, after setting up the styling but before the cell content text is drawn.
     * @param table Table the cell belongs to
     * @param cell Cell being rendered
     * @param columnIndex Column index of the cell being rendered
     * @param rowIndex Row index of the cell being rendered
     * @param isSelected Set to {@code true} if the cell is currently selected by the user
     * @param textUiGraphics {@link TextUiGraphics} object for the cell, already having been prepared with styling
     */
    protected void beforeRender(Table<V> table, V cell, int columnIndex, int rowIndex, boolean isSelected, TextUiGraphics textUiGraphics) {
        textUiGraphics.fill(' ');
    }

    /**
     * Called by the cell renderer to draw the content of the cell into the assigned area. In the default implementation
     * it will transform the content to multilines are draw them one by one. This method exists as protected in order to
     * make it easier to extend and customize {@link DefaultTableCellRenderer}. Unless
     * {@link DefaultTableCellRenderer#drawCell(Table, Object, int, int, TextUiGraphics)} it overridden, it will be
     * called when the cell is rendered, after setting up the styling and preparing the cell.
     * @param table Table the cell belongs to
     * @param cell Cell being rendered
     * @param columnIndex Column index of the cell being rendered
     * @param rowIndex Row index of the cell being rendered
     * @param isSelected Set to {@code true} if the cell is currently selected by the user
     * @param textUiGraphics {@link TextUiGraphics} object for the cell, already having been prepared with styling
     */
    protected void render(Table<V> table, V cell, int columnIndex, int rowIndex, boolean isSelected, TextUiGraphics textUiGraphics) {
        String[] lines = getContent(cell);
        int rowCount = 0;
        for(String line: lines) {
            textUiGraphics.putString(0, rowCount++, line);
        }
    }

    /**
     * Called by the cell renderer after the cell content has been drawn into the assigned area. In the default
     * implementation it will do nothing. This method exists as protected in order to make it easier to extend and
     * customize {@link DefaultTableCellRenderer}. Unless
     * {@link DefaultTableCellRenderer#drawCell(Table, Object, int, int, TextUiGraphics)} it overridden, it will be
     * called after the cell has been rendered but before the table moves on to the next cell.
     * @param table Table the cell belongs to
     * @param cell Cell being rendered
     * @param columnIndex Column index of the cell being rendered
     * @param rowIndex Row index of the cell being rendered
     * @param isSelected Set to {@code true} if the cell is currently selected by the user
     * @param textUiGraphics {@link TextUiGraphics} object for the cell, already having been prepared with styling
     */
    protected void afterRender(Table<V> table, V cell, int columnIndex, int rowIndex, boolean isSelected, TextUiGraphics textUiGraphics) {

    }

    /**
     * Turns a cell into a multiline string, as an array.
     * @param cell Cell to turn into string content
     * @return The cell content turned into a multiline string, where each element in the array is one row.
     */
    protected String[] getContent(V cell) {
        String[] lines;
        if(cell == null) {
            lines = new String[] { "" };
        }
        else {
            lines = cell.toString().split("\r?\n");
        }
        return lines;
    }
}

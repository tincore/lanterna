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
package com.googlecode.lanterna;

/**
 * Terminal dimensions in 2-d space, measured in number of rows and columns. This class is immutable and cannot change
 * its internal state after creation.
 *
 * @author Martin
 */
public class Dimension {
    public static final Dimension ZERO = new Dimension(0, 0);
    public static final Dimension ONE = new Dimension(1, 1);

    private final int columns;
    private final int rows;

    /**
     * Creates a new terminal size representation with a given width (columns) and height (rows)
     *
     * @param columns Width, in number of columns
     * @param rows    Height, in number of columns
     */
    public Dimension(int columns, int rows) {
        if (columns < 0 || rows < 0) {
            throw new IllegalArgumentException("TerminalSize dimensions cannot be less than 0: [columns: " + columns + ", rows: " + rows + "]");
        }

        this.columns = columns;
        this.rows = rows;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Dimension)) {
            return false;
        }

        Dimension other = (Dimension) obj;
        return columns == other.columns
            && rows == other.rows;
    }

    /**
     * @return Returns the width of this size representation, in number of columns
     */
    public int getColumns() {
        return columns;
    }

    /**
     * @return Returns the height of this size representation, in number of rows
     */
    public int getRows() {
        return rows;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + this.columns;
        hash = 53 * hash + this.rows;
        return hash;
    }

    /**
     * Takes a different TerminalSize and returns a new TerminalSize that has the largest dimensions of the two,
     * measured separately. So calling 3x5 on a 5x3 will return 5x5.
     *
     * @param other Other TerminalSize to compare with
     * @return TerminalSize that combines the maximum width between the two and the maximum height
     */
    public Dimension max(Dimension other) {
        return withColumns(Math.max(columns, other.columns))
            .withRows(Math.max(rows, other.rows));
    }

    /**
     * Takes a different TerminalSize and returns a new TerminalSize that has the smallest dimensions of the two,
     * measured separately. So calling 3x5 on a 5x3 will return 3x3.
     *
     * @param other Other TerminalSize to compare with
     * @return TerminalSize that combines the minimum width between the two and the minimum height
     */
    public Dimension min(Dimension other) {
        return withColumns(Math.min(columns, other.columns))
            .withRows(Math.min(rows, other.rows));
    }

    @Override
    public String toString() {
        return "{" + columns + "x" + rows + "}";
    }

    /**
     * Returns itself if it is equal to the supplied size, otherwise the supplied size. You can use this if you have a
     * size field which is frequently recalculated but often resolves to the same size; it will keep the same object
     * in memory instead of swapping it out every cycle.
     *
     * @param size Size you want to return
     * @return Itself if this size equals the size passed in, otherwise the size passed in
     */
    public Dimension with(Dimension size) {
        if (equals(size)) {
            return this;
        }
        return size;
    }

    /**
     * Creates a new size based on this size, but with a different width
     *
     * @param columns Width of the new size, in columns
     * @return New size based on this one, but with a new width
     */
    public Dimension withColumns(int columns) {
        if (this.columns == columns) {
            return this;
        }
        if (columns == 0 && this.rows == 0) {
            return ZERO;
        }
        return new Dimension(columns, this.rows);
    }

    /**
     * Creates a new TerminalSize object representing a size based on this object's size but with a delta applied.
     * This is the same as calling
     * <code>withRelativeColumns(delta.getColumns()).withRelativeRows(delta.getRows())</code>
     *
     * @param delta Column and row offset
     * @return New terminal size based off this one but with an applied resize
     */
    public Dimension withRelative(Dimension delta) {
        return withRelative(delta.getColumns(), delta.getRows());
    }

    /**
     * Creates a new TerminalSize object representing a size based on this object's size but with a delta applied.
     * This is the same as calling
     * <code>withRelativeColumns(deltaColumns).withRelativeRows(deltaRows)</code>
     *
     * @param deltaColumns How many extra columns the new TerminalSize will have (negative values are allowed)
     * @param deltaRows    How many extra rows the new TerminalSize will have (negative values are allowed)
     * @return New terminal size based off this one but with an applied resize
     */
    public Dimension withRelative(int deltaColumns, int deltaRows) {
        return withRelativeRows(deltaRows).withRelativeColumns(deltaColumns);
    }

    /**
     * Creates a new TerminalSize object representing a size with the same number of rows, but with a column size offset by a
     * supplied value. Calling this method with delta 0 will return this, calling it with a positive delta will return
     * a terminal size <i>delta</i> number of columns wider and for negative numbers shorter.
     *
     * @param delta Column offset
     * @return New terminal size based off this one but with an applied transformation
     */
    public Dimension withRelativeColumns(int delta) {
        if (delta == 0) {
            return this;
        }
        // Prevent going below 0 (which would throw an exception)
        return withColumns(Math.max(0, columns + delta));
    }

    /**
     * Creates a new TerminalSize object representing a size with the same number of columns, but with a row size offset by a
     * supplied value. Calling this method with delta 0 will return this, calling it with a positive delta will return
     * a terminal size <i>delta</i> number of rows longer and for negative numbers shorter.
     *
     * @param delta Row offset
     * @return New terminal size based off this one but with an applied transformation
     */
    public Dimension withRelativeRows(int delta) {
        if (delta == 0) {
            return this;
        }
        // Prevent going below 0 (which would throw an exception)
        return withRows(Math.max(0, rows + delta));
    }

    /**
     * Creates a new size based on this size, but with a different height
     *
     * @param rows Height of the new size, in rows
     * @return New size based on this one, but with a new height
     */
    public Dimension withRows(int rows) {
        if (this.rows == rows) {
            return this;
        }
        if (rows == 0 && this.columns == 0) {
            return ZERO;
        }
        return new Dimension(this.columns, rows);
    }
}

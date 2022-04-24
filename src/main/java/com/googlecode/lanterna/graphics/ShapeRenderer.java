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
package com.googlecode.lanterna.graphics;

import com.googlecode.lanterna.Dimension;
import com.googlecode.lanterna.Point;
import com.googlecode.lanterna.TextCharacter;

/**
 * This package private interface exposes methods for translating abstract lines, triangles and rectangles to discreet
 * points on a grid.
 * @author Martin
 */
interface ShapeRenderer {
    void drawLine(Point p1, Point p2, TextCharacter character);
    void drawTriangle(Point p1, Point p2, Point p3, TextCharacter character);
    void drawRectangle(Point topLeft, Dimension size, TextCharacter character);
    void fillTriangle(Point p1, Point p2, Point p3, TextCharacter character);
    void fillRectangle(Point topLeft, Dimension size, TextCharacter character);
}

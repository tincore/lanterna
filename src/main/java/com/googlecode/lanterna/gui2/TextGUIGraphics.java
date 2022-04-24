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

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.*;
import com.googlecode.lanterna.screen.TabBehaviour;

import java.util.Collection;
import java.util.EnumSet;

/**
 * TextGraphics implementation used by TextGUI when doing any drawing operation.
 * @author Martin
 */
public interface TextGUIGraphics extends ThemedTextGraphics, TextGraphics {
    /**
     * Returns the {@code TextGUI} this {@code TextGUIGraphics} belongs to
     * @return {@code TextGUI} this {@code TextGUIGraphics} belongs to
     */
    Frame getTextGUI();

    @Override
    TextGUIGraphics newTextGraphics(Point topLeftCorner, Dimension size) throws IllegalArgumentException;

    @Override
    TextGUIGraphics applyThemeStyle(ThemeStyle themeStyle);

    @Override
    TextGUIGraphics setBackgroundColor(TextColor backgroundColor);

    @Override
    TextGUIGraphics setForegroundColor(TextColor foregroundColor);

    @Override
    TextGUIGraphics enableModifiers(SGR... modifiers);

    @Override
    TextGUIGraphics disableModifiers(SGR... modifiers);

    @Override
    TextGUIGraphics setModifiers(EnumSet<SGR> modifiers);

    @Override
    TextGUIGraphics clearModifiers();

    @Override
    TextGUIGraphics setTabBehaviour(TabBehaviour tabBehaviour);

    @Override
    TextGUIGraphics fill(char c);

    @Override
    TextGUIGraphics fillRectangle(Point topLeft, Dimension size, char character);

    @Override
    TextGUIGraphics fillRectangle(Point topLeft, Dimension size, TextCharacter character);

    @Override
    TextGUIGraphics drawRectangle(Point topLeft, Dimension size, char character);

    @Override
    TextGUIGraphics drawRectangle(Point topLeft, Dimension size, TextCharacter character);

    @Override
    TextGUIGraphics fillTriangle(Point p1, Point p2, Point p3, char character);

    @Override
    TextGUIGraphics fillTriangle(Point p1, Point p2, Point p3, TextCharacter character);

    @Override
    TextGUIGraphics drawTriangle(Point p1, Point p2, Point p3, char character);

    @Override
    TextGUIGraphics drawTriangle(Point p1, Point p2, Point p3, TextCharacter character);

    @Override
    TextGUIGraphics drawLine(Point fromPoint, Point toPoint, char character);

    @Override
    TextGUIGraphics drawLine(Point fromPoint, Point toPoint, TextCharacter character);

    @Override
    TextGUIGraphics drawLine(int fromX, int fromY, int toX, int toY, char character);

    @Override
    TextGUIGraphics drawLine(int fromX, int fromY, int toX, int toY, TextCharacter character);

    @Override
    TextGUIGraphics drawImage(Point topLeft, TextImage image);

    @Override
    TextGUIGraphics drawImage(Point topLeft, TextImage image, Point sourceImageTopLeft, Dimension sourceImageSize);

    @Override
    TextGUIGraphics setCharacter(Point point, char character);

    @Override
    TextGUIGraphics setCharacter(Point point, TextCharacter character);

    @Override
    TextGUIGraphics setCharacter(int column, int row, char character);

    @Override
    TextGUIGraphics setCharacter(int column, int row, TextCharacter character);

    @Override
    TextGUIGraphics putString(int column, int row, String string);

    @Override
    TextGUIGraphics putString(Point point, String string);

    @Override
    TextGUIGraphics putString(int column, int row, String string, SGR extraModifier, SGR... optionalExtraModifiers);

    @Override
    TextGUIGraphics putString(Point point, String string, SGR extraModifier, SGR... optionalExtraModifiers);

    @Override
    TextGUIGraphics putString(int column, int row, String string, Collection<SGR> extraModifiers);

    @Override
    TextGUIGraphics putCSIStyledString(int column, int row, String string);

    @Override
    TextGUIGraphics putCSIStyledString(Point point, String string);

    @Override
    TextGUIGraphics setStyleFrom(StyleSet<?> source);

}

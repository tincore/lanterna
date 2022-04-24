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
public interface TextUiGraphics extends ThemedTextGraphics, TextGraphics {
    /**
     * Returns the {@code TextGUI} this {@code TextGUIGraphics} belongs to
     * @return {@code TextGUI} this {@code TextGUIGraphics} belongs to
     */
    Frame getTextGUI();

    @Override
    TextUiGraphics newTextGraphics(Point topLeftCorner, Dimension size) throws IllegalArgumentException;

    @Override
    TextUiGraphics applyThemeStyle(ThemeStyle themeStyle);

    @Override
    TextUiGraphics setBackgroundColor(TextColor backgroundColor);

    @Override
    TextUiGraphics setForegroundColor(TextColor foregroundColor);

    @Override
    TextUiGraphics enableModifiers(SGR... modifiers);

    @Override
    TextUiGraphics disableModifiers(SGR... modifiers);

    @Override
    TextUiGraphics setModifiers(EnumSet<SGR> modifiers);

    @Override
    TextUiGraphics clearModifiers();

    @Override
    TextUiGraphics setTabBehaviour(TabBehaviour tabBehaviour);

    @Override
    TextUiGraphics fill(char c);

    @Override
    TextUiGraphics fillRectangle(Point topLeft, Dimension size, char character);

    @Override
    TextUiGraphics fillRectangle(Point topLeft, Dimension size, TextCharacter character);

    @Override
    TextUiGraphics drawRectangle(Point topLeft, Dimension size, char character);

    @Override
    TextUiGraphics drawRectangle(Point topLeft, Dimension size, TextCharacter character);

    @Override
    TextUiGraphics fillTriangle(Point p1, Point p2, Point p3, char character);

    @Override
    TextUiGraphics fillTriangle(Point p1, Point p2, Point p3, TextCharacter character);

    @Override
    TextUiGraphics drawTriangle(Point p1, Point p2, Point p3, char character);

    @Override
    TextUiGraphics drawTriangle(Point p1, Point p2, Point p3, TextCharacter character);

    @Override
    TextUiGraphics drawLine(Point fromPoint, Point toPoint, char character);

    @Override
    TextUiGraphics drawLine(Point fromPoint, Point toPoint, TextCharacter character);

    @Override
    TextUiGraphics drawLine(int fromX, int fromY, int toX, int toY, char character);

    @Override
    TextUiGraphics drawLine(int fromX, int fromY, int toX, int toY, TextCharacter character);

    @Override
    TextUiGraphics drawImage(Point topLeft, TextImage image);

    @Override
    TextUiGraphics drawImage(Point topLeft, TextImage image, Point sourceImageTopLeft, Dimension sourceImageSize);

    @Override
    TextUiGraphics setCharacter(Point point, char character);

    @Override
    TextUiGraphics setCharacter(Point point, TextCharacter character);

    @Override
    TextUiGraphics setCharacter(int column, int row, char character);

    @Override
    TextUiGraphics setCharacter(int column, int row, TextCharacter character);

    @Override
    TextUiGraphics putString(int column, int row, String string);

    @Override
    TextUiGraphics putString(Point point, String string);

    @Override
    TextUiGraphics putString(int column, int row, String string, SGR extraModifier, SGR... optionalExtraModifiers);

    @Override
    TextUiGraphics putString(Point point, String string, SGR extraModifier, SGR... optionalExtraModifiers);

    @Override
    TextUiGraphics putString(int column, int row, String string, Collection<SGR> extraModifiers);

    @Override
    TextUiGraphics putCSIStyledString(int column, int row, String string);

    @Override
    TextUiGraphics putCSIStyledString(Point point, String string);

    @Override
    TextUiGraphics setStyleFrom(StyleSet<?> source);

}

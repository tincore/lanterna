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

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.screen.TabBehaviour;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

/**
 * TextGraphics implementation that does nothing, but has a pre-defined size
 * @author martin
 */
class NullTextGraphics implements TextGraphics {
    private final Dimension size;
    private TextColor foregroundColor;
    private TextColor backgroundColor;
    private TabBehaviour tabBehaviour;
    private final EnumSet<SGR> activeModifiers;

    /**
     * Creates a new {@code NullTextGraphics} that will return the specified size value if asked how big it is but other
     * than that ignore all other calls.
     * @param size The size to report
     */
    public NullTextGraphics(Dimension size) {
        this.size = size;
        this.foregroundColor = TextColor.ANSI.DEFAULT;
        this.backgroundColor = TextColor.ANSI.DEFAULT;
        this.tabBehaviour = TabBehaviour.ALIGN_TO_COLUMN_4;
        this.activeModifiers = EnumSet.noneOf(SGR.class);
    }

    @Override
    public Dimension getSize() {
        return size;
    }

    @Override
    public TextGraphics newTextGraphics(Point topLeftCorner, Dimension size) throws IllegalArgumentException {
        return this;
    }

    @Override
    public TextColor getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public TextGraphics setBackgroundColor(TextColor backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    @Override
    public TextColor getForegroundColor() {
        return foregroundColor;
    }

    @Override
    public TextGraphics setForegroundColor(TextColor foregroundColor) {
        this.foregroundColor = foregroundColor;
        return this;
    }

    @Override
    public TextGraphics enableModifiers(SGR... modifiers) {
        activeModifiers.addAll(Arrays.asList(modifiers));
        return this;
    }

    @Override
    public TextGraphics disableModifiers(SGR... modifiers) {
        activeModifiers.removeAll(Arrays.asList(modifiers));
        return this;
    }

    @Override
    public TextGraphics setModifiers(EnumSet<SGR> modifiers) {
        clearModifiers();
        activeModifiers.addAll(modifiers);
        return this;
    }

    @Override
    public TextGraphics clearModifiers() {
        activeModifiers.clear();
        return this;
    }

    @Override
    public EnumSet<SGR> getActiveModifiers() {
        return EnumSet.copyOf(activeModifiers);
    }

    @Override
    public TabBehaviour getTabBehaviour() {
        return tabBehaviour;
    }

    @Override
    public TextGraphics setTabBehaviour(TabBehaviour tabBehaviour) {
        this.tabBehaviour = tabBehaviour;
        return this;
    }

    @Override
    public TextGraphics fill(char c) {
        return this;
    }

    @Override
    public TextGraphics setCharacter(int column, int row, char character) {
        return this;
    }

    @Override
    public TextGraphics setCharacter(int column, int row, TextCharacter character) {
        return this;
    }

    @Override
    public TextGraphics setCharacter(Point point, char character) {
        return this;
    }

    @Override
    public TextGraphics setCharacter(Point point, TextCharacter character) {
        return this;
    }

    @Override
    public TextGraphics drawLine(Point fromPoint, Point toPoint, char character) {
        return this;
    }

    @Override
    public TextGraphics drawLine(Point fromPoint, Point toPoint, TextCharacter character) {
        return this;
    }

    @Override
    public TextGraphics drawLine(int fromX, int fromY, int toX, int toY, char character) {
        return this;
    }

    @Override
    public TextGraphics drawLine(int fromX, int fromY, int toX, int toY, TextCharacter character) {
        return this;
    }

    @Override
    public TextGraphics drawTriangle(Point p1, Point p2, Point p3, char character) {
        return this;
    }

    @Override
    public TextGraphics drawTriangle(Point p1, Point p2, Point p3, TextCharacter character) {
        return this;
    }

    @Override
    public TextGraphics fillTriangle(Point p1, Point p2, Point p3, char character) {
        return this;
    }

    @Override
    public TextGraphics fillTriangle(Point p1, Point p2, Point p3, TextCharacter character) {
        return this;
    }

    @Override
    public TextGraphics drawRectangle(Point topLeft, Dimension size, char character) {
        return this;
    }

    @Override
    public TextGraphics drawRectangle(Point topLeft, Dimension size, TextCharacter character) {
        return this;
    }

    @Override
    public TextGraphics fillRectangle(Point topLeft, Dimension size, char character) {
        return this;
    }

    @Override
    public TextGraphics fillRectangle(Point topLeft, Dimension size, TextCharacter character) {
        return this;
    }

    @Override
    public TextGraphics drawImage(Point topLeft, TextImage image) {
        return this;
    }

    @Override
    public TextGraphics drawImage(Point topLeft, TextImage image, Point sourceImageTopLeft, Dimension sourceImageSize) {
        return this;
    }

    @Override
    public TextGraphics putString(int column, int row, String string) {
        return this;
    }

    @Override
    public TextGraphics putString(Point point, String string) {
        return this;
    }

    @Override
    public TextGraphics putString(int column, int row, String string, SGR extraModifier, SGR... optionalExtraModifiers) {
        return this;
    }

    @Override
    public TextGraphics putString(Point point, String string, SGR extraModifier, SGR... optionalExtraModifiers) {
        return this;
    }

    @Override
    public TextGraphics putString(int column, int row, String string, Collection<SGR> extraModifiers) {
        return this;
    }

    @Override
    public TextGraphics putCSIStyledString(int column, int row, String string) {
        return this;
    }

    @Override
    public TextGraphics putCSIStyledString(Point point, String string) {
        return this;
    }

    @Override
    public TextCharacter getCharacter(int column, int row) {
        return null;
    }

    @Override
    public TextCharacter getCharacter(Point point) {
        return null;
    }

    @Override
    public TextGraphics setStyleFrom(StyleSet<?> source) {
        setBackgroundColor(source.getBackgroundColor());
        setForegroundColor(source.getForegroundColor());
        setModifiers(source.getActiveModifiers());
        return this;
    }

}

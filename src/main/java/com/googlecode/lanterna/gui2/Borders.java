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
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.graphics.ThemeDefinition;

import java.util.Arrays;
import java.util.List;

/**
 * This class containers a couple of border implementation and utility methods for instantiating them. It also contains
 * a utility method for joining border line graphics together with adjacent lines so they blend in together:
 * {@code joinLinesWithFrame(..)}.
 *
 * @author Martin
 */
public class Borders {
    private Borders() {
    }

    /**
     * Creates a {@code Border} that is drawn as a solid color double line surrounding the wrapped component
     *
     * @return New solid color double line {@code Border}
     */
    public static Border doubleLine() {
        return doubleLine("");
    }

    /**
     * Creates a {@code Border} that is drawn as a solid color double line surrounding the wrapped component with a
     * title string normally drawn at the top-left side
     *
     * @param title The title to draw on the border
     * @return New solid color double line {@code Border} with a title
     */
    public static Border doubleLine(String title) {
        return doubleLine(title, Attributes.EMPTY);
    }

    public static Border doubleLine(String title, Attributes attributes) {
        return new DoubleLine(title, BorderStyle.Solid, attributes);
    }

    public static Border doubleLine(Component component) {
        return doubleLine("", Attributes.EMPTY, component);
    }

    public static Border doubleLine(String title, Component component) {
        return doubleLine(title, Attributes.EMPTY, component);
    }

    public static Border doubleLine(String title, Attributes attributes, Component component) {
        return doubleLine(title, attributes).setComponent(component);
    }

    /**
     * Creates a {@code Border} that is drawn as a bevel color double line surrounding the wrapped component
     *
     * @return New bevel color double line {@code Border}
     */
    public static Border doubleLineBevel() {
        return doubleLineBevel("");
    }

    /**
     * Creates a {@code Border} that is drawn as a bevel color double line surrounding the wrapped component with a
     * title string normally drawn at the top-left side
     *
     * @param title The title to draw on the border
     * @return New bevel color double line {@code Border} with a title
     */
    public static Border doubleLineBevel(String title) {
        return doubleLineBevel(title, Attributes.EMPTY);
    }

    public static Border doubleLineBevel(String title, Attributes attributes) {
        return new DoubleLine(title, BorderStyle.Bevel, attributes);
    }

    public static Border doubleLineBevel(Component component) {
        return doubleLineBevel("", Attributes.EMPTY, component);
    }

    public static Border doubleLineBevel(String title, Component component) {
        return doubleLineBevel(title, Attributes.EMPTY, component);
    }

    public static Border doubleLineBevel(String title, Attributes attributes, Component component) {
        return doubleLineBevel(title, attributes).setComponent(component);
    }

    /**
     * Creates a {@code Border} that is drawn as a reverse bevel color double line surrounding the wrapped component
     *
     * @return New reverse bevel color double line {@code Border}
     */
    public static Border doubleLineReverseBevel() {
        return doubleLineReverseBevel("");
    }

    /**
     * Creates a {@code Border} that is drawn as a reverse bevel color double line surrounding the wrapped component
     * with a title string normally drawn at the top-left side
     *
     * @param title The title to draw on the border
     * @return New reverse bevel color double line {@code Border} with a title
     */
    public static Border doubleLineReverseBevel(String title) {
        return doubleLineReverseBevel(title, Attributes.EMPTY);
    }

    public static Border doubleLineReverseBevel(String title, Attributes attributes) {
        return new DoubleLine(title, BorderStyle.ReverseBevel, attributes);
    }

    public static Border doubleLineReverseBevel(Component component) {
        return doubleLineReverseBevel("", Attributes.EMPTY, component);
    }

    public static Border doubleLineReverseBevel(String title, Component component) {
        return doubleLineReverseBevel(title, Attributes.EMPTY, component);
    }

    public static Border doubleLineReverseBevel(String title, Attributes attributes, Component component) {
        return doubleLineReverseBevel(title, attributes).setComponent(component);
    }


    /**
     * This method will attempt to join line drawing characters with the outermost bottom and top rows and left and
     * right columns. For example, if a vertical left border character is ║ and the character immediately to the right
     * of it is ─, then the border character will be updated to ╟ to join the two together. Please note that this method
     * will <b>only</b> join the outer border columns and rows.
     *
     * @param graphics Graphics to use when inspecting and joining characters
     */
    public static void joinLinesWithFrame(TextGraphics graphics) {
        Dimension drawableArea = graphics.getSize();
        if (drawableArea.getRows() <= 2 || drawableArea.getColumns() <= 2) {
            //Too small
            return;
        }

        int upperRow = 0;
        int lowerRow = drawableArea.getRows() - 1;
        int leftRow = 0;
        int rightRow = drawableArea.getColumns() - 1;

        List<Character> junctionFromBelowSingle = Arrays.asList(
            Symbols.SINGLE_LINE_VERTICAL,
            Symbols.BOLD_FROM_NORMAL_SINGLE_LINE_VERTICAL,
            Symbols.BOLD_SINGLE_LINE_VERTICAL,
            Symbols.SINGLE_LINE_CROSS,
            Symbols.DOUBLE_LINE_HORIZONTAL_SINGLE_LINE_CROSS,
            Symbols.SINGLE_LINE_BOTTOM_LEFT_CORNER,
            Symbols.SINGLE_LINE_BOTTOM_RIGHT_CORNER,
            Symbols.SINGLE_LINE_T_LEFT,
            Symbols.SINGLE_LINE_T_RIGHT,
            Symbols.SINGLE_LINE_T_UP,
            Symbols.SINGLE_LINE_T_DOUBLE_LEFT,
            Symbols.SINGLE_LINE_T_DOUBLE_RIGHT,
            Symbols.DOUBLE_LINE_T_SINGLE_UP);
        List<Character> junctionFromBelowDouble = Arrays.asList(
            Symbols.DOUBLE_LINE_VERTICAL,
            Symbols.DOUBLE_LINE_CROSS,
            Symbols.DOUBLE_LINE_VERTICAL_SINGLE_LINE_CROSS,
            Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER,
            Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER,
            Symbols.DOUBLE_LINE_T_LEFT,
            Symbols.DOUBLE_LINE_T_RIGHT,
            Symbols.DOUBLE_LINE_T_UP,
            Symbols.DOUBLE_LINE_T_SINGLE_LEFT,
            Symbols.DOUBLE_LINE_T_SINGLE_RIGHT,
            Symbols.SINGLE_LINE_T_DOUBLE_UP);
        List<Character> junctionFromAboveSingle = Arrays.asList(
            Symbols.SINGLE_LINE_VERTICAL,
            Symbols.BOLD_TO_NORMAL_SINGLE_LINE_VERTICAL,
            Symbols.BOLD_SINGLE_LINE_VERTICAL,
            Symbols.SINGLE_LINE_CROSS,
            Symbols.DOUBLE_LINE_HORIZONTAL_SINGLE_LINE_CROSS,
            Symbols.SINGLE_LINE_TOP_LEFT_CORNER,
            Symbols.SINGLE_LINE_TOP_RIGHT_CORNER,
            Symbols.SINGLE_LINE_T_LEFT,
            Symbols.SINGLE_LINE_T_RIGHT,
            Symbols.SINGLE_LINE_T_DOWN,
            Symbols.SINGLE_LINE_T_DOUBLE_LEFT,
            Symbols.SINGLE_LINE_T_DOUBLE_RIGHT,
            Symbols.DOUBLE_LINE_T_SINGLE_DOWN);
        List<Character> junctionFromAboveDouble = Arrays.asList(
            Symbols.DOUBLE_LINE_VERTICAL,
            Symbols.DOUBLE_LINE_CROSS,
            Symbols.DOUBLE_LINE_VERTICAL_SINGLE_LINE_CROSS,
            Symbols.DOUBLE_LINE_TOP_LEFT_CORNER,
            Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER,
            Symbols.DOUBLE_LINE_T_LEFT,
            Symbols.DOUBLE_LINE_T_RIGHT,
            Symbols.DOUBLE_LINE_T_DOWN,
            Symbols.DOUBLE_LINE_T_SINGLE_LEFT,
            Symbols.DOUBLE_LINE_T_SINGLE_RIGHT,
            Symbols.SINGLE_LINE_T_DOUBLE_DOWN);
        List<Character> junctionFromLeftSingle = Arrays.asList(
            Symbols.SINGLE_LINE_HORIZONTAL,
            Symbols.BOLD_TO_NORMAL_SINGLE_LINE_HORIZONTAL,
            Symbols.BOLD_SINGLE_LINE_HORIZONTAL,
            Symbols.SINGLE_LINE_CROSS,
            Symbols.DOUBLE_LINE_VERTICAL_SINGLE_LINE_CROSS,
            Symbols.SINGLE_LINE_BOTTOM_LEFT_CORNER,
            Symbols.SINGLE_LINE_TOP_LEFT_CORNER,
            Symbols.SINGLE_LINE_T_UP,
            Symbols.SINGLE_LINE_T_DOWN,
            Symbols.SINGLE_LINE_T_RIGHT,
            Symbols.SINGLE_LINE_T_DOUBLE_UP,
            Symbols.SINGLE_LINE_T_DOUBLE_DOWN,
            Symbols.DOUBLE_LINE_T_SINGLE_RIGHT);
        List<Character> junctionFromLeftDouble = Arrays.asList(
            Symbols.DOUBLE_LINE_HORIZONTAL,
            Symbols.DOUBLE_LINE_CROSS,
            Symbols.DOUBLE_LINE_HORIZONTAL_SINGLE_LINE_CROSS,
            Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER,
            Symbols.DOUBLE_LINE_TOP_LEFT_CORNER,
            Symbols.DOUBLE_LINE_T_UP,
            Symbols.DOUBLE_LINE_T_DOWN,
            Symbols.DOUBLE_LINE_T_RIGHT,
            Symbols.DOUBLE_LINE_T_SINGLE_UP,
            Symbols.DOUBLE_LINE_T_SINGLE_DOWN,
            Symbols.SINGLE_LINE_T_DOUBLE_RIGHT);
        List<Character> junctionFromRightSingle = Arrays.asList(
            Symbols.SINGLE_LINE_HORIZONTAL,
            Symbols.BOLD_FROM_NORMAL_SINGLE_LINE_HORIZONTAL,
            Symbols.BOLD_SINGLE_LINE_HORIZONTAL,
            Symbols.SINGLE_LINE_CROSS,
            Symbols.DOUBLE_LINE_VERTICAL_SINGLE_LINE_CROSS,
            Symbols.SINGLE_LINE_BOTTOM_RIGHT_CORNER,
            Symbols.SINGLE_LINE_TOP_RIGHT_CORNER,
            Symbols.SINGLE_LINE_T_UP,
            Symbols.SINGLE_LINE_T_DOWN,
            Symbols.SINGLE_LINE_T_LEFT,
            Symbols.SINGLE_LINE_T_DOUBLE_UP,
            Symbols.SINGLE_LINE_T_DOUBLE_DOWN,
            Symbols.DOUBLE_LINE_T_SINGLE_LEFT);
        List<Character> junctionFromRightDouble = Arrays.asList(
            Symbols.DOUBLE_LINE_HORIZONTAL,
            Symbols.DOUBLE_LINE_CROSS,
            Symbols.DOUBLE_LINE_HORIZONTAL_SINGLE_LINE_CROSS,
            Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER,
            Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER,
            Symbols.DOUBLE_LINE_T_UP,
            Symbols.DOUBLE_LINE_T_DOWN,
            Symbols.DOUBLE_LINE_T_LEFT,
            Symbols.DOUBLE_LINE_T_SINGLE_UP,
            Symbols.DOUBLE_LINE_T_SINGLE_DOWN,
            Symbols.SINGLE_LINE_T_DOUBLE_LEFT);

        //Go horizontally and check vertical neighbours if it's possible to extend lines into the border
        for (int column = 1; column < drawableArea.getColumns() - 1; column++) {
            //Check first row
            TextCharacter borderCharacter = graphics.getCharacter(column, upperRow);
            if (borderCharacter == null) {
                continue;
            }
            TextCharacter neighbourCharacter = graphics.getCharacter(column, upperRow + 1);
            if (neighbourCharacter != null) {
                char neighbour = neighbourCharacter.getCharacterString().charAt(0);
                if (borderCharacter.is(Symbols.SINGLE_LINE_HORIZONTAL)) {
                    if (junctionFromBelowSingle.contains(neighbour)) {
                        graphics.setCharacter(column, upperRow, borderCharacter.withCharacter(Symbols.SINGLE_LINE_T_DOWN));
                    } else if (junctionFromBelowDouble.contains(neighbour)) {
                        graphics.setCharacter(column, upperRow, borderCharacter.withCharacter(Symbols.SINGLE_LINE_T_DOUBLE_DOWN));
                    }
                } else if (borderCharacter.is(Symbols.DOUBLE_LINE_HORIZONTAL)) {
                    if (junctionFromBelowSingle.contains(neighbour)) {
                        graphics.setCharacter(column, upperRow, borderCharacter.withCharacter(Symbols.DOUBLE_LINE_T_SINGLE_DOWN));
                    } else if (junctionFromBelowDouble.contains(neighbour)) {
                        graphics.setCharacter(column, upperRow, borderCharacter.withCharacter(Symbols.DOUBLE_LINE_T_DOWN));
                    }
                }
            }

            //Check last row
            borderCharacter = graphics.getCharacter(column, lowerRow);
            if (borderCharacter == null) {
                continue;
            }
            neighbourCharacter = graphics.getCharacter(column, lowerRow - 1);
            if (neighbourCharacter != null) {
                char neighbour = neighbourCharacter.getCharacterString().charAt(0);
                if (borderCharacter.is(Symbols.SINGLE_LINE_HORIZONTAL)) {
                    if (junctionFromAboveSingle.contains(neighbour)) {
                        graphics.setCharacter(column, lowerRow, borderCharacter.withCharacter(Symbols.SINGLE_LINE_T_UP));
                    } else if (junctionFromAboveDouble.contains(neighbour)) {
                        graphics.setCharacter(column, lowerRow, borderCharacter.withCharacter(Symbols.SINGLE_LINE_T_DOUBLE_UP));
                    }
                } else if (borderCharacter.is(Symbols.DOUBLE_LINE_HORIZONTAL)) {
                    if (junctionFromAboveSingle.contains(neighbour)) {
                        graphics.setCharacter(column, lowerRow, borderCharacter.withCharacter(Symbols.DOUBLE_LINE_T_SINGLE_UP));
                    } else if (junctionFromAboveDouble.contains(neighbour)) {
                        graphics.setCharacter(column, lowerRow, borderCharacter.withCharacter(Symbols.DOUBLE_LINE_T_UP));
                    }
                }
            }
        }

        //Go vertically and check horizontal neighbours if it's possible to extend lines into the border
        for (int row = 1; row < drawableArea.getRows() - 1; row++) {
            //Check first column
            TextCharacter borderCharacter = graphics.getCharacter(leftRow, row);
            if (borderCharacter == null) {
                continue;
            }
            TextCharacter neighbourCharacter = graphics.getCharacter(leftRow + 1, row);
            if (neighbourCharacter != null) {
                char neighbour = neighbourCharacter.getCharacterString().charAt(0);
                if (borderCharacter.is(Symbols.SINGLE_LINE_VERTICAL)) {
                    if (junctionFromRightSingle.contains(neighbour)) {
                        graphics.setCharacter(leftRow, row, borderCharacter.withCharacter(Symbols.SINGLE_LINE_T_RIGHT));
                    } else if (junctionFromRightDouble.contains(neighbour)) {
                        graphics.setCharacter(leftRow, row, borderCharacter.withCharacter(Symbols.SINGLE_LINE_T_DOUBLE_RIGHT));
                    }
                } else if (borderCharacter.is(Symbols.DOUBLE_LINE_VERTICAL)) {
                    if (junctionFromRightSingle.contains(neighbour)) {
                        graphics.setCharacter(leftRow, row, borderCharacter.withCharacter(Symbols.DOUBLE_LINE_T_SINGLE_RIGHT));
                    } else if (junctionFromRightDouble.contains(neighbour)) {
                        graphics.setCharacter(leftRow, row, borderCharacter.withCharacter(Symbols.DOUBLE_LINE_T_RIGHT));
                    }
                }
            }

            //Check last column
            borderCharacter = graphics.getCharacter(rightRow, row);
            if (borderCharacter == null) {
                continue;
            }
            neighbourCharacter = graphics.getCharacter(rightRow - 1, row);
            if (neighbourCharacter != null) {
                char neighbour = neighbourCharacter.getCharacterString().charAt(0);
                if (borderCharacter.is(Symbols.SINGLE_LINE_VERTICAL)) {
                    if (junctionFromLeftSingle.contains(neighbour)) {
                        graphics.setCharacter(rightRow, row, borderCharacter.withCharacter(Symbols.SINGLE_LINE_T_LEFT));
                    } else if (junctionFromLeftDouble.contains(neighbour)) {
                        graphics.setCharacter(rightRow, row, borderCharacter.withCharacter(Symbols.SINGLE_LINE_T_DOUBLE_LEFT));
                    }
                } else if (borderCharacter.is(Symbols.DOUBLE_LINE_VERTICAL)) {
                    if (junctionFromLeftSingle.contains(neighbour)) {
                        graphics.setCharacter(rightRow, row, borderCharacter.withCharacter(Symbols.DOUBLE_LINE_T_SINGLE_LEFT));
                    } else if (junctionFromLeftDouble.contains(neighbour)) {
                        graphics.setCharacter(rightRow, row, borderCharacter.withCharacter(Symbols.DOUBLE_LINE_T_LEFT));
                    }
                }
            }
        }
    }

    /**
     * Creates a {@code Border} that is drawn as a solid color single line surrounding the wrapped component
     *
     * @return New solid color single line {@code Border}
     */
    public static Border singleLine() {
        return singleLine("");
    }

    /**
     * Creates a {@code Border} that is drawn as a solid color single line surrounding the wrapped component with a
     * title string normally drawn at the top-left side
     *
     * @param title The title to draw on the border
     * @return New solid color single line {@code Border} with a title
     */
    public static Border singleLine(String title) {
        return singleLine(title, Attributes.EMPTY);
    }

    public static Border singleLine(String title, Attributes attributes) {
        return new SingleLine(title, BorderStyle.Solid, attributes);
    }

    public static Border singleLine(Component component) {
        return singleLine("", Attributes.EMPTY, component);
    }

    public static Border singleLine(String title, Component component) {
        return singleLine(title, Attributes.EMPTY, component);
    }

    public static Border singleLine(String title, Attributes attributes, Component component) {
        return singleLine(title, attributes).setComponent(component);
    }

    /**
     * Creates a {@code Border} that is drawn as a bevel color single line surrounding the wrapped component
     *
     * @return New bevel color single line {@code Border}
     */
    public static Border singleLineBevel() {
        return singleLineBevel("");
    }

    /**
     * Creates a {@code Border} that is drawn as a bevel color single line surrounding the wrapped component with a
     * title string normally drawn at the top-left side
     *
     * @param title The title to draw on the border
     * @return New bevel color single line {@code Border} with a title
     */
    public static Border singleLineBevel(String title) {
        return singleLineBevel(title, Attributes.EMPTY);
    }

    public static Border singleLineBevel(String title, Attributes attributes) {
        return new SingleLine(title, BorderStyle.Bevel, attributes);
    }

    public static Border singleLineBevel(Component component) {
        return singleLineBevel("", Attributes.EMPTY, component);
    }

    public static Border singleLineBevel(String title, Component component) {
        return singleLineBevel(title, Attributes.EMPTY, component);
    }

    public static Border singleLineBevel(String title, Attributes attributes, Component component) {
        return singleLineBevel(title, attributes).setComponent(component);
    }

    /**
     * Creates a {@code Border} that is drawn as a reverse bevel color single line surrounding the wrapped component
     *
     * @return New reverse bevel color single line {@code Border}
     */
    public static Border singleLineReverseBevel() {
        return singleLineReverseBevel("");
    }

    /**
     * Creates a {@code Border} that is drawn as a reverse bevel color single line surrounding the wrapped component
     * with a title string normally drawn at the top-left side
     *
     * @param title The title to draw on the border
     * @return New reverse bevel color single line {@code Border} with a title
     */
    public static Border singleLineReverseBevel(String title) {
        return singleLineReverseBevel(title, Attributes.EMPTY);
    }

    public static Border singleLineReverseBevel(String title, Attributes attributes) {
        return new SingleLine(title, BorderStyle.ReverseBevel, attributes);
    }

    public static Border singleLineReverseBevel(Component component) {
        return singleLineReverseBevel("", Attributes.EMPTY, component);
    }

    public static Border singleLineReverseBevel(String title, Component component) {
        return singleLineReverseBevel(title, Attributes.EMPTY, component);
    }

    public static Border singleLineReverseBevel(String title, Attributes attributes, Component component) {
        return singleLineReverseBevel(title, attributes).setComponent(component);
    }

    //Different ways to draw the border
    private enum BorderStyle {
        Solid,
        Bevel,
        ReverseBevel,
    }

    private static abstract class StandardBorder extends AbstractBorder {
        protected final BorderStyle borderStyle;
        private final String title;

        protected StandardBorder(String title, BorderStyle borderStyle, Attributes attributes) {
            super(attributes);
            if (title == null) {
                throw new IllegalArgumentException("Cannot create a border with null title");
            }
            this.borderStyle = borderStyle;
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" + title + "}";
        }
    }

    private static abstract class AbstractBorderRenderer implements Border.BorderRenderer {
        private final BorderStyle borderStyle;

        protected AbstractBorderRenderer(BorderStyle borderStyle) {
            this.borderStyle = borderStyle;
        }

        @Override
        public void drawComponent(TextUiGraphics graphics, Border component) {
            StandardBorder border = (StandardBorder) component;
            Component wrappedComponent = border.getComponent();
            if (wrappedComponent == null) {
                return;
            }
            Dimension drawableArea = graphics.getSize();

            char horizontalLine = getHorizontalLine(component.getTheme());
            char verticalLine = getVerticalLine(component.getTheme());
            char bottomLeftCorner = getBottomLeftCorner(component.getTheme());
            char topLeftCorner = getTopLeftCorner(component.getTheme());
            char bottomRightCorner = getBottomRightCorner(component.getTheme());
            char topRightCorner = getTopRightCorner(component.getTheme());
            char titleLeft = getTitleLeft(component.getTheme());
            char titleRight = getTitleRight(component.getTheme());

            ThemeDefinition themeDefinition = component.getTheme().getDefinition(AbstractBorder.class);
            if (borderStyle == BorderStyle.Bevel) {
                graphics.applyThemeStyle(themeDefinition.getPreLight());
            } else {
                graphics.applyThemeStyle(themeDefinition.getNormal());
            }
            graphics.setCharacter(0, drawableArea.getRows() - 1, bottomLeftCorner);
            if (drawableArea.getRows() > 2) {
                graphics.drawLine(new Point(0, drawableArea.getRows() - 2), new Point(0, 1), verticalLine);
            }
            graphics.setCharacter(0, 0, topLeftCorner);
            if (drawableArea.getColumns() > 2) {
                graphics.drawLine(new Point(1, 0), new Point(drawableArea.getColumns() - 2, 0), horizontalLine);
            }

            if (borderStyle == BorderStyle.ReverseBevel) {
                graphics.applyThemeStyle(themeDefinition.getPreLight());
            } else {
                graphics.applyThemeStyle(themeDefinition.getNormal());
            }
            graphics.setCharacter(drawableArea.getColumns() - 1, 0, topRightCorner);
            if (drawableArea.getRows() > 2) {
                graphics.drawLine(new Point(drawableArea.getColumns() - 1, 1),
                    new Point(drawableArea.getColumns() - 1, drawableArea.getRows() - 2),
                    verticalLine);
            }
            graphics.setCharacter(drawableArea.getColumns() - 1, drawableArea.getRows() - 1, bottomRightCorner);
            if (drawableArea.getColumns() > 2) {
                graphics.drawLine(new Point(1, drawableArea.getRows() - 1),
                    new Point(drawableArea.getColumns() - 2, drawableArea.getRows() - 1),
                    horizontalLine);
            }


            if (border.getTitle() != null && !border.getTitle().isEmpty() &&
                drawableArea.getColumns() >= TerminalTextUtils.getColumnWidth(border.getTitle()) + 4) {
                graphics.applyThemeStyle(themeDefinition.getActive());
                graphics.putString(2, 0, border.getTitle());

                if (borderStyle == BorderStyle.Bevel) {
                    graphics.applyThemeStyle(themeDefinition.getPreLight());
                } else {
                    graphics.applyThemeStyle(themeDefinition.getNormal());
                }
                graphics.setCharacter(1, 0, titleLeft);
                graphics.setCharacter(2 + TerminalTextUtils.getColumnWidth(border.getTitle()), 0, titleRight);
            }

            wrappedComponent.draw(graphics.newTextGraphics(getWrappedComponentTopLeftOffset(), getWrappedComponentSize(drawableArea)));
            joinLinesWithFrame(graphics);
        }

        protected abstract char getBottomLeftCorner(Theme theme);

        protected abstract char getBottomRightCorner(Theme theme);

        protected abstract char getHorizontalLine(Theme theme);

        @Override
        public Dimension getPreferredSize(Border component) {
            StandardBorder border = (StandardBorder) component;
            Component wrappedComponent = border.getComponent();
            Dimension preferredSize;
            if (wrappedComponent == null) {
                preferredSize = Dimension.ZERO;
            } else {
                preferredSize = wrappedComponent.getPreferredSize();
            }
            preferredSize = preferredSize.withRelativeColumns(2).withRelativeRows(2);
            String borderTitle = border.getTitle();
            return preferredSize.max(new Dimension((borderTitle.isEmpty() ? 2 : TerminalTextUtils.getColumnWidth(borderTitle) + 4), 2));
        }

        protected abstract char getTitleLeft(Theme theme);

        protected abstract char getTitleRight(Theme theme);

        protected abstract char getTopLeftCorner(Theme theme);

        protected abstract char getTopRightCorner(Theme theme);

        protected abstract char getVerticalLine(Theme theme);

        @Override
        public Dimension getWrappedComponentSize(Dimension borderSize) {
            return borderSize
                .withRelativeColumns(-Math.min(2, borderSize.getColumns()))
                .withRelativeRows(-Math.min(2, borderSize.getRows()));
        }

        @Override
        public Point getWrappedComponentTopLeftOffset() {
            return Point.OFFSET_1x1;
        }
    }

    private static class SingleLine extends StandardBorder {
        private SingleLine(String title, BorderStyle borderStyle, Attributes attributes) {
            super(title, borderStyle, attributes);
        }

        @Override
        protected BorderRenderer createDefaultRenderer() {
            return new SingleLineRenderer(borderStyle);
        }
    }

    private static class SingleLineRenderer extends AbstractBorderRenderer {
        public SingleLineRenderer(BorderStyle borderStyle) {
            super(borderStyle);
        }

        @Override
        protected char getBottomLeftCorner(Theme theme) {
            return theme.getDefinition(SingleLine.class).getCharacter("BOTTOM_LEFT_CORNER", Symbols.SINGLE_LINE_BOTTOM_LEFT_CORNER);
        }

        @Override
        protected char getBottomRightCorner(Theme theme) {
            return theme.getDefinition(SingleLine.class).getCharacter("BOTTOM_RIGHT_CORNER", Symbols.SINGLE_LINE_BOTTOM_RIGHT_CORNER);
        }

        @Override
        protected char getHorizontalLine(Theme theme) {
            return theme.getDefinition(SingleLine.class).getCharacter("HORIZONTAL_LINE", Symbols.SINGLE_LINE_HORIZONTAL);
        }

        @Override
        protected char getTitleLeft(Theme theme) {
            return theme.getDefinition(SingleLine.class).getCharacter("TITLE_LEFT", Symbols.SINGLE_LINE_HORIZONTAL);
        }

        @Override
        protected char getTitleRight(Theme theme) {
            return theme.getDefinition(SingleLine.class).getCharacter("TITLE_RIGHT", Symbols.SINGLE_LINE_HORIZONTAL);
        }

        @Override
        protected char getTopLeftCorner(Theme theme) {
            return theme.getDefinition(SingleLine.class).getCharacter("TOP_LEFT_CORNER", Symbols.SINGLE_LINE_TOP_LEFT_CORNER);
        }

        @Override
        protected char getTopRightCorner(Theme theme) {
            return theme.getDefinition(SingleLine.class).getCharacter("TOP_RIGHT_CORNER", Symbols.SINGLE_LINE_TOP_RIGHT_CORNER);
        }

        @Override
        protected char getVerticalLine(Theme theme) {
            return theme.getDefinition(SingleLine.class).getCharacter("VERTICAL_LINE", Symbols.SINGLE_LINE_VERTICAL);
        }
    }

    private static class DoubleLine extends StandardBorder {
        private DoubleLine(String title, BorderStyle borderStyle, Attributes attributes) {
            super(title, borderStyle, attributes);
        }

        @Override
        protected BorderRenderer createDefaultRenderer() {
            return new DoubleLineRenderer(borderStyle);
        }
    }

    private static class DoubleLineRenderer extends AbstractBorderRenderer {
        public DoubleLineRenderer(BorderStyle borderStyle) {
            super(borderStyle);
        }

        @Override
        protected char getBottomLeftCorner(Theme theme) {
            return theme.getDefinition(DoubleLine.class).getCharacter("BOTTOM_LEFT_CORNER", Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER);
        }

        @Override
        protected char getBottomRightCorner(Theme theme) {
            return theme.getDefinition(DoubleLine.class).getCharacter("BOTTOM_RIGHT_CORNER", Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER);
        }

        @Override
        protected char getHorizontalLine(Theme theme) {
            return theme.getDefinition(DoubleLine.class).getCharacter("HORIZONTAL_LINE", Symbols.DOUBLE_LINE_HORIZONTAL);
        }

        @Override
        protected char getTitleLeft(Theme theme) {
            return theme.getDefinition(DoubleLine.class).getCharacter("TITLE_LEFT", Symbols.DOUBLE_LINE_HORIZONTAL);
        }

        @Override
        protected char getTitleRight(Theme theme) {
            return theme.getDefinition(DoubleLine.class).getCharacter("TITLE_RIGHT", Symbols.DOUBLE_LINE_HORIZONTAL);
        }

        @Override
        protected char getTopLeftCorner(Theme theme) {
            return theme.getDefinition(DoubleLine.class).getCharacter("TOP_LEFT_CORNER", Symbols.DOUBLE_LINE_TOP_LEFT_CORNER);
        }

        @Override
        protected char getTopRightCorner(Theme theme) {
            return theme.getDefinition(DoubleLine.class).getCharacter("TOP_RIGHT_CORNER", Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER);
        }

        @Override
        protected char getVerticalLine(Theme theme) {
            return theme.getDefinition(DoubleLine.class).getCharacter("VERTICAL_LINE", Symbols.DOUBLE_LINE_VERTICAL);
        }
    }
}

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

import com.googlecode.lanterna.Dimension;
import com.googlecode.lanterna.Point;
import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.input.KeyStroke;

/**
 * Simple labeled button that the user can trigger by pressing the Enter or the Spacebar key on the keyboard when the
 * component is in focus.
 *
 * @author Martin
 */
public class Button extends AbstractInteractableComponent<Button> {

    private String label;
    private ClickListener clickListener = ClickListener.DUMMY;

    /**
     * Creates a new button with a specific label and no initially attached action.
     *
     * @param label Label to put on the button
     */
    public Button(String label) {
        this(label, Attributes.EMPTY);
    }

    public Button(String label, ClickListener clickListener) {
        this(label, Attributes.EMPTY);
        setClickListener(clickListener);
    }


    public Button(String label, Attributes attributes) {
        super(attributes);
        setLabel(label);
    }

    @Override
    protected ButtonRenderer createDefaultRenderer() {
        return new DefaultButtonRenderer();
    }

    public ClickListener getClickListener() {
        return clickListener;
    }

    public Button setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
        return this;
    }

    @Override
    public synchronized Point getCursorLocation() {
        return getRenderer().getCursorLocation(this);
    }

    /**
     * Returns the label current assigned to the button
     *
     * @return Label currently used by the button
     */
    public String getLabel() {
        return label;
    }

    /**
     * Updates the label on the button to the specified string
     *
     * @param label New label to use on the button
     */
    public final synchronized void setLabel(String label) {
        if (label == null) {
            throw new IllegalArgumentException("null label to a button is not allowed");
        }
        if (label.isEmpty()) {
            label = " ";
        }
        this.label = label;
        invalidate();
    }

    public void onClicked() {
        clickListener.onClicked(this);
    }

    @Override
    public KeyStrokeResult onKeyStroke(KeyStroke keyStroke) {
        if (isActivationStroke(keyStroke)) {
            onClicked();
            return KeyStrokeResult.HANDLED;
        }
        return super.onKeyStroke(keyStroke);
    }

    @Override
    public String toString() {
        return "Button{" + label + "}";
    }

    /**
     * Listener interface that can be used to catch user events on the button
     */
    public interface Listener {
        /**
         * This is called when the user has triggered the button
         *
         * @param button Button which was triggered
         */
        void onTriggered(Button button);
    }

    /**
     * Helper interface that doesn't add any new methods but makes coding new button renderers a little bit more clear
     */
    public interface ButtonRenderer extends InteractableRenderer<Button> {
    }

    /**
     * This is the default button renderer that is used if you don't override anything. With this renderer, buttons are
     * drawn on a single line, with the label inside of "&lt;" and "&gt;".
     */
    public static class DefaultButtonRenderer implements ButtonRenderer {
        @Override
        public void drawComponent(TextUiGraphics graphics, Button button) {
            ThemeDefinition themeDefinition = button.getThemeDefinition();
            if (button.isFocused()) {
                graphics.applyThemeStyle(themeDefinition.getActive());
            } else {
                graphics.applyThemeStyle(themeDefinition.getInsensitive());
            }
            graphics.fill(' ');
            graphics.setCharacter(0, 0, themeDefinition.getCharacter("LEFT_BORDER", '<'));
            graphics.setCharacter(graphics.getSize().getColumns() - 1, 0, themeDefinition.getCharacter("RIGHT_BORDER", '>'));

            if (button.isFocused()) {
                graphics.applyThemeStyle(themeDefinition.getActive());
            } else {
                graphics.applyThemeStyle(themeDefinition.getPreLight());
            }
            int labelShift = getLabelShift(button, graphics.getSize());
            graphics.setCharacter(1 + labelShift, 0, button.getLabel().charAt(0));

            if (TerminalTextUtils.getColumnWidth(button.getLabel()) == 1) {
                return;
            }
            if (button.isFocused()) {
                graphics.applyThemeStyle(themeDefinition.getSelected());
            } else {
                graphics.applyThemeStyle(themeDefinition.getNormal());
            }
            graphics.putString(1 + labelShift + 1, 0, button.getLabel().substring(1));
        }

        @Override
        public Point getCursorLocation(Button button) {
            if (button.getThemeDefinition().isCursorVisible()) {
                return new Point(1 + getLabelShift(button, button.getSize()), 0);
            } else {
                return null;
            }
        }

        private int getLabelShift(Button button, Dimension size) {
            int availableSpace = size.getColumns() - 2;
            if (availableSpace <= 0) {
                return 0;
            }
            int labelShift = 0;
            int widthInColumns = TerminalTextUtils.getColumnWidth(button.getLabel());
            if (availableSpace > widthInColumns) {
                labelShift = (size.getColumns() - 2 - widthInColumns) / 2;
            }
            return labelShift;
        }

        @Override
        public Dimension getPreferredSize(Button button) {
            return new Dimension(Math.max(8, TerminalTextUtils.getColumnWidth(button.getLabel()) + 2), 1);
        }
    }

    /**
     * Alternative button renderer that displays buttons with just the label and minimal decoration
     */
    public static class FlatButtonRenderer implements ButtonRenderer {
        @Override
        public void drawComponent(TextUiGraphics graphics, Button button) {
            ThemeDefinition themeDefinition = button.getThemeDefinition();
            if (button.isFocused()) {
                graphics.applyThemeStyle(themeDefinition.getActive());
            } else {
                graphics.applyThemeStyle(themeDefinition.getInsensitive());
            }
            graphics.fill(' ');
            if (button.isFocused()) {
                graphics.applyThemeStyle(themeDefinition.getSelected());
            } else {
                graphics.applyThemeStyle(themeDefinition.getNormal());
            }
            graphics.putString(0, 0, button.getLabel());
        }

        @Override
        public Point getCursorLocation(Button component) {
            return null;
        }

        @Override
        public Dimension getPreferredSize(Button component) {
            return new Dimension(TerminalTextUtils.getColumnWidth(component.getLabel()), 1);
        }
    }

    public static class BorderedButtonRenderer implements ButtonRenderer {
        @Override
        public void drawComponent(TextUiGraphics graphics, Button button) {
            ThemeDefinition themeDefinition = button.getThemeDefinition();
            graphics.applyThemeStyle(themeDefinition.getNormal());
            Dimension size = graphics.getSize();
            graphics.drawLine(1, 0, size.getColumns() - 3, 0, Symbols.SINGLE_LINE_HORIZONTAL);
            graphics.drawLine(1, size.getRows() - 2, size.getColumns() - 3, size.getRows() - 2, Symbols.SINGLE_LINE_HORIZONTAL);
            graphics.drawLine(0, 1, 0, size.getRows() - 3, Symbols.SINGLE_LINE_VERTICAL);
            graphics.drawLine(size.getColumns() - 2, 1, size.getColumns() - 2, size.getRows() - 3, Symbols.SINGLE_LINE_VERTICAL);
            graphics.setCharacter(0, 0, Symbols.SINGLE_LINE_TOP_LEFT_CORNER);
            graphics.setCharacter(size.getColumns() - 2, 0, Symbols.SINGLE_LINE_TOP_RIGHT_CORNER);
            graphics.setCharacter(size.getColumns() - 2, size.getRows() - 2, Symbols.SINGLE_LINE_BOTTOM_RIGHT_CORNER);
            graphics.setCharacter(0, size.getRows() - 2, Symbols.SINGLE_LINE_BOTTOM_LEFT_CORNER);

            // Fill the inner part of the box
            graphics.drawLine(1, 1, size.getColumns() - 3, 1, ' ');

            // Draw the text inside the button
            if (button.isFocused()) {
                graphics.applyThemeStyle(themeDefinition.getActive());
            }
            graphics.putString(2, 1, TerminalTextUtils.fitString(button.getLabel(), size.getColumns() - 5));

            // Draw the shadow
            graphics.applyThemeStyle(themeDefinition.getInsensitive());
            graphics.drawLine(1, size.getRows() - 1, size.getColumns() - 1, size.getRows() - 1, ' ');
            graphics.drawLine(size.getColumns() - 1, 1, size.getColumns() - 1, size.getRows() - 2, ' ');
        }

        @Override
        public Point getCursorLocation(Button component) {
            return null;
        }

        @Override
        public Dimension getPreferredSize(Button component) {
            return new Dimension(TerminalTextUtils.getColumnWidth(component.getLabel()) + 5, 4);
        }
    }
}

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
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.input.MouseAction;
import com.googlecode.lanterna.input.MouseActionType;

import java.util.Optional;

/**
 * This class is a list box implementation that displays a number of items that has actions associated with them. You
 * can activate this action by pressing the Enter or Space keys on the keyboard and the action associated with the
 * currently selected item will fire.
 *
 * @author Martin
 */
public class ActionListBox extends AbstractListBox<ActionListBox.Item, ActionListBox> {

    /**
     * Default constructor, creates an {@code ActionListBox} with no pre-defined size that will request to be big enough
     * to display all items
     */
    public ActionListBox() {
        this(null, Attributes.EMPTY);
    }

    public ActionListBox(Attributes attributes) {
        this(null, attributes);
    }

    /**
     * Creates a new {@code ActionListBox} with a pre-set size. If the items don't fit in within this size, scrollbars
     * will be used to accommodate. Calling {@code new ActionListBox(null)} has the same effect as calling
     * {@code new ActionListBox()}.
     *
     * @param preferredSize Preferred size of this {@link ActionListBox}
     */
    public ActionListBox(Dimension preferredSize) {
        this(preferredSize, Attributes.EMPTY);
    }

    public ActionListBox(Dimension preferredSize, Attributes attributes) {
        super(preferredSize, new ActionListBoxItemRenderer(), attributes);
    }

    /**
     * Adds a new item to the list, which is displayed in the list using a supplied label.
     *
     * @param label         Label to use in the list for the new item
     * @param clickListener Runnable to invoke when this action is selected and then triggered
     * @return Itself
     */
    public ActionListBox addItem(String label, ClickListener clickListener) {
        return addItem(new Item(label, clickListener));
    }

    @Override
    public Point getCursorLocation() {
        return null;
    }

    public void onItemSelected() {
        Optional.ofNullable(getSelectedItem()).ifPresent(i -> i.getClickListener().onClicked(this));
    }

    @Override
    public KeyStrokeResult onKeyStroke(KeyStroke keyStroke) {
        if (isKeyboardActivationStroke(keyStroke)) {
            onItemSelected();
            return KeyStrokeResult.HANDLED;
        } else if (keyStroke.getKeyType() == KeyType.MouseEvent) {
            MouseAction mouseAction = (MouseAction) keyStroke;
            MouseActionType actionType = mouseAction.getActionType();

            if (isMouseMove(keyStroke)
                || actionType == MouseActionType.CLICK_RELEASE
                || actionType == MouseActionType.SCROLL_UP
                || actionType == MouseActionType.SCROLL_DOWN) {
                return super.onKeyStroke(keyStroke);
            }

            // includes mouse drag
            int existingIndex = getSelectedIndex();
            int newIndex = getIndexByMouseAction(mouseAction);
            if (existingIndex != newIndex || !isFocused() || actionType == MouseActionType.CLICK_DOWN) {
                // the index has changed, or the focus needs to be obtained, or the user is clicking on the current selection to perform the action again
                KeyStrokeResult keyStrokeResult = super.onKeyStroke(keyStroke);
                onItemSelected();
                return keyStrokeResult;
            }
            return KeyStrokeResult.HANDLED;
        } else {
            return super.onKeyStroke(keyStroke);
        }
    }

    public static class Item {

        private final String label;
        private final ClickListener clickListener;

        public Item(String label, ClickListener clickListener) {
            this.label = label;
            this.clickListener = clickListener;
        }

        public ClickListener getClickListener() {
            return clickListener;
        }

        public String getLabel() {
            return label;
        }
    }

    public static class ActionListBoxItemRenderer extends ListItemRenderer<Item, ActionListBox> {

        @Override
        public String getLabel(ActionListBox listBox, int index, Item item) {
            return item.getLabel();
        }
    }
}

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
package com.googlecode.lanterna.gui2.dialogs;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.Interactable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Dialog builder for the {@code ActionListDialog} class, use this to create instances of that class and to customize
 * them
 *
 * @author Martin
 */
public class ActionListDialogBuilder extends AbstractDialogBuilder<ActionListDialogBuilder, ActionListDialog> {

    private final List<ActionListBox.Item> items = new ArrayList<>();
    private TerminalSize listBoxSize;
    private boolean cancellable;
    private boolean onSelectionClose;

    /**
     * Default constructor
     */
    public ActionListDialogBuilder() {
        super("ActionListDialogBuilder");
        this.listBoxSize = null;
        this.cancellable = true;
        this.onSelectionClose = true;
    }

    @Override
    protected ActionListDialog buildDialog() {
        return new ActionListDialog(title, description, listBoxSize, cancellable, onSelectionClose, items);
    }

    /**
     * Sets if the dialog can be cancelled or not (default: {@code true})
     *
     * @param cancellable If {@code true}, the user has the option to cancel the dialog, if {@code false} there is no such
     *                    button in the dialog
     * @return Itself
     */
    public ActionListDialogBuilder cancellable(boolean cancellable) {
        this.cancellable = cancellable;
        return this;
    }

    /**
     * Adds an additional action to the {@code ActionListBox} that is to be displayed when the dialog is opened
     *
     * @param label  Label of the new action
     * @param action Action to perform if the user selects this item
     * @return Itself
     */
    public ActionListDialogBuilder item(final String label, final Runnable action) {
        return item(label, s -> action.run());
    }

    public ActionListDialogBuilder item(String label, Interactable.ClickListener clickListener) {
        return item(new ActionListBox.Item(label, clickListener));
    }

    /**
     * Adds an additional action to the {@code ActionListBox} that is to be displayed when the dialog is opened. The
     * label of this item will be derived by calling {@code toString()} on the runnable
     *
     * @param item Action to perform if the user selects this item
     * @return Itself
     */
    public ActionListDialogBuilder item(ActionListBox.Item item) {
        this.items.add(item);
        return this;
    }

    /**
     * Adds additional actions to the {@code ActionListBox} that is to be displayed when the dialog is opened. The
     * label of the items will be derived by calling {@code toString()} on each runnable
     *
     * @param items Items to add to the {@code ActionListBox}
     * @return Itself
     */
    public ActionListDialogBuilder items(ActionListBox.Item... items) {
        this.items.addAll(Arrays.asList(items));
        return this;
    }

    /**
     * Sets the size of the internal {@code ActionListBox} in columns and rows, forcing scrollbars to appear if the
     * space isn't big enough to contain all the items
     *
     * @param listBoxSize Size of the {@code ActionListBox}
     * @return Itself
     */
    public ActionListDialogBuilder listSize(TerminalSize listBoxSize) {
        this.listBoxSize = listBoxSize;
        return this;
    }

    /**
     * Sets if clicking on an action automatically closes the dialog after the action is finished (default: {@code true})
     *
     * @param onSelectionClose if {@code true} dialog will be automatically closed after choosing and finish any of the action
     * @return Itself
     */
    public ActionListDialogBuilder onSelectionClose(boolean onSelectionClose) {
        this.onSelectionClose = onSelectionClose;
        return this;
    }

    @Override
    protected ActionListDialogBuilder self() {
        return this;
    }
}

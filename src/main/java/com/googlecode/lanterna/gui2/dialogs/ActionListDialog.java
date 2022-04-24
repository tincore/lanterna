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

import com.googlecode.lanterna.Dimension;
import com.googlecode.lanterna.gui2.*;

import java.util.List;

/**
 * Dialog containing a multiple item action list box
 *
 * @author Martin
 */
public class ActionListDialog extends DialogWindow {

    public ActionListDialog(String title, String description, Dimension actionListPreferredSize,
                            boolean cancellable, final boolean onSelectionClose, ActionListBox.Item... items) {
        this(title, description, actionListPreferredSize, cancellable, onSelectionClose, List.of(items));
    }

    public ActionListDialog(String title, String description, Dimension actionListPreferredSize,
                            boolean cancellable, final boolean onSelectionClose, List<ActionListBox.Item> items) {

        super(title);

        Panel mainPanel = new Panel(
            new GridLayout(1)
                .setLeftMarginSize(1)
                .setRightMarginSize(1));

        if (description != null) {
            mainPanel
                .add(new Label(description))
                .add(new EmptySpace(Dimension.ONE));
        }

        ActionListBox listBox = new ActionListBox(actionListPreferredSize);
        if (onSelectionClose) {
            for (ActionListBox.Item item : items) {
                listBox.addItem(new ActionListBox.Item(item.getLabel(), s -> {
                    item.getClickListener().onClicked(s);
                    close();
                }));
            }
        } else {
            listBox.addItems(items);
        }

        listBox.setLayoutData(
            GridLayout.createLayoutData(
                GridLayout.Alignment.FILL,
                GridLayout.Alignment.CENTER,
                true,
                false))
            .addTo(mainPanel);
        mainPanel.add(new EmptySpace(Dimension.ONE));

        if (cancellable) {
            Panel buttonPanel = new Panel();
            buttonPanel.setLayoutManager(new GridLayout(2).setHorizontalSpacing(1));
            buttonPanel.add(new Button(LocalizedString.Cancel.toString(), s -> onCancel()).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true, false)));
            buttonPanel.setLayoutData(
                GridLayout.createLayoutData(
                    GridLayout.Alignment.END,
                    GridLayout.Alignment.CENTER,
                    false,
                    false))
                .addTo(mainPanel);
        }
        setComponent(mainPanel);
    }

    /**
     * Helper method for immediately displaying a {@code ActionListDialog}, the method will return when the dialog is
     * closed
     *
     * @param textGUI     Text GUI the dialog should be added to
     * @param title       Title of the dialog
     * @param description Description of the dialog
     * @param items       Items in the {@code ActionListBox}, the label will be taken from each {@code Runnable} by calling
     *                    {@code toString()} on each one
     */
    public static void showDialog(WindowBasedTextGUI textGUI, String title, String description, ActionListBox.Item... items) {
        ActionListDialog actionListDialog = new ActionListDialogBuilder()
            .title(title)
            .description(description)
            .items(items)
            .build();
        actionListDialog.show(textGUI);
    }

    public void onCancel() {
        close();
    }
}

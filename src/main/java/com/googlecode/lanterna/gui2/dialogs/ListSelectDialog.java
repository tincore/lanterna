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
import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.gui2.*;

import java.util.List;

/**
 * Dialog that allows the user to select an item from a list
 *
 * @param <T> Type of elements in the list
 * @author Martin
 */
public class ListSelectDialog<T> extends DialogWindow {
    private T result;

    public ListSelectDialog(String title, String description, TerminalSize listBoxPreferredSize, boolean cancellable, T... items) {
        this(title, description, listBoxPreferredSize, cancellable, List.of(items));
    }

    public ListSelectDialog(String title, String description, TerminalSize listBoxPreferredSize, boolean cancellable, List<T> items) {
        super(title);
        if (items.isEmpty()) {
            throw new IllegalStateException("ListSelectDialog needs at least one item");
        }

        ActionListBox listBox = new ActionListBox(listBoxPreferredSize);
        for (final T item : items) {
            listBox.addItem(item.toString(), s -> onItemSelected(item));
        }

        Panel mainPanel = new Panel(new GridLayout(1).setLeftMarginSize(1).setRightMarginSize(1));
        if (description != null) {
            mainPanel
                .add(new Label(description))
                .add(new EmptySpace(TerminalSize.ONE));
        }
        listBox.setLayoutData(
            GridLayout.createLayoutData(
                GridLayout.Alignment.FILL,
                GridLayout.Alignment.CENTER,
                true,
                false))
            .addTo(mainPanel);

        mainPanel.add(new EmptySpace(TerminalSize.ONE));

        if (cancellable) {
            Panel buttonPanel = new Panel(new GridLayout(2).setHorizontalSpacing(1))
                .add(new Button(LocalizedString.Cancel.toString(), s -> onCancel()).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true, false)));

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
     * Shortcut for quickly creating a new dialog
     *
     * @param textGUI     Text GUI to add the dialog to
     * @param title       Title of the dialog
     * @param description Description of the dialog
     * @param items       Items in the dialog
     * @param <T>         Type of items in the dialog
     * @return The selected item or {@code null} if cancelled
     */
    @SafeVarargs
    public static <T> T showDialog(WindowBasedTextGUI textGUI, String title, String description, T... items) {
        return showDialog(textGUI, title, description, null, items);
    }

    /**
     * Shortcut for quickly creating a new dialog
     *
     * @param textGUI       Text GUI to add the dialog to
     * @param title         Title of the dialog
     * @param description   Description of the dialog
     * @param listBoxHeight Maximum height of the list box, scrollbars will be used if there are more items
     * @param items         Items in the dialog
     * @param <T>           Type of items in the dialog
     * @return The selected item or {@code null} if cancelled
     */
    @SafeVarargs
    public static <T> T showDialog(WindowBasedTextGUI textGUI, String title, String description, int listBoxHeight, T... items) {
        int width = 0;
        for (T item : items) {
            width = Math.max(width, TerminalTextUtils.getColumnWidth(item.toString()));
        }
        width += 2;
        return showDialog(textGUI, title, description, new TerminalSize(width, listBoxHeight), items);
    }

    /**
     * Shortcut for quickly creating a new dialog
     *
     * @param textGUI     Text GUI to add the dialog to
     * @param title       Title of the dialog
     * @param description Description of the dialog
     * @param listBoxSize Maximum size of the list box, scrollbars will be used if the items cannot fit
     * @param items       Items in the dialog
     * @param <T>         Type of items in the dialog
     * @return The selected item or {@code null} if cancelled
     */
    @SafeVarargs
    public static <T> T showDialog(WindowBasedTextGUI textGUI, String title, String description, TerminalSize listBoxSize, T... items) {
        ListSelectDialog<T> listSelectDialog = new ListSelectDialogBuilder<T>()
            .setTitle(title)
            .setDescription(description)
            .setListBoxSize(listBoxSize)
            .addListItems(items)
            .build();
        return listSelectDialog.showDialog(textGUI);
    }

    public void onCancel() {
        close();
    }

    public void onItemSelected(T item) {
        result = item;
        close();
    }

    /**
     * {@inheritDoc}
     *
     * @param textGUI Text GUI to add the dialog to
     * @return The item in the list that was selected or {@code null} if the dialog was cancelled
     */
    @Override
    public T showDialog(WindowBasedTextGUI textGUI) {
        result = null;
        super.showDialog(textGUI);
        return result;
    }
}

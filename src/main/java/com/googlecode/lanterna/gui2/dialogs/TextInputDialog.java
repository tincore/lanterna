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

import java.math.BigInteger;
import java.util.regex.Pattern;

/**
 * {@code TextInputDialog} is a modal text input dialog that prompts the user to enter a text string. The class supports
 * validation and password masking. The builder class to help setup {@code TextInputDialog}s is
 * {@code TextInputDialogBuilder}.
 */
public class TextInputDialog extends DialogWindow {

    private final TextBox textBox;
    private final TextInputDialogResultValidator validator;
    private String result;

    TextInputDialog(
        String title,
        String description,
        Dimension textBoxPreferredSize,
        String initialContent,
        TextInputDialogResultValidator validator,
        boolean password) {

        super(title);
        this.result = null;
        this.textBox = new TextBox(textBoxPreferredSize, initialContent);
        this.validator = validator;

        if (password) {
            textBox.setMask('*');
        }

        Panel buttonPanel = new Panel();
        buttonPanel.setLayoutManager(new GridLayout(2).setHorizontalSpacing(1));
        buttonPanel.add(new Button(LocalizedString.OK.toString(), s -> onOK()).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true, false)));
        buttonPanel.add(new Button(LocalizedString.Cancel.toString(), s -> onCancel()));

        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(
            new GridLayout(1)
                .setLeftMarginSize(1)
                .setRightMarginSize(1));
        if (description != null) {
            mainPanel.add(new Label(description));
        }
        mainPanel.add(new EmptySpace(Dimension.ONE));
        textBox.setLayoutData(
            GridLayout.createLayoutData(
                GridLayout.Alignment.FILL,
                GridLayout.Alignment.CENTER,
                true,
                false))
            .addTo(mainPanel);
        mainPanel.add(new EmptySpace(Dimension.ONE));
        buttonPanel.setLayoutData(
            GridLayout.createLayoutData(
                GridLayout.Alignment.END,
                GridLayout.Alignment.CENTER,
                false,
                false))
            .addTo(mainPanel);
        setComponent(mainPanel);
    }

    /**
     * Shortcut for quickly showing a {@code TextInputDialog}
     *
     * @param textGUI        GUI to show the dialog on
     * @param title          Title of the dialog
     * @param description    Description of the dialog
     * @param initialContent What content to place in the text box initially
     * @return The string the user typed into the text box, or {@code null} if the dialog was cancelled
     */
    public static String showDialog(WindowBasedTextGUI textGUI, String title, String description, String initialContent) {
        TextInputDialog textInputDialog = new TextInputDialogBuilder()
            .title(title)
            .description(description)
            .setInitialContent(initialContent)
            .build();
        return textInputDialog.show(textGUI);
    }

    /**
     * Shortcut for quickly showing a {@code TextInputDialog} that only accepts numbers
     *
     * @param textGUI        GUI to show the dialog on
     * @param title          Title of the dialog
     * @param description    Description of the dialog
     * @param initialContent What content to place in the text box initially
     * @return The number the user typed into the text box, or {@code null} if the dialog was cancelled
     */
    public static BigInteger showNumberDialog(WindowBasedTextGUI textGUI, String title, String description, String initialContent) {
        TextInputDialog textInputDialog = new TextInputDialogBuilder()
            .title(title)
            .description(description)
            .setInitialContent(initialContent)
            .setValidationPattern(Pattern.compile("[0-9]+"), "Not a number")
            .build();
        String numberString = textInputDialog.show(textGUI);
        return numberString != null ? new BigInteger(numberString) : null;
    }

    /**
     * Shortcut for quickly showing a {@code TextInputDialog} with password masking
     *
     * @param textGUI        GUI to show the dialog on
     * @param title          Title of the dialog
     * @param description    Description of the dialog
     * @param initialContent What content to place in the text box initially
     * @return The string the user typed into the text box, or {@code null} if the dialog was cancelled
     */
    public static String showPasswordDialog(WindowBasedTextGUI textGUI, String title, String description, String initialContent) {
        TextInputDialog textInputDialog = new TextInputDialogBuilder()
            .title(title)
            .description(description)
            .setInitialContent(initialContent)
            .setPasswordInput(true)
            .build();
        return textInputDialog.show(textGUI);
    }

    public void onCancel() {
        close();
    }

    public void onOK() {
        String text = textBox.getText();
        if (validator != null) {
            String errorMessage = validator.validate(text);
            if (errorMessage != null) {
                MessageDialog.showMessageDialog(getTextGUI(), getTitle(), errorMessage, MessageDialogButton.OK);
                return;
            }
        }

        result = text;
        close();
    }

    @Override
    public String show(WindowBasedTextGUI textGUI) {
        result = null;
        super.show(textGUI);
        return result;
    }
}

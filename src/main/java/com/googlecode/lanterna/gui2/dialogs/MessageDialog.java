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
import com.googlecode.lanterna.gui2.*;

/**
 * Simple message dialog that displays a message and has optional selection/confirmation buttons
 *
 * @author Martin
 */
public class MessageDialog extends DialogWindow {

    private MessageDialogButton result;

    MessageDialog(
        String title,
        String text,
        MessageDialogButton... buttons) {

        super(title);
        this.result = null;
        if (buttons == null || buttons.length == 0) {
            buttons = new MessageDialogButton[]{MessageDialogButton.OK};
        }


        Panel mainPanel = new Panel()
            .setLayoutManager(
                new GridLayout(1)
                    .setLeftMarginSize(1)
                    .setRightMarginSize(1))
            .add(new Label(text))
            .add(new EmptySpace(TerminalSize.ONE));

        Panel buttonPanel = new Panel()
            .setLayoutManager(new GridLayout(buttons.length).setHorizontalSpacing(1));
        for (final MessageDialogButton button : buttons) {
            buttonPanel.add(new Button(button.toString(), s -> {
                result = button;
                close();
            }));
        }
        buttonPanel.setLayoutData(
            GridLayout.createLayoutData(
                GridLayout.Alignment.END,
                GridLayout.Alignment.CENTER,
                false,
                false))
            .addTo(mainPanel);
//        mainPanel.addComponent(buttonPanel);

        setComponent(mainPanel);
    }

    /**
     * Shortcut for quickly displaying a message box
     *
     * @param textGUI The GUI to display the message box on
     * @param title   Title of the message box
     * @param text    Main message of the message box
     * @param buttons Buttons that the user can confirm the message box with
     * @return Which button the user selected
     */
    public static MessageDialogButton showMessageDialog(
        WindowBasedTextGUI textGUI,
        String title,
        String text,
        MessageDialogButton... buttons) {
        MessageDialogBuilder builder = new MessageDialogBuilder()
            .setTitle(title)
            .setText(text);
        if (buttons.length == 0) {
            builder.addButton(MessageDialogButton.OK);
        }
        for (MessageDialogButton button : buttons) {
            builder.addButton(button);
        }
        return builder.build().show(textGUI);
    }

    /**
     * {@inheritDoc}
     *
     * @param textGUI Text GUI to add the dialog to
     * @return The selected button's enum value
     */
    @Override
    public MessageDialogButton show(WindowBasedTextGUI textGUI) {
        result = null;
        super.show(textGUI);
        return result;
    }
}

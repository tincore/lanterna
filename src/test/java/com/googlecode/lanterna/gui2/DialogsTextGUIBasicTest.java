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
import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.gui2.dialogs.*;
import com.googlecode.lanterna.screen.Screen;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @author Martin
 */
public class DialogsTextGUIBasicTest {
    public static void main(String[] args) throws IOException {
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.start();
        final WindowFrame textGUI = new MultiWindowFrame(screen);
        try {
            final BasicWindow window = new BasicWindow("Dialog test");

            Panel mainPanel = new Panel();
            ActionListBox dialogsListBox = new ActionListBox();
            dialogsListBox.addItem("Simple TextInputDialog",
                s -> {
                    String result = TextInputDialog.showDialog(textGUI, "TextInputDialog sample", "This is the description", "initialContent");
                    System.out.println("Result was: " + result);
                });
            dialogsListBox.addItem("Password input",
                s -> {
                    String result = TextInputDialog.showPasswordDialog(textGUI, "Test password input", "This is a password input dialog", "");
                    System.out.println("Result was: " + result);
                });
            dialogsListBox.addItem("Multi-line input",
                s -> {
                    String result = new TextInputDialogBuilder()
                        .title("Multi-line editor")
                        .setTextBoxSize(new Dimension(35, 5))
                        .build()
                        .show(textGUI);
                    System.out.println("Result was: " + result);
                });
            dialogsListBox.addItem("Numeric input",
                s -> {
                    String result = new TextInputDialogBuilder()
                        .title("Numeric input")
                        .description("Enter a number")
                        .setValidationPattern(Pattern.compile("[0-9]+"), "Please enter a valid number")
                        .build()
                        .show(textGUI);
                    System.out.println("Result was: " + result);
                });
            dialogsListBox.addItem("File dialog (open)",
                s -> {
                    File result = new FileDialogBuilder()
                        .title("Open File")
                        .description("Choose a file:")
                        .setActionLabel(LocalizedString.Open.toString())
                        .build()
                        .show(textGUI);
                    System.out.println("Result was: " + result);
                });
            dialogsListBox.addItem("File dialog (save)",
                s -> {
                    File result = new FileDialogBuilder()
                        .title("Save File")
                        .description("Choose a file:")
                        .setActionLabel(LocalizedString.Save.toString())
                        .build()
                        .show(textGUI);
                    System.out.println("Result was: " + result);
                });
            dialogsListBox.addItem("Action list dialog",
                s -> new ActionListDialogBuilder()
                    .title("Action List Dialog")
                    .description("Choose an item")
                    .item("First Item", s2 -> MessageDialog.showMessageDialog(textGUI, "Action List Dialog", "You chose First Item", MessageDialogButton.OK))
                    .item("Second Item", s2 -> MessageDialog.showMessageDialog(textGUI, "Action List Dialog", "You chose Second Item", MessageDialogButton.OK))
                    .item("Third Item", s2 -> MessageDialog.showMessageDialog(textGUI, "Action List Dialog", "You chose Third Item", MessageDialogButton.OK))
                    .build()
                    .show(textGUI));

            mainPanel.add(dialogsListBox);
            mainPanel.add(new EmptySpace(Dimension.ONE));
            mainPanel.add(new Button("Exit", s -> window.close()));
            window.setComponent(mainPanel);

            textGUI.addWindowAndWait(window);
        } finally {
            screen.stop();
        }
    }
}

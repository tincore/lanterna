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
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;

import java.io.IOException;
import java.util.Arrays;
import java.util.TimeZone;
import java.util.regex.Pattern;

import static com.googlecode.lanterna.gui2.Borders.singleLine;
import static com.googlecode.lanterna.gui2.Borders.singleLineBevel;
import static com.googlecode.lanterna.gui2.Panels.horizontal;
import static com.googlecode.lanterna.gui2.Panels.vertical;

public class ComboBoxTest extends AbstractGuiTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        new ComboBoxTest().run(args);
    }

    @Override
    public void init(final WindowFrame textGUI) {
        Window window = new BasicWindow("ComboBoxTest");

        final ComboBox<String> comboBoxReadOnly = new ComboBox<>();
        final ComboBox<String> comboBoxEditable = new ComboBox<String>().setReadOnly(false);
        final ComboBox<String> comboBoxTimeZones = new ComboBox<String>().setReadOnly(true);

        for (String item : Arrays.asList("Berlin", "London", "Paris", "Stockholm", "Tokyo")) {
            comboBoxEditable.addItem(item);
            comboBoxReadOnly.addItem(item);
        }
        for (String id : TimeZone.getAvailableIDs()) {
            comboBoxTimeZones.addItem(id);
        }

        final TextBox textBoxNewItem = new TextBox(new Dimension(20, 1));
        final TextBox textBoxSetSelectedIndex = new TextBox(new Dimension(20, 1), "0")
            .setValidationPattern(Pattern.compile("-?[0-9]+"));

        TextBox textBoxSetSelectedItem = new TextBox(new Dimension(20, 1));


        Panel mainPanel = new Panel()
            .add(horizontal(
                singleLine("Read-only", comboBoxReadOnly),
                comboBoxEditable.withBorder(singleLine("Editable")),
                new ComboBox<String>().setReadOnly(false).setPreferredSize(new Dimension(13, 1))
                    .addItem(
                        "维基百科人人可編輯的自由百科全書",
                        "ウィキペディアは誰でも編集できるフリー百科事典です",
                        "위키백과는 전 세계 여러 언어로 만들어 나가는 자유 백과사전으로, 누구나 참여하실 수 있습니다.",
                        "This is a string without double-width characters").withBorder(singleLine("CJK"))))
            .add(new EmptySpace(Dimension.ONE));


        mainPanel.add(
            vertical(
                horizontal(textBoxNewItem, new Button("Add", s3 -> {
                    comboBoxEditable.addItem(textBoxNewItem.getText());
                    comboBoxReadOnly.addItem(textBoxNewItem.getText());
                    textBoxNewItem.setText("");
                    window.setFocusedInteractable(textBoxNewItem);
                })),
                horizontal(textBoxSetSelectedIndex, new Button("Set Selected Index", s2 -> {
                    try {
                        comboBoxEditable.setSelectedIndex(Integer.parseInt(textBoxSetSelectedIndex.getText()));
                        comboBoxReadOnly.setSelectedIndex(Integer.parseInt(textBoxSetSelectedIndex.getText()));
                    } catch (Exception e1) {
                        MessageDialog.showMessageDialog(textGUI, e1.getClass().getName(), e1.getMessage(), MessageDialogButton.OK);
                    }
                })),
                horizontal(textBoxSetSelectedItem, new Button("Set Selected Item", s1 -> {
                    try {
                        comboBoxEditable.setSelectedItem(textBoxSetSelectedItem.getText());
                        comboBoxReadOnly.setSelectedItem(textBoxSetSelectedItem.getText());
                    } catch (Exception e) {
                        MessageDialog.showMessageDialog(textGUI, e.getClass().getName(), e.getMessage(), MessageDialogButton.OK);
                    }
                })))
                .withBorder(singleLineBevel("Modify Content")))
            .add(new EmptySpace(Dimension.ONE))
            .add(comboBoxTimeZones.withBorder(singleLine("Large ComboBox")))
            .add(new EmptySpace(Dimension.ONE))
            .add(new Separator(Direction.HORIZONTAL).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill)))
            .add(new Button("OK", s -> window.close()));
        window.setComponent(mainPanel);


        textGUI.addWindow(window);
    }
}

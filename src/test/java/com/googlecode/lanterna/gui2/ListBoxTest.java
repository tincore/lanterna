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

import java.io.IOException;

import static com.googlecode.lanterna.gui2.Borders.singleLine;
import static com.googlecode.lanterna.gui2.Panels.vertical;

/**
 * Simple test for the different kinds of list boxes
 *
 * @author Martin
 */
public class ListBoxTest extends AbstractGuiTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        new ListBoxTest().run(args);
    }

    @Override
    public void init(WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("ListBox test");

        Dimension size = new Dimension(14, 10);
        CheckBoxList<String> checkBoxList = new CheckBoxList<>(size);
        RadioBoxList<String> radioBoxList = new RadioBoxList<>(size);
        ActionListBox actionListBox = new ActionListBox(size);
        for (int i = 0; i < 30; i++) {
            final String itemText = "Item " + (i + 1);
            checkBoxList.addItem(itemText);
            radioBoxList.addItem(itemText);
            actionListBox.addItem(itemText, s -> System.out.println("Selected " + itemText));
        }
        Panel horizontalPanel = new Panel()
            .setLayoutManager(new LinearLayout(Direction.HORIZONTAL))
            .add(checkBoxList.withBorder(singleLine("CheckBoxList")))
            .add(radioBoxList.withBorder(singleLine("RadioBoxList")))
            .add(actionListBox.withBorder(singleLine("ActionListBox")));

        window.setComponent(vertical(horizontalPanel, new Button("OK", s -> window.close())));
        textGUI.addWindow(window);
    }
}

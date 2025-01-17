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
package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.List;

public class Issue212 {
    public static void main(String[] args) throws IOException {
        final Table<String> table = new Table<>("Column 1", "Column 2", "Column 3");

        table.setClickListener(s -> {
            List<String> data = table.getTableModel().getRow(
                table.getSelectedRow());
            for (String aData : data) {
                System.out.println(aData);
            }
        });

        table.getTableModel()
            .addRow("1", "2", "3")
            .addRow("1", "2", "3")
            .addRow("1", "2", "3")
            .addRow("1", "2", "3")
            .addRow("1", "2", "3");

        Window win = new BasicWindow();
        win.setComponent(table);

        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        Terminal terminal = factory.createTerminal();

        Screen screen = new TerminalScreen(terminal);
        screen.start();

        // Create gui and start gui
        MultiWindowFrame gui = new MultiWindowFrame(screen,
            new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
        gui.addWindowAndWait(win);

        screen.stop();
    }
}

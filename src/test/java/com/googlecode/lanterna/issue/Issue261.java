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

import com.googlecode.lanterna.Dimension;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

/**
 * Test case for Issue261
 */
public class Issue261 {
    public static void main(String[] args) throws IOException {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.start();

        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(2));

        panel.add(new Label("Forename"));
        panel.add(new TextBox());

        panel.add(new Label("Surname"));
        panel.add(new TextBox());

        panel.add(new EmptySpace(new Dimension(0,0))); // Empty space underneath labels
        panel.add(new Button("Submit"));

        // Create gui and start gui
        MultiWindowFrame gui = new MultiWindowFrame(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));

        // Create window to hold the panel
        BasicWindow window = new BasicWindow();
        window.setFixedSize(new Dimension(500, 700));
        window.setComponent(panel);

        gui.addWindowAndWait(window);
    }
}

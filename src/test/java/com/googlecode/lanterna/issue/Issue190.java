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
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class Issue190 {
    public static void main(String[] args) throws IOException {
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        factory.setInitialTerminalSize(new Dimension(150, 50));
        factory.setTerminalEmulatorTitle("name");
        Terminal terminal = factory.createTerminal();
        TerminalScreen screen = new TerminalScreen(terminal);
        screen.start();
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));

        Panel panel = Panels.border()
            .add(new ActionListBox().withBorder(Borders.singleLine("Channels")), BorderLayout.Location.LEFT);

        panel.add(new TextBox("", TextBox.Style.MULTI_LINE).setReadOnly(true).setLayoutData(BorderLayout.Location.CENTER).withBorder(Borders.singleLine("Log")));

        Panel options = new Panel().setLayoutData(BorderLayout.Location.BOTTOM);
        options.withBorder(Borders.singleLine("Send Message"));

        options.setLayoutManager(new BorderLayout());

        final TextBox input = new TextBox("Message", TextBox.Style.SINGLE_LINE).setLayoutData(BorderLayout.Location.CENTER);
        options.add(input);
        options.add(new Button("Send", s -> input.setText("")).setLayoutData(BorderLayout.Location.RIGHT));

        panel.add(options.withBorder(Borders.singleLine("Send Message")));

        gui.addWindowAndWait(new BasicWindow()
            .setHints(Window.Hint.EXPANDED, Window.Hint.FIT_TERMINAL_WINDOW)
            .setComponent(panel.withBorder(Borders.doubleLine("DarkOwlBot"))));
    }
}

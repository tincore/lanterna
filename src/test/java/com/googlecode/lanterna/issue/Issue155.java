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
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class Issue155 {
    public static void main(String... args) throws IOException {
        Terminal term = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(term);
        WindowManager windowManager = new DefaultWindowManager();
        Component background = new EmptySpace(TextColor.ANSI.DEFAULT);
        final WindowFrame gui = new MultiWindowFrame(screen, windowManager, background);
        screen.start();
        gui.addWindowAndWait(new BasicWindow("Issue155") {{
            setComponent(createUi(gui, this));
        }});
        screen.stop();
    }


    private static Panel createUi(WindowFrame gui, BasicWindow window) {
        return createUi(gui, window, 1);
    }

    private static Panel createUi(WindowFrame gui, final BasicWindow window, final int counter) {
        final int nextCounter = counter + 3;
        return Panels.vertical(
                new Button("Open Dialog (and crush stuff)", openDialog(gui, window, nextCounter)),
                new CheckBoxList<String>() {{
                    for (int i = counter; i < nextCounter; ++i) {
                        addItem(String.valueOf(i));
                    }
                }},
                new Button("Quit", s -> window.close())
        );
    }

    private static Interactable.ClickListener openDialog(final WindowFrame gui, final BasicWindow window, final int counter) {
        return s -> new ActionListDialogBuilder().
            cancellable(true).
            item("Reinstall UI (this crashes everything)", setupUI(gui, window, counter)).
                build().
            show(gui);
    }

    private static Runnable setupUI(final WindowFrame gui, final BasicWindow window, final int counter) {
        return () -> window.setComponent(createUi(gui, window, counter));
    }
}

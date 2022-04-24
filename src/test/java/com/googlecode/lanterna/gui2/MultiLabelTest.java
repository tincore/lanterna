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

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

public class MultiLabelTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.start();
        WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
        try {
            final BasicWindow window = new BasicWindow("Label test");
            window.setComponent(new Panel()
                .setLayoutManager(new LinearLayout(Direction.VERTICAL))
                .add(new Label("This is a single line label"))
                .add(new Label("This is another label on the second line"))
                .add(new EmptySpace(new TerminalSize(5, 1)))
                .add(new Label("Here is a\nmulti-line\ntext segment that is using \\n"))
                .add(new Label("We can change foreground color...").setForegroundColor(TextColor.ANSI.BLUE))
                .add(new Label("...and background color...").setBackgroundColor(TextColor.ANSI.MAGENTA))
                .add(new Label("...and add custom SGR styles!")
                    .addStyle(SGR.BOLD)
                    .addStyle(SGR.UNDERLINE))
                .add(new EmptySpace(new TerminalSize(5, 1)))
                .add(new Label("Here is an animated label:"))
                .add(AnimatedLabel.createClassicSpinningLine())
                .add(new EmptySpace())
                .add(new Button("Close", s -> window.close()).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center))));

            textGUI.addWindow(window);
            textGUI.updateScreen();
            while (!textGUI.getWindows().isEmpty()) {
                textGUI.processInput();
                if (textGUI.isPendingUpdate()) {
                    textGUI.updateScreen();
                } else {
                    Thread.sleep(1);
                }
            }
        } finally {
            screen.stop();
        }
    }
}

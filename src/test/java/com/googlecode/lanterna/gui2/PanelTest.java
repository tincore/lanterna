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

import java.io.IOException;

public class PanelTest extends AbstractGuiTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        new PanelTest().run(args);
    }

    @Override
    public void init(WindowFrame textGUI) {
        final BasicWindow window = new BasicWindow("Grid layout test");

        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        Panel leftPanel = new Panel();
        mainPanel.add(leftPanel.withBorder(Borders.singleLine("Left")));

        Panel panel = new Panel();
        panel.add(new Button("Panel 1 Button"));
        leftPanel.add(panel.withBorder(Borders.singleLine()));
        panel = new Panel();
        panel.add(new Button("Panel 2 Button"));
        leftPanel.add(panel.withBorder(Borders.singleLine("Title")));
        panel = new Panel();
        panel.add(new Button("Panel 3 Button"));
        leftPanel.add(panel.withBorder(Borders.doubleLine()));
        panel = new Panel();
        panel.add(new Button("Panel 4 Button"));
        leftPanel.add(panel.withBorder(Borders.doubleLine("Title")));

        Panel rightPanel = new Panel();
        mainPanel.add(rightPanel.withBorder(Borders.singleLine("Right")));

        panel = new Panel();
        panel.add(new Button("Panel 1 Button"));
        panel.add(new Panel().withBorder(Borders.singleLine("A")));
        panel.add(new Panel().withBorder(Borders.singleLine("Some Text")));
        rightPanel.add(panel.withBorder(Borders.singleLine("B")));
        panel = new Panel();
        panel.add(new Button("Panel 2 Button"));
        rightPanel.add(panel.withBorder(Borders.singleLine("Title")));
        panel = new Panel();
        panel.add(new Button("Panel 3 Button"));
        rightPanel.add(panel.withBorder(Borders.doubleLine()));
        panel = new Panel();
        panel.add(new Button("Panel 4 Button"));
        rightPanel.add(panel.withBorder(Borders.doubleLine("Title")));

        window.setComponent(Panels.vertical(
                mainPanel.withBorder(Borders.singleLine("Main")),
                new Button("OK", s -> window.close())));
        textGUI.addWindow(window);
    }
}

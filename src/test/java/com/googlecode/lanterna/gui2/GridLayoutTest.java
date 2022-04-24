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
import com.googlecode.lanterna.TextColor;

import java.io.IOException;

public class GridLayoutTest extends AbstractGuiTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        new GridLayoutTest().run(args);
    }

    @Override
    public void init(WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("Grid layout test");

        EmptySpace visibilityToggleableComponent = new EmptySpace(TextColor.ANSI.CYAN, new Dimension(4, 2));

        Panel leftGridPanel = new Panel(new GridLayout(4))
            .add(new EmptySpace(TextColor.ANSI.BLACK, new Dimension(4, 2)))
            .add(new EmptySpace(TextColor.ANSI.BLUE, new Dimension(4, 2)))
            .add(visibilityToggleableComponent)
            .add(new EmptySpace(TextColor.ANSI.GREEN, new Dimension(4, 2)))

            .add(new EmptySpace(TextColor.ANSI.MAGENTA, new Dimension(4, 2))
                .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER, true, false, 4, 1)))
            .add(new EmptySpace(TextColor.ANSI.RED, new Dimension(4, 2))
                .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true, false, 4, 1)))
            .add(new EmptySpace(TextColor.ANSI.YELLOW, new Dimension(4, 2))
                .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER, true, false, 4, 1)))
            .add(new EmptySpace(TextColor.ANSI.BLACK, new Dimension(4, 2))
                .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.FILL, GridLayout.Alignment.CENTER, true, false, 4, 1)));

        Panel rightGridPanel = new Panel(new GridLayout(5))
            .add(new EmptySpace(TextColor.ANSI.BLACK, new Dimension(4, 2)))
            .add(new EmptySpace(TextColor.ANSI.MAGENTA, new Dimension(4, 2))
                .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.BEGINNING, false, true, 1, 4)));
        rightGridPanel.add(new EmptySpace(TextColor.ANSI.RED, new Dimension(4, 2))
            .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, false, true, 1, 4)));
        rightGridPanel.add(new EmptySpace(TextColor.ANSI.YELLOW, new Dimension(4, 2))
            .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.END, false, true, 1, 4)));
        rightGridPanel.add(new EmptySpace(TextColor.ANSI.BLACK, new Dimension(4, 2))
            .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.FILL, false, true, 1, 4)));
        rightGridPanel.add(new EmptySpace(TextColor.ANSI.BLUE, new Dimension(4, 2)));
        rightGridPanel.add(new EmptySpace(TextColor.ANSI.CYAN, new Dimension(4, 2)));
        rightGridPanel.add(new EmptySpace(TextColor.ANSI.GREEN, new Dimension(4, 2)));

        Panel contentPanel = new Panel();
        contentPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        contentPanel.add(Panels.horizontal(leftGridPanel, new EmptySpace(Dimension.ONE), rightGridPanel));
        contentPanel.add(new EmptySpace(Dimension.ONE));
        contentPanel.add(Panels.horizontal(
            new Button("Toggle Visible Component", s -> visibilityToggleableComponent.setVisible(!visibilityToggleableComponent.isVisible())),
            createButtonCloseContainer()
        ));
        window.setComponent(contentPanel);
        textGUI.addWindow(window);
    }
}

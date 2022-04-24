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
import java.util.Collections;

public class LinearLayoutTest extends TestBase {
    public static void main(String[] args) throws InterruptedException, IOException {
        new LinearLayoutTest().run(args);
    }

    @Override
    public void init(WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("Linear layout test");
        final LinearLayout linearLayout = new LinearLayout(Direction.VERTICAL).setSpacing(1);
        final Panel labelPanel = new Panel().setLayoutManager(linearLayout);

        for (int i = 0; i < 5; i++) {
            new Label("LABEL COMPONENT")
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Beginning, LinearLayout.GrowPolicy.CanGrow))
                .addTo(labelPanel);
        }
        final Panel mainPanel = new Panel()
            .add(labelPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Beginning, LinearLayout.GrowPolicy.CanGrow)));

        new Separator(Direction.HORIZONTAL)
            .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill))
            .addTo(mainPanel);

        mainPanel.add(Panels.horizontal(
            new Button("Add", s -> new Label("LABEL COMPONENT").addTo(labelPanel)),
            new Button("Spacing", s -> linearLayout.setSpacing(linearLayout.getSpacing() == 1 ? 0 : 1)),
            new Button("Toggle Hide Odd #", s -> toggleVisibleOnOddNumberLabels(labelPanel)),
            new Button("Expand", s -> window.setHints(Collections.singletonList(Window.Hint.EXPANDED))),
            new Button("Collapse", s -> window.setHints(Collections.emptySet())),
            new Button("Close", s -> window.close())
        ));

        window.setComponent(mainPanel);
        textGUI.addWindow(window);
    }

    void toggleVisibleOnOddNumberLabels(Panel panel) {
        for (int i = 0; i < panel.getComponentCount(); i++) {
            if ((i + 1) % 2 == 1) {
                Component component = panel.getChildrenList().get(i);
                component.setVisible(!component.isVisible());
            }
        }
    }
}

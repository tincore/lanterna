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

import com.googlecode.lanterna.TestUtils;

import java.io.IOException;
import java.util.Collections;

/**
 * Test/example class for various kinds of window manager behaviours
 *
 * @author Martin
 */
public class SimpleWindowManagerTest extends AbstractGuiTest {


    public static void main(String[] args) throws IOException, InterruptedException {
        new SimpleWindowManagerTest().run(args);
    }

    @Override
    public void init(final WindowFrame textGUI) {
        textGUI.addWindow(new BasicWindow("Choose test")
            .setComponent(new Panel(new LinearLayout(Direction.VERTICAL))
                .add(new Button("Centered window", s -> textGUI.addWindow(new CenteredWindow())))
                .add(new Button("Undecorated window", s -> textGUI.addWindow(new UndecoratedWindow())))
                .add(new Button("Undecorated + Centered window", s -> textGUI.addWindow(new UndecoratedCenteredWindow())))
                .add(new Button("Full-screen window", s -> textGUI.addWindow(new FullScreenWindow(true))))
                .add(new Button("Undecorated + Full-screen window", s -> textGUI.addWindow(new FullScreenWindow(false))))
                .add(new Button("Expanded window", s -> textGUI.addWindow(new ExpandedWindow(true))))
                .add(new Button("Undecorated + Expanded window", s -> textGUI.addWindow(new ExpandedWindow(false))))
                .add(createButtonCloseContainer())));
    }

    private static class CenteredWindow extends TestWindow {
        CenteredWindow() {
            super("Centered window");
            setHints(Collections.singletonList(Hint.CENTERED));
        }
    }

    private static class UndecoratedWindow extends TestWindow {
        UndecoratedWindow() {
            super("Undecorated");
            setHints(Hint.NO_DECORATIONS);
        }
    }

    private static class UndecoratedCenteredWindow extends TestWindow {

        UndecoratedCenteredWindow() {
            super("UndecoratedCentered");
            setHints(Hint.NO_DECORATIONS, Hint.CENTERED);
        }
    }

    private static class FullScreenWindow extends TestWindow {

        public FullScreenWindow(boolean decorations) {
            super("FullScreenWindow");
            setHints(Hint.EXPANDED);
            if (!decorations) {
                addHints(Hint.NO_DECORATIONS);
            }

            setComponent(new Panel(new BorderLayout())
                .add(new TextBox(TestUtils.downloadGPL(), TextBox.Style.MULTI_LINE).setReadOnly(true), BorderLayout.Location.CENTER));
        }
    }

    private static class ExpandedWindow extends TestWindow {

        public ExpandedWindow(boolean decorations) {
            super("ExpandedWindow");

            setHints(Hint.EXPANDED);
            if (!decorations) {
                addHints(Hint.NO_DECORATIONS);
            }
            setComponent(new Panel(new BorderLayout())
                .add(new TextBox(TestUtils.downloadGPL(), TextBox.Style.MULTI_LINE).setReadOnly(true), BorderLayout.Location.CENTER));

        }
    }

    private static class TestWindow extends BasicWindow {
        TestWindow(String title) {
            super(title);
            setComponent(createButtonCloseContainer());
            setOnKeyEscapeClose(true);
        }
    }

}

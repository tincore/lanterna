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
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

public class LineWrappingLabelTest extends AbstractGuiTest {
    public static final String BIG_TEXT =
        "                   GNU LESSER GENERAL PUBLIC LICENSE\n" +
            "                       Version 3, 29 June 2007\n" +
            "\n" +
            " Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>\n" +
            " Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.\n" +
            "\n" +
            "\n" +
            "  This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.\n" +
            "\n" +
            "  0. Additional Definitions.\n" +
            "\n" +
            "  As used herein, \"this License\" refers to version 3 of the GNU Lesser General Public License, and the \"GNU GPL\" refers to version 3 of the GNU General Public License.\n" +
            "\n" +
            "  \"The Library\" refers to a covered work governed by this License, other than an Application or a Combined Work as defined below.";
    private Dimension windowSize;

    public LineWrappingLabelTest() {
        windowSize = new Dimension(70, 15);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new LineWrappingLabelTest().run(args);
    }

    @Override
    protected MultiWindowFrame createTextGUI(Screen screen) {
        return new MultiWindowFrame(
            new SeparateTextUiThread.Factory(),
            screen,
            new MyWindowManager(),
            new WindowShadowRenderer(),
            new EmptySpace(TextColor.ANSI.BLUE));
    }

    @Override
    public void init(WindowFrame textGUI) {
        final BasicWindow window = new BasicWindow("Wrapping label test");
        window.setComponent(new Panel()
            .setLayoutManager(new BorderLayout())
            .add(new Label("Resize window by holding ctrl and pressing arrow keys").setLayoutData(BorderLayout.Location.TOP))
            .add(new Label(BIG_TEXT).withBorder(Borders.doubleLine()).setLayoutData(BorderLayout.Location.CENTER))
            .add(createButtonCloseContainer().setLayoutData(BorderLayout.Location.BOTTOM)));

        textGUI.setKeyStrokeListener((k, h, g) -> {
            if (h) {
                return false;
            }
            if (k.isCtrlDown()) {
                switch (k.getKeyType()) {
                    case ArrowUp:
                        if (windowSize.getRows() > 1) {
                            windowSize = windowSize.withRelativeRows(-1);
                            return true;
                        }
                    case ArrowDown:
                        windowSize = windowSize.withRelativeRows(1);
                        return true;
                    case ArrowLeft:
                        if (windowSize.getColumns() > 1) {
                            windowSize = windowSize.withRelativeColumns(-1);
                            return true;
                        }
                    case ArrowRight:
                        windowSize = windowSize.withRelativeColumns(1);
                        return true;
                    default:
                }
            }
            return false;
        });

        textGUI.addWindow(window);
    }

    private class MyWindowManager extends DefaultWindowManager {
        @Override
        protected void prepareWindow(Dimension screenSize, Window window) {
            super.prepareWindow(screenSize, window);
            window.setDecoratedSize(getWindowDecorationRenderer(window).getDecoratedSize(window, windowSize));
        }
    }
}

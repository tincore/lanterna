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

import com.googlecode.lanterna.bundle.LanternaThemes;

import static com.googlecode.lanterna.gui2.Borders.singleLine;

public class ImageComponentTest extends AbstractGuiTest {
    public static void main(String[] args) throws Exception {
        new ImageComponentTest().run(args);
    }

    @Override
    public void init(WindowBasedTextGUI textGUI) {

        ImageComponent b = createImageComponent("B", IMAGE_BLANK);

        textGUI.addWindow(new BasicWindow("ImageComponentTest")
            .setTheme(LanternaThemes.getTheme("conqueror"))
            .setComponent(Panels.grid(2,
                singleLine("x", createImageComponent("X", IMAGE_X).setKeyStrokeListener((k, r, s) -> b.setTextImage(((ImageComponent) s).getTextImage()))),
                singleLine("y", createImageComponent("Y", IMAGE_Y).setKeyStrokeListener((k1, r1, s1) -> b.setTextImage(((ImageComponent) s1).getTextImage()))),
                singleLine("z", createImageComponent("Z", IMAGE_Z).setKeyStrokeListener((k2, r2, s2) -> b.setTextImage(((ImageComponent) s2).getTextImage()))),
                singleLine("click space", b))));
    }

}


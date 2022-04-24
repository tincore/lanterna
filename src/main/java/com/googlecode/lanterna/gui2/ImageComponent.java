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

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.BasicTextImage;
import com.googlecode.lanterna.graphics.TextImage;
import com.googlecode.lanterna.input.KeyStroke;

/**
 * @author ginkoblongata
 */
public class ImageComponent extends AbstractInteractableComponent {

    private TextImage textImage;

    public ImageComponent() {
        this(Attributes.EMPTY);
    }

    public ImageComponent(Attributes attributes) {
        super(attributes);
        setTextImage(new BasicTextImage(0, 0));
    }

    @Override
    public InteractableRenderer<ImageComponent> createDefaultRenderer() {
        return new InteractableRenderer<>() {
            @Override
            public void drawComponent(TextGUIGraphics graphics, ImageComponent panel) {
                graphics.drawImage(TerminalPosition.TOP_LEFT_CORNER, textImage);
            }

            @Override
            public TerminalPosition getCursorLocation(ImageComponent component) {
                // when null, lanterna hidden cursor for this component
                return null;
            }

            @Override
            public TerminalSize getPreferredSize(ImageComponent component) {
                return textImage.getSize();
            }
        };
    }

    @Override
    public Result onKeyStroke(KeyStroke keyStroke) {
        Result superResult = super.onKeyStroke(keyStroke);

        // just arrows and focus move stuff
        return superResult;
    }

    public ImageComponent setTextImage(TextImage textImage) {
        this.textImage = textImage;
        invalidate();
        return this;
    }

}

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
package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.BasicTextImage;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextImage;

import java.io.IOException;

/**
 * Test to try out drawImage in TextGraphics
 */
public class DrawImageTest {
    public static void main(String[] args) throws IOException {
        //Setup a standard Screen
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.start();
        screen.setCursorPosition(null);

        //Create an 'image' that we fill with recognizable characters
        TextImage image = new BasicTextImage(5, 5);
        TextCharacter imageCharacter = new TextCharacter('X');
        TextGraphics textGraphics = image.newTextGraphics();
        textGraphics.drawRectangle(
                Point.TOP_LEFT_CORNER,
                new Dimension(5, 5),
                imageCharacter.withBackgroundColor(TextColor.ANSI.RED));
        textGraphics.drawRectangle(
                Point.OFFSET_1x1,
                new Dimension(3, 3),
                imageCharacter.withBackgroundColor(TextColor.ANSI.MAGENTA));
        textGraphics.setCharacter(2, 2,
                imageCharacter.withBackgroundColor(TextColor.ANSI.CYAN));

        TextGraphics screenGraphics = screen.newTextGraphics();
        screenGraphics.setBackgroundColor(TextColor.Indexed.fromRGB(50, 50, 50));
        screenGraphics.fill(' ');
        screenGraphics.drawImage(Point.OFFSET_1x1, image);
        screenGraphics.drawImage(new Point(8, 1), image, Point.TOP_LEFT_CORNER, image.getSize().withRelativeColumns(-4));
        screenGraphics.drawImage(new Point(10, 1), image, Point.TOP_LEFT_CORNER, image.getSize().withRelativeColumns(-3));
        screenGraphics.drawImage(new Point(13, 1), image, Point.TOP_LEFT_CORNER, image.getSize().withRelativeColumns(-2));
        screenGraphics.drawImage(new Point(17, 1), image, Point.TOP_LEFT_CORNER, image.getSize().withRelativeColumns(-1));
        screenGraphics.drawImage(new Point(22, 1), image);
        screenGraphics.drawImage(new Point(28, 1), image, new Point(1, 0), image.getSize());
        screenGraphics.drawImage(new Point(33, 1), image, new Point(2, 0), image.getSize());
        screenGraphics.drawImage(new Point(37, 1), image, new Point(3, 0), image.getSize());
        screenGraphics.drawImage(new Point(40, 1), image, new Point(4, 0), image.getSize());

        //Try to draw bigger than the image size, this should ignore the extra size
        screenGraphics.drawImage(new Point(1, 7), image, Point.TOP_LEFT_CORNER, image.getSize().withRelativeColumns(10));

        //0 size should draw nothing
        screenGraphics.drawImage(new Point(8, 7), image, Point.TOP_LEFT_CORNER, Dimension.ZERO);

        //Drawing with a negative source image offset will move the target position
        screenGraphics.drawImage(new Point(8, 7), image, new Point(-2, -2), image.getSize());

        screen.refresh();
        screen.readInput();
        screen.stop();
    }
}

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

import com.googlecode.lanterna.Dimension;
import com.googlecode.lanterna.Point;
import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.TextColor;
import java.io.IOException;
import java.util.Random;

/**
 *
 * @author martin
 */
public class ScreenLineTest {
    private static Point circleLastPoint = null;
    public static void main(String[] args) throws IOException, InterruptedException {
        boolean useAnsiColors = false;
        boolean slow = false;
        boolean circle = false;
        for(String arg: args) {
            if(arg.equals("--ansi-colors")) {
                useAnsiColors = true;
            }
            if(arg.equals("--slow")) {
                slow = true;
            }
            if(arg.equals("--circle")) {
                circle = true;
            }
        }
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.start();

        TextGraphics textGraphics = new ScreenTextGraphics(screen);
        Random random = new Random();
        while(true) {
            KeyStroke keyStroke = screen.pollInput();
            if(keyStroke != null &&
                    (keyStroke.getKeyType() == KeyType.Escape || keyStroke.getKeyType() == KeyType.EOF)) {
                break;
            }
            screen.doResizeIfNecessary();
            Dimension size = textGraphics.getSize();
            TextColor color;
            if(useAnsiColors) {
                color = TextColor.ANSI.values()[random.nextInt(TextColor.ANSI.values().length)];
            }
            else {
                //Draw a rectangle in random indexed color
                color = new TextColor.Indexed(random.nextInt(256));
            }

            Point p1;
            Point p2;
            if(circle) {
                p1 = new Point(size.getColumns() / 2, size.getRows() / 2);
                if(circleLastPoint == null) {
                    circleLastPoint = new Point(0, 0);
                }
                else if(circleLastPoint.getRow() == 0) {
                    if(circleLastPoint.getColumn() < size.getColumns() - 1) {
                        circleLastPoint = circleLastPoint.withRelativeColumn(1);
                    }
                    else {
                        circleLastPoint = circleLastPoint.withRelativeRow(1);
                    }
                }
                else if(circleLastPoint.getRow() < size.getRows() - 1) {
                    if(circleLastPoint.getColumn() == 0) {
                        circleLastPoint = circleLastPoint.withRelativeRow(-1);
                    }
                    else {
                        circleLastPoint = circleLastPoint.withRelativeRow(1);
                    }
                }
                else {
                    if(circleLastPoint.getColumn() > 0) {
                        circleLastPoint = circleLastPoint.withRelativeColumn(-1);
                    }
                    else {
                        circleLastPoint = circleLastPoint.withRelativeRow(-1);
                    }
                }
                p2 = circleLastPoint;
            }
            else {
                p1 = new Point(random.nextInt(size.getColumns()), random.nextInt(size.getRows()));
                p2 = new Point(random.nextInt(size.getColumns()), random.nextInt(size.getRows()));
            }
            textGraphics.setBackgroundColor(color);
            textGraphics.drawLine(p1, p2, ' ');
            textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            textGraphics.putString(4, size.getRows() - 1, "P1 " + p1 + " -> P2 " + p2);
            screen.refresh(Screen.RefreshType.DELTA);
            if(slow) {
                Thread.sleep(500);
            }
        }
        screen.stop();
    }
}

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
package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.Dimension;
import com.googlecode.lanterna.Point;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.concurrent.TimeUnit;

/**
 * Interface extending Terminal that removes the IOException throw clause. You can for example use this instead of 
 * Terminal if you use an implementation that doesn't throw any IOExceptions or if you wrap your terminal in an 
 * IOSafeTerminalAdapter. Please note that readInput() still throws IOException when it is interrupted, in order to fit
 * better in with what normal terminal do when they are blocked on input and you interrupt them.
 * @author Martin
 */
public interface IOSafeTerminal extends Terminal {
    @Override
    void enterPrivateMode();
    @Override
    void exitPrivateMode();
    @Override
    void clearScreen();
    @Override
    void setCursorPosition(int x, int y);
    @Override
    void setCursorPosition(Point point);
    @Override
    Point getCursorPosition();
    @Override
    void setCursorVisible(boolean visible);
    @Override
    void putCharacter(char c);
    @Override
    void putString(String string);
    @Override
    void enableSGR(SGR sgr);
    @Override
    void disableSGR(SGR sgr);
    @Override
    void resetColorAndSGR();
    @Override
    void setForegroundColor(TextColor color);
    @Override
    void setBackgroundColor(TextColor color);
    @Override
    Dimension getTerminalSize();
    @Override
    byte[] enquireTerminal(int timeout, TimeUnit timeoutUnit);
    @Override
    void bell();
    @Override
    void flush();
    @Override
    KeyStroke pollInput();
    @Override
    KeyStroke readInput();
    @Override
    void close();
}

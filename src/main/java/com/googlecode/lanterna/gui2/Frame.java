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

import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

/**
 * This is the base interface for advanced text GUIs supported in Lanterna. You may want to use this in combination with
 * a TextGUIThread, that can be created/retrieved by using {@code getGUIThread()}.
 *
 * @author Martin
 */
public interface Frame {
    /**
     * Returns the interactable component currently in focus
     *
     * @return Component that is currently in input focus
     */
    Interactable getFocusedInteractable();

    /**
     * The first time this method is called, it will create a new TextGUIThread object that you can use to automatically
     * manage this TextGUI instead of manually calling {@code processInput()} and {@code updateScreen()}. After the
     * initial call, it will return the same object as it was originally returning.
     *
     * @return A {@code TextGUIThread} implementation that can be used to asynchronously manage the GUI
     */
    TextUiThread getGUIThread();

    /**
     * Returns the {@link Screen} for this {@link WindowFrame}
     *
     * @return the {@link Screen} used by this {@link WindowFrame}
     */
    Screen getScreen();

    /**
     * Returns the theme currently assigned to this {@link Frame}
     *
     * @return Currently active {@link Theme}
     */
    Theme getTheme();

    /**
     * Sets the global theme to be used by this TextGUI. This value will be set on every TextGUIGraphics object created
     * for drawing the GUI, but individual components can override this if they want. If you don't call this method
     * you should assume that a default theme is assigned by the library.
     *
     * @param theme Theme to use as the default theme for this TextGUI
     */
    Frame setTheme(Theme theme);

    /**
     * This method can be used to determine if any component has requested a redraw. If this method returns
     * {@code true}, you may want to call {@code updateScreen()}.
     *
     * @return {@code true} if this TextGUI has a change and is waiting for someone to call {@code updateScreen()}
     */
    boolean isPendingUpdate();

    /**
     * Drains the input queue and passes the key strokes to the GUI system for processing. For window-based system, it
     * will send each key stroke to the active window for processing. If the input read gives an EOF, it will throw
     * EOFException and this is normally the signal to shut down the GUI (any command coming in before the EOF will be
     * processed as usual before this).
     *
     * @return {@code true} if at least one key stroke was read and processed, {@code false} if there was nothing on the
     * input queue (only for non-blocking IO)
     * @throws java.io.IOException  In case there was an underlying I/O error
     * @throws java.io.EOFException In the input stream received an EOF marker
     */
    boolean processInput() throws IOException;

    /**
     * Removes keyStrokeListener
     */
    void removeKeyStrokeListener();

    /**
     * Adds a listener to this TextGUI to fire events on.
     *
     * @param keyStrokeListener Listener to add
     */
    Frame setKeyStrokeListener(KeyStrokeListener keyStrokeListener);

    /**
     * Updates the screen, to make any changes visible to the user.
     *
     * @throws java.io.IOException In case there was an underlying I/O error
     */
    void updateScreen() throws IOException;

}

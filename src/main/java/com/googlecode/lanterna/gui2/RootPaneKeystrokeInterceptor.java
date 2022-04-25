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

import com.googlecode.lanterna.input.KeyStroke;

/**
 * Base listener interface having callback methods for events relating to {@link RootPane} (and {@link Window}, which
 * extends {@link RootPane}) so that you can be notified by a callback when certain events happen. Assume it is the GUI
 * thread that will call these methods. You typically use this through {@link WindowMoveListener} and calling
 * {@link Window#addWindowListener(WindowMoveListener)}
 */
public interface RootPaneKeystrokeInterceptor {
    /**
     * Called when a user entered some input which wasn't handled by the focused component. This allows you to catch it
     * at a {@link RootPane} (or {@link Window}) level and prevent it from being reported to the {@link Frame} as an
     * unhandled input event.
     *
     * @param keyStroke The unhandled input event
     * @param rootPane  {@link RootPane} that got the input event
     */
    default boolean onAfterKeyStroke(KeyStroke keyStroke, boolean handled, RootPane rootPane) {
        return false;
    }

    /**
     * Called when a user input is about to be delivered to the focused {@link Interactable} inside the
     * {@link RootPane}, but before it is actually delivered. You can catch it and prevent it from being passed into
     * the component by returning true.
     *
     * @param keyStroke The actual input event
     * @param rootPane  Base pane that got the input event
     * @return intercepted
     */
    default boolean onBeforeKeyStroke(KeyStroke keyStroke, RootPane rootPane) {
        return false;
    }
}

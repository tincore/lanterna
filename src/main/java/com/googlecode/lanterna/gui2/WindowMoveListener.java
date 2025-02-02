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
import com.googlecode.lanterna.Point;

/**
 * Extended {@link RootPaneKeystrokeInterceptor} for {@link Window} that exposes additional events that are specific to windows
 */
public interface WindowMoveListener {
    /**
     * Called whenever the window's position has changed, no matter if it was done by the window manager or the user
     *
     * @param window   Window that was repositioned
     * @param oldPoint Previous position of the window
     * @param newPoint New position of the window
     */
    void onMoved(Window window, Point oldPoint, Point newPoint);

    /**
     * Called whenever the window's size has changed, no matter if it was done by the window manager or the user
     *
     * @param window  Window that was resized
     * @param oldSize Previous size of the window
     * @param newSize New size of the window
     */
    void onResized(Window window, Dimension oldSize, Dimension newSize);
}

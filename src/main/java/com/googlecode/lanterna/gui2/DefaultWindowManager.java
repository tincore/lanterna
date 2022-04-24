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

import java.util.List;

/**
 * The default window manager implementation used by Lanterna. New windows will be generally added in a tiled manner,
 * starting in the top-left corner and moving down-right as new windows are added. By using the various window hints
 * that are available you have some control over how the window manager will place and size the windows.
 *
 * @author Martin
 */
public class DefaultWindowManager implements WindowManager {

    private final WindowDecorationRenderer windowDecorationRendererOverride;
    private Dimension lastKnownScreenSize;

    /**
     * Default constructor, will create a window manager that uses {@code DefaultWindowDecorationRenderer} for drawing
     * window decorations, unless the current theme has an override. Any size calculations done before the text GUI has
     * actually been started and displayed on the terminal will assume the terminal size is 80x24.
     */
    public DefaultWindowManager() {
        this(null);
    }

    /**
     * Creates a new {@code DefaultWindowManager} using a {@code DefaultWindowDecorationRenderer} for drawing window
     * decorations, unless the current theme has an override. Any size calculations done before the text GUI has
     * actually been started and displayed on the terminal will use the size passed in with the
     * {@code initialScreenSize} parameter (if {@code null} then size will be assumed to be 80x24)
     *
     * @param initialScreenSize Size to assume the terminal has until the text GUI is started and can be notified of the
     *                          correct size
     */
    public DefaultWindowManager(Dimension initialScreenSize) {
        this(null, initialScreenSize);
    }

    /**
     * Creates a new {@code DefaultWindowManager} using a specified {@code windowDecorationRendererOverride} for drawing window
     * decorations. Any size calculations done before the text GUI has actually been started and displayed on the
     * terminal will use the size passed in with the {@code initialScreenSize} parameter
     *
     * @param windowDecorationRenderer Window decoration renderer to use when drawing windows
     * @param initialScreenSize Size to assume the terminal has until the text GUI is started and can be notified of the
     *                          correct size
     */
    public DefaultWindowManager(WindowDecorationRenderer windowDecorationRenderer, Dimension initialScreenSize) {
        this.windowDecorationRendererOverride = windowDecorationRenderer;
        if(initialScreenSize != null) {
            this.lastKnownScreenSize = initialScreenSize;
        }
        else {
            this.lastKnownScreenSize = new Dimension(80, 24);
        }
    }

    @Override
    public boolean isInvalid() {
        return false;
    }

    @Override
    public WindowDecorationRenderer getWindowDecorationRenderer(Window window) {
        if(window.isHint(Window.Hint.NO_DECORATIONS)) {
            return new EmptyWindowDecorationRenderer();
        }
        else if(windowDecorationRendererOverride != null) {
            return windowDecorationRendererOverride;
        }
        else if(window.getTheme() != null && window.getTheme().getWindowDecorationRenderer() != null) {
            return window.getTheme().getWindowDecorationRenderer();
        }
        else {
            return new DefaultWindowDecorationRenderer();
        }
    }

    @Override
    public void onAdded(WindowBasedTextGUI textGUI, Window window, List<Window> allWindows) {
        WindowDecorationRenderer decorationRenderer = getWindowDecorationRenderer(window);
        Dimension expectedDecoratedSize = decorationRenderer.getDecoratedSize(window, window.getPreferredSize());
        window.setDecoratedSize(expectedDecoratedSize);

        //noinspection StatementWithEmptyBody
        if(window.isHint(Window.Hint.FIXED_POSITION)) {
            //Don't place the window, assume the position is already set
        }
        else if(allWindows.isEmpty()) {
            window.setPosition(Point.OFFSET_1x1);
        }
        else if(window.isHint(Window.Hint.CENTERED)) {
            int left = (lastKnownScreenSize.getColumns() - expectedDecoratedSize.getColumns()) / 2;
            int top = (lastKnownScreenSize.getRows() - expectedDecoratedSize.getRows()) / 2;
            window.setPosition(new Point(left, top));
        }
        else {
            Point nextPoint = allWindows.get(allWindows.size() - 1).getPosition().withRelative(2, 1);
            if(nextPoint.getColumn() + expectedDecoratedSize.getColumns() > lastKnownScreenSize.getColumns() ||
                    nextPoint.getRow() + expectedDecoratedSize.getRows() > lastKnownScreenSize.getRows()) {
                nextPoint = Point.OFFSET_1x1;
            }
            window.setPosition(nextPoint);
        }

        // Finally, run through the usual calculations so the window manager's usual prepare method can have it's say
        prepareWindow(lastKnownScreenSize, window);
    }

    @Override
    public void onRemoved(WindowBasedTextGUI textGUI, Window window, List<Window> allWindows) {
        //NOP
    }

    @Override
    public void prepareWindows(WindowBasedTextGUI textGUI, List<Window> allWindows, Dimension screenSize) {
        this.lastKnownScreenSize = screenSize;
        for(Window window: allWindows) {
            prepareWindow(screenSize, window);
        }
    }

    /**
     * Called by {@link DefaultWindowManager} when iterating through all windows to decide their size and position. If
     * you override {@link DefaultWindowManager} to add your own logic to how windows are placed on the screen, you can
     * override this method and selectively choose which window to interfere with. Note that the two key properties that
     * are read by the GUI system after preparing all windows are the position and decorated size. Your custom
     * implementation should set these two fields directly on the window. You can infer the decorated size from the
     * content size by using the window decoration renderer that is attached to the window manager.
     *
     * @param screenSize Size of the terminal that is available to draw on
     * @param window Window to prepare decorated size and position for
     */
    protected void prepareWindow(Dimension screenSize, Window window) {
        Dimension contentAreaSize;
        if(window.isHint(Window.Hint.FIXED_SIZE)) {
            contentAreaSize = window.getSize();
        }
        else {
            contentAreaSize = window.getPreferredSize();
        }
        Dimension size = getWindowDecorationRenderer(window).getDecoratedSize(window, contentAreaSize);
        Point point = window.getPosition();

        if(window.isHint(Window.Hint.FULL_SCREEN)) {
            point = Point.TOP_LEFT_CORNER;
            size = screenSize;
        }
        else if(window.isHint(Window.Hint.EXPANDED)) {
            point = Point.OFFSET_1x1;
            size = screenSize.withRelative(
                    -Math.min(4, screenSize.getColumns()),
                    -Math.min(3, screenSize.getRows()));
            if(!size.equals(window.getDecoratedSize())) {
                window.invalidate();
            }
        }
        else if(window.isHint(Window.Hint.FIT_TERMINAL_WINDOW) ||
            window.isHint(Window.Hint.CENTERED)) {
            //If the window is too big for the terminal, move it up towards 0x0 and if that's not enough then shrink
            //it instead
            while(point.getRow() > 0 && point.getRow() + size.getRows() > screenSize.getRows()) {
                point = point.withRelativeRow(-1);
            }
            while(point.getColumn() > 0 && point.getColumn() + size.getColumns() > screenSize.getColumns()) {
                point = point.withRelativeColumn(-1);
            }
            if(point.getRow() + size.getRows() > screenSize.getRows()) {
                size = size.withRows(screenSize.getRows() - point.getRow());
            }
            if(point.getColumn() + size.getColumns() > screenSize.getColumns()) {
                size = size.withColumns(screenSize.getColumns() - point.getColumn());
            }
            if(window.isHint(Window.Hint.CENTERED)) {
                int left = (lastKnownScreenSize.getColumns() - size.getColumns()) / 2;
                int top = (lastKnownScreenSize.getRows() - size.getRows()) / 2;
                point = new Point(left, top);
            }
        }

        window.setPosition(point);
        window.setDecoratedSize(size);
    }

}

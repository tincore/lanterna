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
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Abstract Window has most of the code requiring for a window to function, all concrete window implementations extends
 * from this in one way or another. You can define your own window by extending from this, as an alternative to building
 * up the GUI externally by constructing a {@code BasicWindow} and adding components to it.
 *
 * @author Martin
 */
public abstract class AbstractWindow extends AbstractRootPane<Window> implements Window {
    private final CopyOnWriteArrayList<WindowMoveListener> windowMoveListeners = new CopyOnWriteArrayList<>();
    private String title;
    private boolean visible = true;
    private Dimension lastKnownSize;
    private Dimension lastKnownDecoratedSize;
    private Point lastKnownPoint;
    private Point contentOffset = Point.TOP_LEFT_CORNER;
    private Set<Hint> hints = new HashSet<>();
    private boolean onKeyEscapeClose;
    private WindowPostRenderer windowPostRenderer;
    private WindowFrame textGUI;

    public AbstractWindow(Attributes attributes) {
        this("", attributes);
    }

    /**
     * Creates a window with a specific title that will (probably) be drawn in the window decorations
     *
     * @param title Title of this window
     */
    public AbstractWindow(String title, Attributes attributes) {
        super(attributes);
        this.title = title;
    }

    @Override
    public Window addHints(Hint... hints) {
        return addHints(List.of(hints));
    }

    @Override
    public Window addHints(Collection<Hint> hints) {
        this.hints.addAll(hints);
        invalidate();
        return self();
    }


    @Override
    public void addWindowListener(WindowMoveListener windowMoveListener) {
        windowMoveListeners.add(windowMoveListener);
    }

    @Override
    public void close() {
        if (textGUI != null) {
            textGUI.removeWindow(this);
        }
        setComponent(null);
    }

    @Override
    public void draw(TextGUIGraphics graphics) {
        if (!graphics.getSize().equals(lastKnownSize)) {
            getComponent().invalidate();
        }
        setSize(graphics.getSize(), false);
        super.draw(graphics);
    }

    /**
     * @see Window#fromGlobalToContentRelative(Point)
     */
    @Override
    @Deprecated
    public Point fromGlobal(Point globalPoint) {
        return fromGlobalToContentRelative(globalPoint);
    }

    @Override
    public Point fromGlobalToContentRelative(Point globalPoint) {
        if (globalPoint == null || lastKnownPoint == null) {
            return null;
        }
        return globalPoint.withRelative(
            -lastKnownPoint.getColumn() - contentOffset.getColumn(),
            -lastKnownPoint.getRow() - contentOffset.getRow());
    }

    @Override
    public Point fromGlobalToDecoratedRelative(Point globalPoint) {
        if (globalPoint == null || lastKnownPoint == null) {
            return null;
        }
        return globalPoint.withRelative(-lastKnownPoint.getColumn(), -lastKnownPoint.getRow());
    }

    @Override
    public final Dimension getDecoratedSize() {
        return lastKnownDecoratedSize;
    }

    @Override
    public final void setDecoratedSize(Dimension decoratedSize) {
        this.lastKnownDecoratedSize = decoratedSize;
    }

    @Override
    public final Point getPosition() {
        return lastKnownPoint;
    }

    @Override
    public WindowPostRenderer getPostRenderer() {
        return windowPostRenderer;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension preferredSize = contentHolder.getPreferredSize();
        MenuBar menuBar = getMenuBar();
        if (menuBar.getMenuCount() > 0) {
            Dimension menuPreferredSize = menuBar.getPreferredSize();
            preferredSize = preferredSize.withRelativeRows(menuPreferredSize.getRows())
                .withColumns(Math.max(menuPreferredSize.getColumns(), preferredSize.getColumns()));
        }
        return preferredSize;
    }

    @Override
    public final Dimension getSize() {
        return lastKnownSize;
    }

    @Override
    public WindowFrame getTextGUI() {
        return textGUI;
    }

    @Override
    public void setTextGUI(WindowFrame textGUI) {
        //This is kind of stupid check, but might cause it to blow up on people using the library incorrectly instead of
        //just causing weird behaviour
        if (this.textGUI != null && textGUI != null) {
            throw new UnsupportedOperationException("Are you calling setTextGUI yourself? Please read the documentation"
                + " in that case (this could also be a bug in Lanterna, please report it if you are sure you are "
                + "not calling Window.setTextGUI(..) from your code)");
        }
        this.textGUI = textGUI;
    }

    @Override
    public String getTitle() {
        return title;
    }

    /**
     * Alters the title of the window to the supplied string
     *
     * @param title New title of the window
     */
    public void setTitle(String title) {
        this.title = title;
        invalidate();
    }

    @Override
    public boolean isHint(Hint hint) {
        return this.hints.contains(hint);
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public boolean onInput(KeyStroke keyStroke) {
        boolean handled = super.onInput(keyStroke);
        // Should add as listener
        if (!handled && onKeyEscapeClose && keyStroke.isKeyType(KeyType.Escape)) {
            close();
            return true;
        }
        return handled;
    }

    @Override
    public void removeWindowListener(WindowMoveListener windowMoveListener) {
        windowMoveListeners.remove(windowMoveListener);
    }

    Window self() {
        return this;
    }

    @Override
    public Window setContentOffset(Point offset) {
        this.contentOffset = offset;
        return this;
    }

    @Override
    public Window setDraggable() {
        /**
         * In order for window to be draggable, it would no longer be CENTERED.
         * Removes Hint.CENTERED, adds Hint.FIXED_POSITION to the window hints.
         */
        Set<Hint> hints = new HashSet<>(this.hints);
        hints.remove(Hint.CENTERED);
        hints.add(Hint.FIXED_POSITION);
        setHints(hints);
        return this;
    }

    @Override
    public Window setFixedSize(Dimension size) {
        hints.add(Hint.FIXED_SIZE);
        return setSize(size);
    }

    @Override
    public Window setHints(Collection<Hint> hints) {
        this.hints = new HashSet<>(hints);
        invalidate();
        return self();
    }

    @Override
    public Window setHints(Hint... hints) {
        return setHints(List.of(hints));
    }

    /**
     * Setting this property to {@code true} will cause pressing the ESC key to close the window. This used to be the
     * default behaviour of lanterna 3 during the development cycle but is not longer the case. You are encouraged to
     * put proper buttons or other kind of components to clearly mark to the user how to close the window instead of
     * magically taking ESC, but sometimes it can be useful (when doing testing, for example) to enable this mode.
     *
     * @param onKeyEscapeClose If {@code true}, this window will self-close if you press ESC key
     */
    public Window setOnKeyEscapeClose(boolean onKeyEscapeClose) {
        this.onKeyEscapeClose = onKeyEscapeClose;
        return this;
    }

    @Override
    public final Window setPosition(Point topLeft) {
        Point oldPoint = this.lastKnownPoint;
        this.lastKnownPoint = topLeft;
        windowMoveListeners.forEach(l -> l.onMoved(this, oldPoint, topLeft));
        return this;
    }

    @Override
    @Deprecated
    public Window setSize(Dimension size) {
        setSize(size, true);
        return this;
    }

    private void setSize(Dimension size, boolean invalidate) {
        Dimension oldSize = this.lastKnownSize;
        this.lastKnownSize = size;
        if (invalidate) {
            invalidate();
        }

        windowMoveListeners.forEach(l -> l.onResized(this, oldSize, size));
    }

    @Override
    public Window setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    /**
     * Sets the post-renderer to use for this window. This will override the default from the GUI system (if there is
     * one set, otherwise from the theme).
     *
     * @param windowPostRenderer Window post-renderer to assign to this window
     */
    public Window setWindowPostRenderer(WindowPostRenderer windowPostRenderer) {
        this.windowPostRenderer = windowPostRenderer;
        return this;
    }

    /**
     * @see Window#toGlobalFromContentRelative(Point)
     */
    @Override
    @Deprecated
    public Point toGlobal(Point localPoint) {
        return toGlobalFromContentRelative(localPoint);
    }

    @Override
    public Point toGlobalFromContentRelative(Point contentLocalPoint) {
        if (contentLocalPoint == null) {
            return null;
        }
        return lastKnownPoint.withRelative(contentOffset.withRelative(contentLocalPoint));
    }

    @Override
    @Deprecated
    public Point toGlobalFromDecoratedRelative(Point localPoint) {
        if (localPoint == null) {
            return null;
        }
        return lastKnownPoint.withRelative(localPoint);
    }

    @Override
    public void waitUntilClosed() {
        WindowFrame textGUI = getTextGUI();
        if (textGUI != null) {
            textGUI.waitForWindowToClose(this);
        }
    }
}

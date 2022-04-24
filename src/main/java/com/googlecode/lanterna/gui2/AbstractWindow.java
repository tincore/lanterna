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
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Abstract Window has most of the code requiring for a window to function, all concrete window implementations extends
 * from this in one way or another. You can define your own window by extending from this, as an alternative to building
 * up the GUI externally by constructing a {@code BasicWindow} and adding components to it.
 *
 * @author Martin
 */
public abstract class AbstractWindow extends AbstractBasePane<Window> implements Window {
    private String title;
    private boolean visible = true;
    private TerminalSize lastKnownSize;
    private TerminalSize lastKnownDecoratedSize;
    private TerminalPosition lastKnownPosition;
    private TerminalPosition contentOffset = TerminalPosition.TOP_LEFT_CORNER;
    private Set<Hint> hints = new HashSet<>();
    private boolean onKeyEscapeClose;

    private WindowPostRenderer windowPostRenderer;
    private WindowBasedTextGUI textGUI;

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
    public void addWindowListener(WindowListener windowListener) {
        addBasePaneListener(windowListener);
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
     * @see Window#fromGlobalToContentRelative(TerminalPosition)
     */
    @Override
    @Deprecated
    public TerminalPosition fromGlobal(TerminalPosition globalPosition) {
        return fromGlobalToContentRelative(globalPosition);
    }

    @Override
    public TerminalPosition fromGlobalToContentRelative(TerminalPosition globalPosition) {
        if (globalPosition == null || lastKnownPosition == null) {
            return null;
        }
        return globalPosition.withRelative(
            -lastKnownPosition.getColumn() - contentOffset.getColumn(),
            -lastKnownPosition.getRow() - contentOffset.getRow());
    }

    @Override
    public TerminalPosition fromGlobalToDecoratedRelative(TerminalPosition globalPosition) {
        if (globalPosition == null || lastKnownPosition == null) {
            return null;
        }
        return globalPosition.withRelative(-lastKnownPosition.getColumn(), -lastKnownPosition.getRow());
    }

    @Override
    public final TerminalSize getDecoratedSize() {
        return lastKnownDecoratedSize;
    }

    @Override
    public final void setDecoratedSize(TerminalSize decoratedSize) {
        this.lastKnownDecoratedSize = decoratedSize;
    }

    @Override
    public final TerminalPosition getPosition() {
        return lastKnownPosition;
    }

    @Override
    public WindowPostRenderer getPostRenderer() {
        return windowPostRenderer;
    }

    @Override
    public TerminalSize getPreferredSize() {
        TerminalSize preferredSize = contentHolder.getPreferredSize();
        MenuBar menuBar = getMenuBar();
        if (menuBar.getMenuCount() > 0) {
            TerminalSize menuPreferredSize = menuBar.getPreferredSize();
            preferredSize = preferredSize.withRelativeRows(menuPreferredSize.getRows())
                .withColumns(Math.max(menuPreferredSize.getColumns(), preferredSize.getColumns()));
        }
        return preferredSize;
    }

    @Override
    public final TerminalSize getSize() {
        return lastKnownSize;
    }

    @Override
    public WindowBasedTextGUI getTextGUI() {
        return textGUI;
    }

    @Override
    public void setTextGUI(WindowBasedTextGUI textGUI) {
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
    public boolean handleInput(KeyStroke key) {
        boolean handled = super.handleInput(key);
        if (!handled && onKeyEscapeClose && key.getKeyType() == KeyType.Escape) {
            close();
            return true;
        }
        return handled;
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
    public void removeWindowListener(WindowListener windowListener) {
        removeBasePaneListener(windowListener);
    }

    Window self() {
        return this;
    }

    @Override
    public Window setContentOffset(TerminalPosition offset) {
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
    public Window setFixedSize(TerminalSize size) {
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
    public final Window setPosition(TerminalPosition topLeft) {
        TerminalPosition oldPosition = this.lastKnownPosition;
        this.lastKnownPosition = topLeft;

        getBasePaneListeners().stream()
            .filter(l -> l instanceof WindowListener)
            .forEach(l -> ((WindowListener) l).onMoved(this, oldPosition, topLeft));

        return this;
    }

    @Override
    @Deprecated
    public Window setSize(TerminalSize size) {
        setSize(size, true);
        return this;
    }

    private void setSize(TerminalSize size, boolean invalidate) {
        TerminalSize oldSize = this.lastKnownSize;
        this.lastKnownSize = size;
        if (invalidate) {
            invalidate();
        }

        // Fire listeners
        for (BasePaneListener<?> listener : getBasePaneListeners()) {
            if (listener instanceof WindowListener) {
                ((WindowListener) listener).onResized(this, oldSize, size);
            }
        }
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
     * @see Window#toGlobalFromContentRelative(TerminalPosition)
     */
    @Override
    @Deprecated
    public TerminalPosition toGlobal(TerminalPosition localPosition) {
        return toGlobalFromContentRelative(localPosition);
    }

    @Override
    public TerminalPosition toGlobalFromContentRelative(TerminalPosition contentLocalPosition) {
        if (contentLocalPosition == null) {
            return null;
        }
        return lastKnownPosition.withRelative(contentOffset.withRelative(contentLocalPosition));
    }

    @Override
    @Deprecated
    public TerminalPosition toGlobalFromDecoratedRelative(TerminalPosition localPosition) {
        if (localPosition == null) {
            return null;
        }
        return lastKnownPosition.withRelative(localPosition);
    }

    @Override
    public void waitUntilClosed() {
        WindowBasedTextGUI textGUI = getTextGUI();
        if (textGUI != null) {
            textGUI.waitForWindowToClose(this);
        }
    }
}

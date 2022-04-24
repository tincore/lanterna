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
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.gui2.Interactable.KeyStrokeResult;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.input.MouseAction;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractRootPane<T extends RootPane> implements RootPane {

    protected final ContentHolder contentHolder;
    private final Attributes attributes;

    private final CopyOnWriteArrayList<RootPaneListener<T>> rootPaneListeners;
    protected InteractableLookupMap interactableLookupMap;
    private Interactable focusedInteractable;
    private boolean invalid;
    private boolean strictFocusChange;
    private boolean enableDirectionBasedMovements;
    private Theme theme;

    private Interactable mouseDownForDrag = null;


    protected AbstractRootPane(Attributes attributes) {
        this.attributes = attributes;
        this.contentHolder = new ContentHolder();
        this.rootPaneListeners = new CopyOnWriteArrayList<>();
        this.interactableLookupMap = new InteractableLookupMap(new Dimension(80, 25));
        this.invalid = false;
        this.strictFocusChange = false;
        this.enableDirectionBasedMovements = true;
        this.theme = null;
    }

    public void addRootPaneListener(RootPaneListener<T> rootPaneListener) {
        rootPaneListeners.addIfAbsent(rootPaneListener);
    }

    private boolean doHandleInput(KeyStroke keyStroke) {
        boolean result = false;
        if (keyStroke.getKeyType() == KeyType.MouseEvent) {
            return handleMouseInput((MouseAction) keyStroke);
        }
        Interactable.FocusChangeDirection direction = Interactable.FocusChangeDirection.TELEPORT; // Default
        Interactable nextFocus = null;
        if (focusedInteractable == null) {
            // If nothing is focused and the user presses certain navigation keys, try to find if there is an
            // Interactable component we can move focus to.
            MenuBar menuBar = getMenuBar();
            Component baseComponent = getComponent();
            switch (keyStroke.getKeyType()) {
                case Tab:
                case ArrowRight:
                case ArrowDown:
                    direction = Interactable.FocusChangeDirection.NEXT;
                    // First try the menu, then the actual component
                    nextFocus = menuBar.nextFocus(null);
                    if (nextFocus == null) {
                        if (baseComponent instanceof Container) {
                            nextFocus = ((Container) baseComponent).nextFocus(null);
                        } else if (baseComponent instanceof Interactable) {
                            nextFocus = (Interactable) baseComponent;
                        }
                    }
                    break;

                case ReverseTab:
                case ArrowUp:
                case ArrowLeft:
                    direction = Interactable.FocusChangeDirection.PREVIOUS;
                    if (baseComponent instanceof Container) {
                        nextFocus = ((Container) baseComponent).previousFocus(null);
                    } else if (baseComponent instanceof Interactable) {
                        nextFocus = (Interactable) baseComponent;
                    }
                    // If no component can take focus, try the menu
                    if (nextFocus == null) {
                        nextFocus = menuBar.previousFocus(null);
                    }
                    break;
            }
            if (nextFocus != null) {
                setFocusedInteractable(nextFocus, direction);
                result = true;
            }
        } else {
            KeyStrokeResult handleKeyStrokeResult = focusedInteractable.onInput(keyStroke);
            if (!enableDirectionBasedMovements) {
                if (handleKeyStrokeResult == Interactable.KeyStrokeResult.MOVE_FOCUS_DOWN || handleKeyStrokeResult == Interactable.KeyStrokeResult.MOVE_FOCUS_RIGHT) {
                    handleKeyStrokeResult = Interactable.KeyStrokeResult.MOVE_FOCUS_NEXT;
                } else if (handleKeyStrokeResult == Interactable.KeyStrokeResult.MOVE_FOCUS_UP || handleKeyStrokeResult == Interactable.KeyStrokeResult.MOVE_FOCUS_LEFT) {
                    handleKeyStrokeResult = Interactable.KeyStrokeResult.MOVE_FOCUS_PREVIOUS;
                }
            }
            switch (handleKeyStrokeResult) {
                case HANDLED:
                    result = true;
                    break;
                case UNHANDLED:
                    //Filter the event recursively through all parent containers until we hit null; give the containers
                    //a chance to absorb the event
                    Container parent = focusedInteractable.getParent();
                    while (parent != null) {
                        if (parent.handleInput(keyStroke)) {
                            return true;
                        }
                        parent = parent.getParent();
                    }
                    result = false;
                    break;
                case MOVE_FOCUS_NEXT:
                    nextFocus = contentHolder.nextFocus(focusedInteractable);
                    if (nextFocus == null) {
                        nextFocus = contentHolder.nextFocus(null);
                    }
                    direction = Interactable.FocusChangeDirection.NEXT;
                    break;
                case MOVE_FOCUS_PREVIOUS:
                    nextFocus = contentHolder.previousFocus(focusedInteractable);
                    if (nextFocus == null) {
                        nextFocus = contentHolder.previousFocus(null);
                    }
                    direction = Interactable.FocusChangeDirection.PREVIOUS;
                    break;
                case MOVE_FOCUS_DOWN:
                    nextFocus = interactableLookupMap.findNextDown(focusedInteractable);
                    direction = Interactable.FocusChangeDirection.DOWN;
                    if (nextFocus == null && !strictFocusChange) {
                        nextFocus = contentHolder.nextFocus(focusedInteractable);
                        direction = Interactable.FocusChangeDirection.NEXT;
                    }
                    break;
                case MOVE_FOCUS_LEFT:
                    nextFocus = interactableLookupMap.findNextLeft(focusedInteractable);
                    direction = Interactable.FocusChangeDirection.LEFT;
                    break;
                case MOVE_FOCUS_RIGHT:
                    nextFocus = interactableLookupMap.findNextRight(focusedInteractable);
                    direction = Interactable.FocusChangeDirection.RIGHT;
                    break;
                case MOVE_FOCUS_UP:
                    nextFocus = interactableLookupMap.findNextUp(focusedInteractable);
                    direction = Interactable.FocusChangeDirection.UP;
                    if (nextFocus == null && !strictFocusChange) {
                        nextFocus = contentHolder.previousFocus(focusedInteractable);
                        direction = Interactable.FocusChangeDirection.PREVIOUS;
                    }
                    break;
            }
        }
        if (nextFocus != null) {
            setFocusedInteractable(nextFocus, direction);
            result = true;
        }
        return result;
    }

    @Override
    public void draw(TextGUIGraphics graphics) {
        graphics.applyThemeStyle(getTheme().getDefinition(Window.class).getNormal());
        graphics.fill(' ');

        if (!interactableLookupMap.getSize().equals(graphics.getSize())) {
            interactableLookupMap = new InteractableLookupMap(graphics.getSize());
        } else {
            interactableLookupMap.reset();
        }

        contentHolder.draw(graphics);
        contentHolder.updateLookupMap(interactableLookupMap);
        //interactableLookupMap.debug();
        invalid = false;
    }

    protected List<RootPaneListener<T>> getBasePaneListeners() {
        return rootPaneListeners;
    }

    @Override
    public Component getComponent() {
        return contentHolder.getComponent();
    }

    @Override
    public Point getCursorPosition() {
        if (focusedInteractable == null) {
            return null;
        }
        Point point = focusedInteractable.getCursorLocation();
        if (point == null) {
            return null;
        }
        //Don't allow the component to set the cursor outside of its own boundaries
        if (point.getColumn() < 0 ||
            point.getRow() < 0 ||
            point.getColumn() >= focusedInteractable.getSize().getColumns() ||
            point.getRow() >= focusedInteractable.getSize().getRows()) {
            return null;
        }
        return focusedInteractable.toBasePane(point);
    }

    @Override
    public Interactable getFocusedInteractable() {
        return focusedInteractable;
    }

    @Override
    public void setFocusedInteractable(Interactable toFocus) {
        setFocusedInteractable(toFocus,
            toFocus != null ?
                Interactable.FocusChangeDirection.TELEPORT : Interactable.FocusChangeDirection.RESET);
    }

    @Override
    public MenuBar getMenuBar() {
        return contentHolder.getMenuBar();
    }

    @Override
    public synchronized Theme getTheme() {
        if (theme != null) {
            return theme;
        } else if (getTextGUI() != null) {
            return getTextGUI().getTheme();
        }
        return null;
    }

    @Override
    public boolean handleInput(KeyStroke keyStroke) {
        // Fire events first and decide if the event should be sent to the focused component or not
        AtomicBoolean deliverEvent = new AtomicBoolean(true);
        for (RootPaneListener<T> listener : rootPaneListeners) {
            listener.onInput(self(), keyStroke, deliverEvent);
        }
        if (!deliverEvent.get()) {
            return true;
        }

        // Now try to deliver the event to the focused component
        boolean handled = doHandleInput(keyStroke);

        // If it wasn't handled, fire the listeners and decide what to report to the TextGUI
        if (!handled) {
            AtomicBoolean hasBeenHandled = new AtomicBoolean(false);
            for (RootPaneListener<T> listener : rootPaneListeners) {
                listener.onUnhandledInput(self(), keyStroke, hasBeenHandled);
            }
            handled = hasBeenHandled.get();
        }
        return handled;
    }

    private boolean handleMouseInput(MouseAction mouseAction) {
        Point localCoordinates = fromGlobal(mouseAction.getPosition());
        if (localCoordinates == null) {
            return false;
        }
        Interactable interactable = interactableLookupMap.getInteractableAt(localCoordinates);
        if (mouseAction.isMouseDown()) {
            mouseDownForDrag = interactable;
        }
        Interactable wasMouseDownForDrag = mouseDownForDrag;
        if (mouseAction.isMouseUp()) {
            mouseDownForDrag = null;
        }
        if (mouseAction.isMouseDrag() && mouseDownForDrag != null) {
            return mouseDownForDrag.onInput(mouseAction) == Interactable.KeyStrokeResult.HANDLED;
        }
        if (interactable == null) {
            return false;
        }
        if (mouseAction.isMouseUp()) {
            // MouseUp only handled by same interactable as MouseDown
            if (wasMouseDownForDrag == interactable) {
                return interactable.onInput(mouseAction) == KeyStrokeResult.HANDLED;
            }
            // did not handleInput because mouse up was not on component mouse down was on
            return false;
        }
        return interactable.onInput(mouseAction) == Interactable.KeyStrokeResult.HANDLED;
    }

    @Override
    public void invalidate() {
        invalid = true;

        //Propagate
        contentHolder.invalidate();
    }

    @Override
    public boolean isInvalid() {
        return invalid || contentHolder.isInvalid();
    }

    protected void removeBasePaneListener(RootPaneListener<T> rootPaneListener) {
        rootPaneListeners.remove(rootPaneListener);
    }

    abstract T self();

    @Override
    public T setComponent(Component component) {
        contentHolder.setComponent(component);
        return self();
    }

    @Override
    public void setEnableDirectionBasedMovements(boolean enableDirectionBasedMovements) {
        this.enableDirectionBasedMovements = enableDirectionBasedMovements;
    }

    protected void setFocusedInteractable(Interactable toFocus, Interactable.FocusChangeDirection direction) {
        if (focusedInteractable == toFocus) {
            return;
        }
        if (toFocus != null && !toFocus.isEnabled()) {
            return;
        }
        if (focusedInteractable != null) {
            focusedInteractable.onFocusLost(direction, focusedInteractable);
        }
        Interactable previous = focusedInteractable;
        focusedInteractable = toFocus;
        if (toFocus != null) {
            toFocus.onFocusGain(direction, previous);
        }
        invalidate();
    }

    @Override
    public T setMenuBar(MenuBar menuBar) {
        contentHolder.setMenuBar(menuBar);
        return self();
    }

    @Override
    public void setStrictFocusChange(boolean strictFocusChange) {
        this.strictFocusChange = strictFocusChange;
    }

    @Override
    public synchronized T setTheme(Theme theme) {
        this.theme = theme;
        invalidate();
        return self();
    }

    private static class EmptyMenuBar extends MenuBar {
        @Override
        public boolean isEmptyMenuBar() {
            return true;
        }

        @Override
        public boolean isInvalid() {
            return false;
        }

        @Override
        public synchronized void onAdded(Container container) {
        }

        @Override
        public synchronized void onRemoved(Container container) {
        }
    }

    protected class ContentHolder extends AbstractComposite<Container> {
        private MenuBar menuBar;

        ContentHolder() {
            super(attributes);
            this.menuBar = new EmptyMenuBar();
        }

        @Override
        protected ComponentRenderer<Container> createDefaultRenderer() {
            return new ComponentRenderer<>() {
                @Override
                public void drawComponent(TextGUIGraphics graphics, Container component) {
                    if (!(menuBar instanceof EmptyMenuBar)) {
                        int menuBarHeight = menuBar.getPreferredSize().getRows();
                        TextGUIGraphics menuGraphics = graphics.newTextGraphics(Point.TOP_LEFT_CORNER, graphics.getSize().withRows(menuBarHeight));
                        menuBar.draw(menuGraphics);
                        graphics = graphics.newTextGraphics(Point.TOP_LEFT_CORNER.withRelativeRow(menuBarHeight), graphics.getSize().withRelativeRows(-menuBarHeight));
                    }

                    Component subComponent = getComponent();
                    if (subComponent == null) {
                        return;
                    }
                    subComponent.draw(graphics);
                }

                @Override
                public Dimension getPreferredSize(Container component) {
                    Component subComponent = getComponent();
                    if (subComponent == null) {
                        return Dimension.ZERO;
                    }
                    return subComponent.getPreferredSize();
                }
            };
        }

        private MenuBar getMenuBar() {
            return menuBar;
        }

        private void setMenuBar(MenuBar menuBar) {
            if (menuBar == null) {
                menuBar = new EmptyMenuBar();
            }

            if (this.menuBar != menuBar) {
                menuBar.onAdded(this);
                this.menuBar.onRemoved(this);
                this.menuBar = menuBar;
                if (focusedInteractable == null) {
                    setFocusedInteractable(menuBar.nextFocus(null));
                }
                invalidate();
            }
        }

        @Override
        public RootPane getRootPane() {
            return AbstractRootPane.this;
        }

        @Override
        public TextGUI getTextGUI() {
            return AbstractRootPane.this.getTextGUI();
        }

        @Override
        public void invalidate() {
            super.invalidate();
            menuBar.invalidate();
        }

        @Override
        public boolean isInvalid() {
            return super.isInvalid() || menuBar.isInvalid();
        }

        public boolean remove(Component component) {
            boolean removed = super.remove(component);
            if (removed) {
                focusedInteractable = null;
            }
            return removed;
        }

        @Override
        public T setComponent(Component component) {
            if (getComponent() == component) {
                return null;
            }
            setFocusedInteractable(null);
            super.setComponent(component);
            if (focusedInteractable == null && component instanceof Interactable) {
                setFocusedInteractable((Interactable) component);
            } else if (focusedInteractable == null && component instanceof Container) {
                setFocusedInteractable(((Container) component).nextFocus(null));
            }
            return null;
        }

        @Override
        public Point toBasePane(Point point) {
            return point;
        }

        @Override
        public Point toGlobal(Point point) {
            return AbstractRootPane.this.toGlobal(point);
        }

        @Override
        public void updateLookupMap(InteractableLookupMap interactableLookupMap) {
            super.updateLookupMap(interactableLookupMap);
            menuBar.updateLookupMap(interactableLookupMap);
        }
    }
}

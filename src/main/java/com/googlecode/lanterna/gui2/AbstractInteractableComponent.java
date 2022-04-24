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

import com.googlecode.lanterna.Point;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.input.MouseAction;
import com.googlecode.lanterna.input.MouseActionType;

/**
 * Default implementation of Interactable that extends from AbstractComponent. If you want to write your own component
 * that is interactable, i.e. can receive keyboard (and mouse) input, you probably want to extend from this class as
 * it contains some common implementations of the methods from {@code Interactable} interface
 *
 * @param <T> Should always be itself, see {@code AbstractComponent}
 * @author Martin
 */
public abstract class AbstractInteractableComponent<T extends AbstractInteractableComponent<T>> extends AbstractComponent<T> implements Interactable {

    private InputFilter inputFilter;
    private boolean focused;
    private boolean enabled = true;

    private KeyStrokeListener<T> keyStrokeListener = KeyStrokeListener.DUMMY;
    private FocusGainListener focusGainListener = FocusGainListener.DUMMY;
    private FocusLostListener focusLostListener = FocusLostListener.DUMMY;

    public AbstractInteractableComponent(Attributes attributes) {
        super(attributes);
    }

    @Override
    protected abstract InteractableRenderer<T> createDefaultRenderer();

    @Override
    public Point getCursorLocation() {
        return getRenderer().getCursorLocation(self());
    }

    @Override
    public InputFilter getInputFilter() {
        return inputFilter;
    }

    @Override
    public synchronized T setInputFilter(InputFilter inputFilter) {
        this.inputFilter = inputFilter;
        return self();
    }

    @Override
    public InteractableRenderer<T> getRenderer() {
        return (InteractableRenderer<T>) super.getRenderer();
    }

    @Override
    public T grabFocus() {
        if (!isEnabled()) {
            return self();
        }
        RootPane rootPane = getRootPane();
        if (rootPane != null) {
            rootPane.setFocusedInteractable(this);
        }
        return self();
    }

    public boolean isActivationStroke(KeyStroke keyStroke) {
        boolean isKeyboardActivationStroke = isKeyboardActivationStroke(keyStroke);
        boolean isMouseActivationStroke = isMouseActivationStroke(keyStroke);

        return isKeyboardActivationStroke || isMouseActivationStroke;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public synchronized T setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled && isFocused()) {
            RootPane rootPane = getRootPane();
            if (rootPane != null) {
                rootPane.setFocusedInteractable(null);
            }
        }
        return self();
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    public boolean isKeyboardActivationStroke(KeyStroke keyStroke) {
        boolean isKeyboardActivation = (keyStroke.getKeyType() == KeyType.Character && keyStroke.getCharacter() == ' ') || keyStroke.getKeyType() == KeyType.Enter;

        return isFocused() && isKeyboardActivation;
    }

    public boolean isMouseActivationStroke(KeyStroke keyStroke) {
        boolean isMouseActivation = false;
        if (keyStroke instanceof MouseAction) {
            MouseAction action = (MouseAction) keyStroke;
            isMouseActivation = action.getActionType() == MouseActionType.CLICK_DOWN;
        }

        return isMouseActivation;
    }

    public boolean isMouseDown(KeyStroke keyStroke) {
        return keyStroke.isKeyType(KeyType.MouseEvent) && ((MouseAction) keyStroke).isMouseDown();
    }

    public boolean isMouseDrag(KeyStroke keyStroke) {
        return keyStroke.isKeyType(KeyType.MouseEvent) && ((MouseAction) keyStroke).isMouseDrag();
    }

    public boolean isMouseMove(KeyStroke keyStroke) {
        return keyStroke.isKeyType(KeyType.MouseEvent) && ((MouseAction) keyStroke).isMouseMove();
    }

    public boolean isMouseUp(KeyStroke keyStroke) {
        return keyStroke.isKeyType(KeyType.MouseEvent) && ((MouseAction) keyStroke).isMouseUp();
    }

    @Override
    public void onFocusGain(FocusChangeDirection direction, Interactable previouslyInFocus) {
        focused = true;
        focusGainListener.onFocusGain(direction, previouslyInFocus, this);
    }

    @Override
    public void onFocusLost(FocusChangeDirection direction, Interactable nextInFocus) {
        focused = false;
        focusLostListener.onFocusLost(direction, nextInFocus, this);
    }

    @Override
    public synchronized KeyStrokeResult onInput(KeyStroke keyStroke) {
        KeyStrokeResult keyStrokeResult = inputFilter == null || inputFilter.onInput(this, keyStroke) ? onKeyStroke(keyStroke) : KeyStrokeResult.UNHANDLED;
        keyStrokeListener.onKeyStroke(keyStroke, keyStrokeResult != KeyStrokeResult.UNHANDLED, self());
        return keyStrokeResult;
    }

    /**
     * This method can be overridden to handle various user input (mostly from the keyboard) when this component is in
     * focus. The input method from the interface, {@code handleInput(..)} is final in
     * {@code AbstractInteractableComponent} to ensure the input filter is properly handled. If the filter decides that
     * this event should be processed, it will call this method.
     *
     * @param keyStroke What input was entered by the user
     * @return Result of processing the key-stroke
     */
    public KeyStrokeResult onKeyStroke(KeyStroke keyStroke) {
        // Skip the keystroke if ctrl, alt or shift was down
        if (!keyStroke.isAltDown() && !keyStroke.isCtrlDown() && !keyStroke.isShiftDown()) {
            switch (keyStroke.getKeyType()) {
                case ArrowDown:
                    return KeyStrokeResult.MOVE_FOCUS_DOWN;
                case ArrowLeft:
                    return KeyStrokeResult.MOVE_FOCUS_LEFT;
                case ArrowRight:
                    return KeyStrokeResult.MOVE_FOCUS_RIGHT;
                case ArrowUp:
                    return KeyStrokeResult.MOVE_FOCUS_UP;
                case Tab:
                    return KeyStrokeResult.MOVE_FOCUS_NEXT;
                case ReverseTab:
                    return KeyStrokeResult.MOVE_FOCUS_PREVIOUS;
                case MouseEvent:
                    if (isMouseMove(keyStroke)) {
                        // do nothing
                        return KeyStrokeResult.UNHANDLED;
                    }

                    getRootPane().setFocusedInteractable(this);
                    return KeyStrokeResult.HANDLED;
                default:
            }
        }
        return KeyStrokeResult.UNHANDLED;
    }

    public T setFocusGainListener(FocusGainListener focusGainListener) {
        this.focusGainListener = focusGainListener;
        return self();
    }

    public T setFocusLostListener(FocusLostListener focusLostListener) {
        this.focusLostListener = focusLostListener;
        return self();
    }

    public T setKeyStrokeListener(KeyStrokeListener<T> keyStrokeListener) {
        this.keyStrokeListener = keyStrokeListener;
        return self();
    }


}

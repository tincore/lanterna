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
    private boolean enabled;

    private FocusGainListener focusGainListener = FocusGainListener.DUMMY;
    private FocusLostListener focusLostListener = FocusLostListener.DUMMY;

    /**
     * Default constructor
     */
    protected AbstractInteractableComponent(Attributes attributes) {
        super(attributes);
        inputFilter = null;
        focused = false;
        enabled = true;
    }

    @Override
    public T takeFocus() {
        if (!isEnabled()) {
            return self();
        }
        BasePane basePane = getBasePane();
        if (basePane != null) {
            basePane.setFocusedInteractable(this);
        }
        return self();
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
    protected abstract InteractableRenderer<T> createDefaultRenderer();

    @Override
    public InteractableRenderer<T> getRenderer() {
        return (InteractableRenderer<T>) super.getRenderer();
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public synchronized T setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled && isFocused()) {
            BasePane basePane = getBasePane();
            if (basePane != null) {
                basePane.setFocusedInteractable(null);
            }
        }
        return self();
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    @Override
    public final synchronized Result handleInput(KeyStroke keyStroke) {
        if (inputFilter == null || inputFilter.onInput(this, keyStroke)) {
            return onKeyStroke(keyStroke);
        } else {
            return Result.UNHANDLED;
        }
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
    protected Result onKeyStroke(KeyStroke keyStroke) {
        // Skip the keystroke if ctrl, alt or shift was down
        if (!keyStroke.isAltDown() && !keyStroke.isCtrlDown() && !keyStroke.isShiftDown()) {
            switch (keyStroke.getKeyType()) {
                case ArrowDown:
                    return Result.MOVE_FOCUS_DOWN;
                case ArrowLeft:
                    return Result.MOVE_FOCUS_LEFT;
                case ArrowRight:
                    return Result.MOVE_FOCUS_RIGHT;
                case ArrowUp:
                    return Result.MOVE_FOCUS_UP;
                case Tab:
                    return Result.MOVE_FOCUS_NEXT;
                case ReverseTab:
                    return Result.MOVE_FOCUS_PREVIOUS;
                case MouseEvent:
                    if (isMouseMove(keyStroke)) {
                        // do nothing
                        return Result.UNHANDLED;
                    }
                    getBasePane().setFocusedInteractable(this);
                    return Result.HANDLED;
                default:
            }
        }
        return Result.UNHANDLED;
    }


    @Override
    public TerminalPosition getCursorLocation() {
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

    public void setFocusGainListener(FocusGainListener focusGainListener) {
        this.focusGainListener = focusGainListener;
    }

    public void setFocusLostListener(FocusLostListener focusLostListener) {
        this.focusLostListener = focusLostListener;
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

    public boolean isActivationStroke(KeyStroke keyStroke) {
        boolean isKeyboardActivationStroke = isKeyboardActivationStroke(keyStroke);
        boolean isMouseActivationStroke = isMouseActivationStroke(keyStroke);

        return isKeyboardActivationStroke || isMouseActivationStroke;
    }

    public boolean isMouseDown(KeyStroke keyStroke) {
        return keyStroke.getKeyType() == KeyType.MouseEvent && ((MouseAction) keyStroke).isMouseDown();
    }

    public boolean isMouseDrag(KeyStroke keyStroke) {
        return keyStroke.getKeyType() == KeyType.MouseEvent && ((MouseAction) keyStroke).isMouseDrag();
    }

    public boolean isMouseMove(KeyStroke keyStroke) {
        return keyStroke.getKeyType() == KeyType.MouseEvent && ((MouseAction) keyStroke).isMouseMove();
    }

    public boolean isMouseUp(KeyStroke keyStroke) {
        return keyStroke.getKeyType() == KeyType.MouseEvent && ((MouseAction) keyStroke).isMouseUp();
    }


}

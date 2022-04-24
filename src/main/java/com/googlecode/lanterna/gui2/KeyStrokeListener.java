package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.input.KeyStroke;

public interface KeyStrokeListener<T> {
    KeyStrokeListener DUMMY = (k, h, s) -> false;

    /**
     * Fired on keystroke. Listeners should also return {@code true} if the event
     * was processed in any way that requires the calling component to update itself, otherwise {@code false}.
     *
     * @param keyStroke Keystroke that was unhandled
     * @param source    TextGUI that had the event
     * @return If the outcome of this KeyStroke processed by the implementer requires the TextGUI to re-draw, return
     * {@code true} here, otherwise {@code false}
     */
    boolean onKeyStroke(KeyStroke keyStroke, boolean handled, T source);
}

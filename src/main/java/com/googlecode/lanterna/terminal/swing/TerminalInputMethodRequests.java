package com.googlecode.lanterna.terminal.swing;

import java.awt.*;
import java.awt.font.TextHitInfo;
import java.awt.im.InputMethodRequests;
import java.text.AttributedCharacterIterator;

class TerminalInputMethodRequests implements InputMethodRequests {

    private Component owner;
    private GraphicalTerminalImplementation terminalImplementation;
    
    public TerminalInputMethodRequests(Component owner, GraphicalTerminalImplementation terminalImplementation) {
        this.owner = owner;
        this.terminalImplementation = terminalImplementation;
    }
    
    @Override
    public Rectangle getTextLocation(TextHitInfo offset) {
        java.awt.Point location = owner.getLocationOnScreen();
        com.googlecode.lanterna.Point cursorPoint = terminalImplementation.getCursorPosition();

        int offsetX = cursorPoint.getColumn() * terminalImplementation.getFontWidth();
        int offsetY = cursorPoint.getRow() * terminalImplementation.getFontHeight() + terminalImplementation.getFontHeight();

        return new Rectangle(location.x + offsetX, location.y + offsetY, 0, 0);
    }

    @Override
    public TextHitInfo getLocationOffset(int x, int y) {
        return null;
    }

    @Override
    public int getInsertPositionOffset() {
        return 0;
    }

    @Override
    public AttributedCharacterIterator getCommittedText(int beginIndex, int endIndex, AttributedCharacterIterator.Attribute[] attributes) {
        return null;
    }

    @Override
    public int getCommittedTextLength() {
        return 0;
    }

    @Override
    public AttributedCharacterIterator cancelLatestCommittedText(AttributedCharacterIterator.Attribute[] attributes) {
        return null;
    }

    @Override
    public AttributedCharacterIterator getSelectedText(AttributedCharacterIterator.Attribute[] attributes) {
        return null;
    }
}

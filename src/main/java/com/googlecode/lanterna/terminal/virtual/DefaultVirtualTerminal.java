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
package com.googlecode.lanterna.terminal.virtual;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.TabBehaviour;
import com.googlecode.lanterna.terminal.AbstractTerminal;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DefaultVirtualTerminal extends AbstractTerminal implements VirtualTerminal {
    private final TextBuffer regularTextBuffer;
    private final TextBuffer privateModeTextBuffer;
    private final TreeSet<Point> dirtyTerminalCells;
    private final List<VirtualTerminalListener> listeners;

    private TextBuffer currentTextBuffer;
    private boolean wholeBufferDirty;

    private Dimension dimension;
    private boolean cursorVisible;
    private int backlogSize;

    private final BlockingQueue<KeyStroke> inputQueue;
    private final EnumSet<SGR> activeModifiers;
    private TextColor activeForegroundColor;
    private TextColor activeBackgroundColor;

    // Global coordinates, i.e. relative to the top-left corner of the full buffer
    private Point cursorPoint;

    // Used when switching back from private mode, to restore the earlier cursor position
    private Point savedCursorPoint;


    /**
     * Creates a new virtual terminal with an initial size set
     */
    public DefaultVirtualTerminal() {
        this(new Dimension(80, 24));
    }

    /**
     * Creates a new virtual terminal with an initial size set
     * @param initialDimension Starting size of the virtual terminal
     */
    public DefaultVirtualTerminal(Dimension initialDimension) {
        this.regularTextBuffer = new TextBuffer();
        this.privateModeTextBuffer = new TextBuffer();
        this.dirtyTerminalCells = new TreeSet<>();
        this.listeners = new ArrayList<>();

        // Terminal state
        this.inputQueue = new LinkedBlockingQueue<>();
        this.activeModifiers = EnumSet.noneOf(SGR.class);
        this.activeForegroundColor = TextColor.ANSI.DEFAULT;
        this.activeBackgroundColor = TextColor.ANSI.DEFAULT;

        // Start with regular mode
        this.currentTextBuffer = regularTextBuffer;
        this.wholeBufferDirty = false;
        this.dimension = initialDimension;
        this.cursorVisible = true;
        this.cursorPoint = Point.TOP_LEFT_CORNER;
        this.savedCursorPoint = Point.TOP_LEFT_CORNER;
        this.backlogSize = 1000;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Terminal interface methods (and related)
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public synchronized Dimension getTerminalSize() {
        return dimension;
    }

    @Override
    public synchronized void setTerminalSize(Dimension newSize) {
        this.dimension = newSize;
        trimBufferBacklog();
        correctCursor();
        for(VirtualTerminalListener listener: listeners) {
            listener.onResized(this, dimension);
        }
        super.onResized(newSize.getColumns(), newSize.getRows());
    }

    @Override
    public synchronized void enterPrivateMode() {
        currentTextBuffer = privateModeTextBuffer;
        savedCursorPoint = getCursorBufferPosition();
        setCursorPosition(Point.TOP_LEFT_CORNER);
        setWholeBufferDirty();
    }

    @Override
    public synchronized void exitPrivateMode() {
        currentTextBuffer = regularTextBuffer;
        cursorPoint = savedCursorPoint;
        setWholeBufferDirty();
    }

    @Override
    public synchronized void clearScreen() {
        currentTextBuffer.clear();
        setWholeBufferDirty();
        setCursorPosition(Point.TOP_LEFT_CORNER);
    }

    @Override
    public synchronized void setCursorPosition(int x, int y) {
        setCursorPosition(cursorPoint.withColumn(x).withRow(y));
    }

    @Override
    public synchronized void setCursorPosition(Point cursorPoint) {
        if(dimension.getRows() < getBufferLineCount()) {
            cursorPoint = cursorPoint.withRelativeRow(getBufferLineCount() - dimension.getRows());
        }
        this.cursorPoint = cursorPoint;
        correctCursor();
    }

    @Override
    public synchronized Point getCursorPosition() {
        if(getBufferLineCount() <= dimension.getRows()) {
            return getCursorBufferPosition();
        }
        else {
            return cursorPoint.withRelativeRow(-(getBufferLineCount() - dimension.getRows()));
        }
    }

    @Override
    public synchronized Point getCursorBufferPosition() {
        return cursorPoint;
    }

    @Override
    public synchronized void setCursorVisible(boolean visible) {
        this.cursorVisible = visible;
    }

    @Override
    public synchronized void putCharacter(char c)  {
        if(c == '\n') {
            moveCursorToNextLine();
        }
        else if(TerminalTextUtils.isPrintableCharacter(c)) {
            putCharacter(new TextCharacter(c, activeForegroundColor, activeBackgroundColor, activeModifiers));
        }
    }

    @Override
    public synchronized void putString(String string) {
        for (TextCharacter textCharacter: TextCharacter.fromString(string, activeForegroundColor, activeBackgroundColor, activeModifiers)) {
            putCharacter(textCharacter);
        }
    }

    @Override
    public synchronized void enableSGR(SGR sgr) {
        activeModifiers.add(sgr);
    }

    @Override
    public synchronized void disableSGR(SGR sgr) {
        activeModifiers.remove(sgr);
    }

    @Override
    public synchronized void resetColorAndSGR() {
        this.activeModifiers.clear();
        this.activeForegroundColor = TextColor.ANSI.DEFAULT;
        this.activeBackgroundColor = TextColor.ANSI.DEFAULT;
    }

    @Override
    public synchronized void setForegroundColor(TextColor color) {
        this.activeForegroundColor = color;
    }

    @Override
    public synchronized void setBackgroundColor(TextColor color) {
        this.activeBackgroundColor = color;
    }

    @Override
    public synchronized byte[] enquireTerminal(int timeout, TimeUnit timeoutUnit) {
        return getClass().getName().getBytes();
    }

    @Override
    public synchronized void bell() {
        for(VirtualTerminalListener listener: listeners) {
            listener.onBell();
        }
    }

    @Override
    public synchronized void flush() {
        for(VirtualTerminalListener listener: listeners) {
            listener.onFlush();
        }
    }

    @Override
    public void close() {
        for(VirtualTerminalListener listener: listeners) {
            listener.onClose();
        }
    }

    @Override
    public synchronized KeyStroke pollInput() {
        return inputQueue.poll();
    }

    @Override
    public synchronized KeyStroke readInput() {
        try {
            return inputQueue.take();
        }
        catch(InterruptedException e) {
            throw new RuntimeException("Unexpected interrupt", e);
        }
    }

    @Override
    public TextGraphics newTextGraphics() {
        return new VirtualTerminalTextGraphics(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // VirtualTerminal specific methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public synchronized void addVirtualTerminalListener(VirtualTerminalListener listener) {
        if(listener != null) {
            listeners.add(listener);
        }
    }

    @Override
    public synchronized void removeVirtualTerminalListener(VirtualTerminalListener listener) {
        listeners.remove(listener);
    }

    @Override
    public synchronized void setBacklogSize(int backlogSize) {
        this.backlogSize = backlogSize;
    }

    @Override
    public synchronized boolean isCursorVisible() {
        return cursorVisible;
    }

    @Override
    public void addInput(KeyStroke keyStroke) {
        inputQueue.add(keyStroke);
    }

    public synchronized TreeSet<Point> getDirtyCells() {
        return new TreeSet<>(dirtyTerminalCells);
    }

    public synchronized TreeSet<Point> getAndResetDirtyCells() {
        TreeSet<Point> copy = new TreeSet<>(dirtyTerminalCells);
        dirtyTerminalCells.clear();
        return copy;
    }

    public synchronized boolean isWholeBufferDirtyThenReset() {
        boolean copy = wholeBufferDirty;
        wholeBufferDirty = false;
        return copy;
    }

    @Override
    public synchronized TextCharacter getCharacter(Point point) {
        return getCharacter(point.getColumn(), point.getRow());
    }

    @Override
    public synchronized TextCharacter getCharacter(int column, int row) {
        if(dimension.getRows() < currentTextBuffer.getLineCount()) {
            row += currentTextBuffer.getLineCount() - dimension.getRows();
        }
        return getBufferCharacter(column, row);
    }

    @Override
    public TextCharacter getBufferCharacter(int column, int row) {
        return currentTextBuffer.getCharacter(row, column);
    }

    @Override
    public TextCharacter getBufferCharacter(Point point) {
        return getBufferCharacter(point.getColumn(), point.getRow());
    }

    @Override
    public synchronized int getBufferLineCount() {
        return currentTextBuffer.getLineCount();
    }

    @Override
    public synchronized void forEachLine(int startRow, int endRow, BufferWalker bufferWalker) {
        final BufferLine emptyLine = column -> TextCharacter.DEFAULT_CHARACTER;
        ListIterator<List<TextCharacter>> iterator = currentTextBuffer.getLinesFrom(startRow);
        for(int row = startRow; row <= endRow; row++) {
            BufferLine bufferLine = emptyLine;
            if(iterator.hasNext()) {
                final List<TextCharacter> list = iterator.next();
                bufferLine = column -> {
                    if(column >= list.size()) {
                        return TextCharacter.DEFAULT_CHARACTER;
                    }
                    return list.get(column);
                };
            }
            bufferWalker.onLine(row, bufferLine);
        }
    }

    synchronized void putCharacter(TextCharacter terminalCharacter) {
        if(terminalCharacter.is('\t')) {
            int nrOfSpaces = TabBehaviour.ALIGN_TO_COLUMN_4.getTabReplacement(cursorPoint.getColumn()).length();
            for(int i = 0; i < nrOfSpaces && cursorPoint.getColumn() < dimension.getColumns() - 1; i++) {
                putCharacter(terminalCharacter.withCharacter(' '));
            }
        }
        else {
            boolean doubleWidth = terminalCharacter.isDoubleWidth();
            // If we're at the last column and the user tries to print a double-width character, reset the cell and move
            // to the next line
            if(cursorPoint.getColumn() == dimension.getColumns() - 1 && doubleWidth) {
                currentTextBuffer.setCharacter(cursorPoint.getRow(), cursorPoint.getColumn(), TextCharacter.DEFAULT_CHARACTER);
                moveCursorToNextLine();
            }
            if(cursorPoint.getColumn() == dimension.getColumns()) {
                moveCursorToNextLine();
            }

            // Update the buffer
            int i = currentTextBuffer.setCharacter(cursorPoint.getRow(), cursorPoint.getColumn(), terminalCharacter);
            if(!wholeBufferDirty) {
                dirtyTerminalCells.add(new Point(cursorPoint.getColumn(), cursorPoint.getRow()));
                if(i == 1) {
                    dirtyTerminalCells.add(new Point(cursorPoint.getColumn() + 1, cursorPoint.getRow()));
                }
                else if(i == 2) {
                    dirtyTerminalCells.add(new Point(cursorPoint.getColumn() - 1, cursorPoint.getRow()));
                }
                if(dirtyTerminalCells.size() > (dimension.getColumns() * dimension.getRows() * 0.9)) {
                    setWholeBufferDirty();
                }
            }

            //Advance cursor
            cursorPoint = cursorPoint.withRelativeColumn(doubleWidth ? 2 : 1);
            if(cursorPoint.getColumn() > dimension.getColumns()) {
                moveCursorToNextLine();
            }
        }
    }

    /**
     * Moves the text cursor to the first column of the next line and trims the backlog of necessary
     */
    private void moveCursorToNextLine() {
        cursorPoint = cursorPoint.withColumn(0).withRelativeRow(1);
        if(cursorPoint.getRow() >= currentTextBuffer.getLineCount()) {
            currentTextBuffer.newLine();
        }
        trimBufferBacklog();
        correctCursor();
    }

    /**
     * Marks the whole buffer as dirty so every cell is considered in need to repainting. This is used by methods such
     * as clear and bell that will affect all content at once.
     */
    private void setWholeBufferDirty() {
        wholeBufferDirty = true;
        dirtyTerminalCells.clear();
    }

    private void trimBufferBacklog() {
        // Now see if we need to discard lines from the backlog
        int bufferBacklogSize = backlogSize;
        if(currentTextBuffer == privateModeTextBuffer) {
            bufferBacklogSize = 0;
        }
        int trimBacklogRows = currentTextBuffer.getLineCount() - (bufferBacklogSize + dimension.getRows());
        if(trimBacklogRows > 0) {
            currentTextBuffer.removeTopLines(trimBacklogRows);
            // Adjust cursor position
            cursorPoint = cursorPoint.withRelativeRow(-trimBacklogRows);
            correctCursor();
            if(!wholeBufferDirty) {
                // Adjust all "dirty" positions
                TreeSet<Point> newDirtySet = new TreeSet<>();
                for(Point dirtyPoint : dirtyTerminalCells) {
                    Point adjustedPoint = dirtyPoint.withRelativeRow(-trimBacklogRows);
                    if(adjustedPoint.getRow() >= 0) {
                        newDirtySet.add(adjustedPoint);
                    }
                }
                dirtyTerminalCells.clear();
                dirtyTerminalCells.addAll(newDirtySet);
            }
        }
    }

    private void correctCursor() {
        this.cursorPoint = cursorPoint.withColumn(Math.min(cursorPoint.getColumn(), dimension.getColumns() - 1));
        this.cursorPoint = cursorPoint.withRow(Math.min(cursorPoint.getRow(), Math.max(dimension.getRows(), getBufferLineCount()) - 1));
        this.cursorPoint =
                new Point(
                        Math.max(cursorPoint.getColumn(), 0),
                        Math.max(cursorPoint.getRow(), 0));
    }

    @Override
    public String toString() {
        return currentTextBuffer.toString();
    }
}

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

import java.io.EOFException;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Abstract implementation of {@link TextUiThread} with common logic for both available concrete implementations.
 */
public abstract class AbstractTextUiThread implements TextUiThread {

    protected final Frame frame;
    protected final Queue<Runnable> customTasks;
    protected ExceptionHandler exceptionHandler;

    /**
     * Sets up this {@link AbstractTextUiThread} for operations on the supplies {@link Frame}
     * @param frame Text GUI this {@link TextUiThread} implementations will be operating on
     */
    public AbstractTextUiThread(Frame frame) {
        this.exceptionHandler = new ExceptionHandler() {
            @Override
            public boolean onIOException(IOException e) {
                e.printStackTrace();
                return true;
            }

            @Override
            public boolean onRuntimeException(RuntimeException e) {
                e.printStackTrace();
                return true;
            }
        };
        this.frame = frame;
        this.customTasks = new LinkedBlockingQueue<>();
    }

    @Override
    public void invokeLater(Runnable runnable) throws IllegalStateException {
        customTasks.add(runnable);
    }

    @Override
    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        if(exceptionHandler == null) {
            throw new IllegalArgumentException("Cannot call setExceptionHandler(null)");
        }
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public synchronized boolean processEventsAndUpdate() throws IOException {
        if(getThread() != Thread.currentThread()) {
            throw new IllegalStateException("Calling processEventAndUpdate outside of GUI thread");
        }
        try {
            frame.processInput();
            while (!customTasks.isEmpty()) {
                Runnable r = customTasks.poll();
                if (r != null) {
                    r.run();
                }
            }
            if (frame.isPendingUpdate()) {
                frame.updateScreen();
                return true;
            }
            return false;
        }
        catch (EOFException e) {
            // Always re-throw EOFExceptions so the UI system knows we've closed the terminal
            throw e;
        }
        catch (IOException e) {
            if (exceptionHandler != null) {
                exceptionHandler.onIOException(e);
            }
            else {
                throw e;
            }
        }
        catch (RuntimeException e) {
            if (exceptionHandler != null) {
                exceptionHandler.onRuntimeException(e);
            }
            else {
                throw e;
            }
        }
        return true;
    }

    @Override
    public void invokeAndWait(final Runnable runnable) throws IllegalStateException, InterruptedException {
        Thread guiThread = getThread();
        if(guiThread == null || Thread.currentThread() == guiThread) {
            runnable.run();
        }
        else {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            invokeLater(() -> {
                try {
                    runnable.run();
                }
                finally {
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
        }
    }
}

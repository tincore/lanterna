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
import com.googlecode.lanterna.Rectangle;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.BasicTextImage;
import com.googlecode.lanterna.graphics.TextImage;
import com.googlecode.lanterna.gui2.Window.Hint;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.input.MouseAction;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.VirtualScreen;

import java.io.EOFException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is the main Text GUI implementation built into Lanterna, supporting multiple tiled windows and a dynamic
 * background area that can be fully customized. If you want to create a text-based GUI with windows and controls,
 * it's very likely this is what you want to use.
 * <p>
 * Note: This class used to always wrap the {@link Screen} object with a {@link VirtualScreen} to ensure that the UI
 * always fits. As of 3.1.0, we don't do this anymore so when you create the {@link MultiWindowFrame} you can wrap
 * the screen parameter yourself if you want to keep this behavior.
 *
 * @author Martin
 */
public class MultiWindowFrame extends AbstractFrame implements WindowFrame {
    private final WindowManager windowManager;
    private final RootPane backgroundPane;
    private final WindowList windowList;
    private final IdentityHashMap<Window, TextImage> windowRenderBufferCache;
    private final WindowPostRenderer postRenderer;

    private boolean eofWhenNoWindows;

    private Window titleBarDragWindow;
    private Point originWindowPoint;
    private Point dragStart;

    /**
     * Creates a new {@code MultiWindowTextGUI} that uses the specified {@code Screen} as the backend for all drawing
     * operations. The background area of the GUI will be a solid color, depending on theme (default is blue). The
     * current thread will be used as the GUI thread for all Lanterna library operations.
     *
     * @param screen Screen to use as the backend for drawing operations
     */
    public MultiWindowFrame(Screen screen) {
        this(new SameTextUiThread.Factory(), screen);
    }

    /**
     * Creates a new {@code MultiWindowTextGUI} that uses the specified {@code Screen} as the backend for all drawing
     * operations. The background area of the GUI will be a solid color, depending on theme (default is blue). This
     * constructor allows you control the threading model for the UI.
     *
     * @param guiThreadFactory Factory implementation to use when creating the {@code TextGUIThread}
     * @param screen           Screen to use as the backend for drawing operations
     */
    public MultiWindowFrame(TextUiThreadFactory guiThreadFactory, Screen screen) {
        this(guiThreadFactory,
            screen,
            new DefaultWindowManager(),
            null,
            new FrameBackdrop());
    }

    /**
     * Creates a new {@code MultiWindowTextGUI} that uses the specified {@code Screen} as the backend for all drawing
     * operations. The background area of the GUI will be a solid color, depending on theme (default is blue). This
     * constructor allows you control the threading model for the UI and set a custom {@link WindowManager}.
     *
     * @param guiThreadFactory Factory implementation to use when creating the {@code TextGUIThread}
     * @param screen           Screen to use as the backend for drawing operations
     * @param windowManager    Custom window manager to use
     */
    public MultiWindowFrame(TextUiThreadFactory guiThreadFactory, Screen screen, WindowManager windowManager) {
        this(guiThreadFactory,
            screen,
            windowManager,
            null,
            new FrameBackdrop());
    }

    /**
     * Creates a new {@code MultiWindowTextGUI} that uses the specified {@code Screen} as the backend for all drawing
     * operations. The background area of the GUI is a solid color as decided by the {@code backgroundColor} parameter.
     *
     * @param screen          Screen to use as the backend for drawing operations
     * @param backgroundColor Color to use for the GUI background
     * @deprecated It's preferred to use a custom background component if you want to customize the background color,
     * or you should change the theme. Using this constructor won't work well with theming.
     */
    @Deprecated
    public MultiWindowFrame(
        Screen screen,
        TextColor backgroundColor) {

        this(screen, new DefaultWindowManager(), new EmptySpace(backgroundColor));
    }

    /**
     * Creates a new {@code MultiWindowTextGUI} that uses the specified {@code Screen} as the backend for all drawing
     * operations. The background area of the GUI will be the component supplied instead of the usual backdrop. This
     * constructor allows you to set a custom {@link WindowManager} instead of {@link DefaultWindowManager}.
     *
     * @param screen        Screen to use as the backend for drawing operations
     * @param windowManager Window manager implementation to use
     * @param background    Component to use as the background of the GUI, behind all the windows
     */
    public MultiWindowFrame(
        Screen screen,
        WindowManager windowManager,
        Component background) {

        this(screen, windowManager, null, background);
    }

    /**
     * Creates a new {@code MultiWindowTextGUI} that uses the specified {@code Screen} as the backend for all drawing
     * operations. The background area of the GUI will be the component supplied instead of the usual backdrop. This
     * constructor allows you to set a custom {@link WindowManager} instead of {@link DefaultWindowManager} as well
     * as a custom {@link WindowPostRenderer} that can be used to tweak the appearance of any window.
     *
     * @param screen        Screen to use as the backend for drawing operations
     * @param windowManager Window manager implementation to use
     * @param postRenderer  {@code WindowPostRenderer} object to invoke after each window has been drawn
     * @param background    Component to use as the background of the GUI, behind all the windows
     */
    public MultiWindowFrame(
        Screen screen,
        WindowManager windowManager,
        WindowPostRenderer postRenderer,
        Component background) {

        this(new SameTextUiThread.Factory(), screen, windowManager, postRenderer, background);
    }

    /**
     * Creates a new {@code MultiWindowTextGUI} that uses the specified {@code Screen} as the backend for all drawing
     * operations. The background area of the GUI will be the component supplied instead of the usual backdrop. This
     * constructor allows you to set a custom {@link WindowManager} instead of {@link DefaultWindowManager} as well
     * as a custom {@link WindowPostRenderer} that can be used to tweak the appearance of any window. This constructor
     * also allows you to control the threading model for the UI.
     *
     * @param guiThreadFactory Factory implementation to use when creating the {@code TextGUIThread}
     * @param screen           Screen to use as the backend for drawing operations
     * @param windowManager    Window manager implementation to use
     * @param postRenderer     {@code WindowPostRenderer} object to invoke after each window has been drawn
     * @param background       Component to use as the background of the GUI, behind all the windows
     */
    public MultiWindowFrame(
        TextUiThreadFactory guiThreadFactory,
        Screen screen,
        WindowManager windowManager,
        WindowPostRenderer postRenderer,
        Component background) {

        super(guiThreadFactory, screen);
        windowList = new WindowList();
        if (windowManager == null) {
            throw new IllegalArgumentException("Creating a window-based TextGUI requires a WindowManager");
        }
        if (background == null) {
            //Use a sensible default instead of throwing
            background = new FrameBackdrop();
        }
        this.windowManager = windowManager;
        this.backgroundPane = new AbstractRootPane<>(Attributes.EMPTY) {
            public Point fromGlobal(Point globalPoint) {
                return globalPoint;
            }

            @Override
            public Frame getTextGUI() {
                return MultiWindowFrame.this;
            }

            RootPane self() {
                return this;
            }

            @Override
            public Point toGlobal(Point localPoint) {
                return localPoint;
            }
        };
        this.backgroundPane.setComponent(background);
        this.windowRenderBufferCache = new IdentityHashMap<>();
        this.postRenderer = postRenderer;
        this.eofWhenNoWindows = false;
    }

    @Override
    public synchronized WindowFrame addWindow(Window window) {
        //To protect against NPE if the user forgot to set a content component
        if (window.getComponent() == null) {
            window.setComponent(new EmptySpace(Dimension.ONE));
        }

        if (window.getTextGUI() != null) {
            window.getTextGUI().removeWindow(window);
        }
        window.setTextGUI(this);
        windowManager.onAdded(this, window, windowList.getWindowsInStableOrder());

        windowList.addWindow(window);

        invalidate();
        return this;
    }

    @Override
    public WindowFrame addWindowAndWait(Window window) {
        addWindow(window);
        window.waitUntilClosed();
        return this;
    }

    /**
     * Switches the active window by cyclically shuffling the window list. If {@code reverse} parameter is {@code false}
     * then the current top window is placed at the bottom of the stack and the window immediately behind it is the new
     * top. If {@code reverse} is set to {@code true} then the window at the bottom of the stack is moved up to the
     * front and the previous top window will be immediately below it
     *
     * @param reverse Direction to cycle through the windows
     * @return Itself
     */
    public synchronized WindowFrame cycleActiveWindow(boolean reverse) {
        windowList.cycleActiveWindow(reverse);
        return this;
    }

    private void drawBackgroundPane(TextUiGraphics graphics) {
        backgroundPane.draw(new DefaultTextUiGraphics(this, graphics));
    }

    @Override
    protected synchronized void drawGUI(TextUiGraphics graphics) {
        drawBackgroundPane(graphics);
        windowManager.prepareWindows(this, windowList.getWindowsInStableOrder(), graphics.getSize());
        for (Window window : getWindows()) {
            if (window.isVisible()) {
                // First draw windows to a buffer, then copy it to the real destination. This is to make physical off-screen
                // drawing work better. Store the buffers in a cache so we don't have to re-create them every time.
                TextImage textImage = windowRenderBufferCache.get(window);
                if (textImage == null || !textImage.getSize().equals(window.getDecoratedSize())) {
                    textImage = new BasicTextImage(window.getDecoratedSize());
                    windowRenderBufferCache.put(window, textImage);
                }
                TextUiGraphics windowGraphics = new DefaultTextUiGraphics(this, textImage.newTextGraphics());
                TextUiGraphics insideWindowDecorationsGraphics = windowGraphics;
                Point contentOffset = Point.TOP_LEFT_CORNER;
                if (!window.isHint(Hint.NO_DECORATIONS)) {
                    WindowDecorationRenderer decorationRenderer = windowManager.getWindowDecorationRenderer(window);
                    insideWindowDecorationsGraphics = decorationRenderer.draw(this, windowGraphics, window);
                    contentOffset = decorationRenderer.getOffset(window);
                }

                window.draw(insideWindowDecorationsGraphics);
                window.setContentOffset(contentOffset);
                if (windowGraphics != insideWindowDecorationsGraphics) {
                    Borders.joinLinesWithFrame(windowGraphics);
                }

                graphics.drawImage(window.getPosition(), textImage);

                if (!window.isHint(Hint.NO_POST_RENDERING)) {
                    if (window.getPostRenderer() != null) {
                        window.getPostRenderer().postRender(graphics, this, window);
                    } else if (postRenderer != null) {
                        postRenderer.postRender(graphics, this, window);
                    } else if (getTheme().getWindowPostRenderer() != null) {
                        getTheme().getWindowPostRenderer().postRender(graphics, this, window);
                    }
                }
            }
        }

        // Purge the render buffer cache from windows that have been removed
        windowRenderBufferCache.keySet().retainAll(getWindows());
    }

    @Override
    public synchronized Window getActiveWindow() {
        return windowList.getActiveWindow();
    }

    @Override
    public synchronized MultiWindowFrame setActiveWindow(Window activeWindow) {
        windowList.setActiveWindow(activeWindow);
        return this;
    }

    @Override
    public RootPane getBackgroundPane() {
        return backgroundPane;
    }

    @Override
    public synchronized Point getCursorPosition() {
        Window activeWindow = getActiveWindow();
        if (activeWindow != null) {
            return activeWindow.toGlobal(activeWindow.getCursorPosition());
        } else {
            return backgroundPane.getCursorPosition();
        }
    }

    @Override
    public synchronized Interactable getFocusedInteractable() {
        Window activeWindow = getActiveWindow();
        if (activeWindow != null) {
            return activeWindow.getFocusedInteractable();
        } else {
            return backgroundPane.getFocusedInteractable();
        }
    }

    @Override
    public WindowManager getWindowManager() {
        return windowManager;
    }

    @Override
    public WindowPostRenderer getWindowPostRenderer() {
        return postRenderer;
    }

    @Override
    public synchronized Collection<Window> getWindows() {
        return windowList.getWindowsInZOrder();
    }

    /**
     * Returns whether the TextGUI should return EOF when you try to read input while there are no windows in the window
     * manager. When this is true (true by default) will make the GUI automatically exit when the last window has been
     * closed.
     *
     * @return Should the GUI return EOF when there are no windows left
     */
    public boolean isEOFWhenNoWindows() {
        return eofWhenNoWindows;
    }

    /**
     * Sets whether the TextGUI should return EOF when you try to read input while there are no windows in the window
     * manager. Setting this to true (off by default) will make the GUI automatically exit when the last window has been
     * closed.
     *
     * @param eofWhenNoWindows Should the GUI return EOF when there are no windows left
     */
    public void setEOFWhenNoWindows(boolean eofWhenNoWindows) {
        this.eofWhenNoWindows = eofWhenNoWindows;
    }

    @Override
    public synchronized boolean isPendingUpdate() {
        for (Window window : getWindows()) {
            if (window.isVisible() && window.isInvalid()) {
                return true;
            }
        }
        return super.isPendingUpdate() || backgroundPane.isInvalid() || windowManager.isInvalid();
    }

    public synchronized WindowFrame moveToBottom(Window window) {
        windowList.moveToBottom(window);
        invalidate();
        return this;
    }

    @Override
    public synchronized WindowFrame moveToTop(Window window) {
        windowList.moveToTop(window);
        invalidate();
        return this;
    }

    @Override
    public synchronized boolean onInput(KeyStroke keyStroke) {
        if (keyStroke instanceof MouseAction) {
            MouseAction mouseAction = (MouseAction) keyStroke;
            if (mouseAction.isMouseDown()) {
                // for now, active windows do not overlap?
                // by happenstance, the last in the list in case of many overlapping will be active
                Window priorActiveWindow = getActiveWindow();
                AtomicBoolean anyHit = new AtomicBoolean(false);
                List<Window> snapshot = new ArrayList<>(getWindows());
                for (Window window : snapshot) {
                    window.getBounds().whenContains(mouseAction.getPosition(), () -> {
                        setActiveWindow(window);
                        anyHit.set(true);
                    });
                }
                // clear popup menus if they clicked onto another window or missed all windows
                if (priorActiveWindow != getActiveWindow() || !anyHit.get()) {
                    if (priorActiveWindow.isHint(Hint.MENU_POPUP)) {
                        priorActiveWindow.close();
                    }
                }
            }
        }
        if (keyStroke instanceof MouseAction) {
            MouseAction mouseAction = (MouseAction) keyStroke;
            if (mouseAction.isMouseDown()) {
                titleBarDragWindow = null;
                dragStart = null;
                Window window = getActiveWindow();
                if (window != null && !window.isHint(Hint.MENU_POPUP)) {
                    WindowDecorationRenderer decorator = windowManager.getWindowDecorationRenderer(window);
                    Rectangle titleBarRectangle = decorator.getTitleBarRectangle(window);
                    Point local = window.fromGlobalToDecoratedRelative(mouseAction.getPosition());
                    titleBarRectangle.whenContains(local, () -> {
                        titleBarDragWindow = window;
                        originWindowPoint = titleBarDragWindow.getPosition();
                        dragStart = mouseAction.getPosition();
                    });
                }

            }
        }
        if (titleBarDragWindow != null && keyStroke instanceof MouseAction) {
            MouseAction mouse = (MouseAction) keyStroke;
            if (mouse.isMouseDrag()) {
                Point mp = mouse.getPosition();
                Point wp = originWindowPoint;
                int dx = mp.getColumn() - dragStart.getColumn();
                int dy = mp.getRow() - dragStart.getRow();
                titleBarDragWindow.setDraggable(); // !!!!!!! THIS CHANGES HINTS!!
                titleBarDragWindow.setPosition(new Point(wp.getColumn() + dx, wp.getRow() + dy));
                // TODO ? any additional children popups (shown menus, etc) should also be moved (or just closed)
            }
        }

        return Optional.ofNullable(getActiveWindow())
            .map(w -> w.onInput(keyStroke))
            .orElseGet(() -> backgroundPane.onInput(keyStroke));
    }

    @Override
    protected synchronized KeyStroke readKeyStroke() throws IOException {
        KeyStroke keyStroke = super.pollInput();
        if (windowList.isHadWindowAtSomePoint() && eofWhenNoWindows && keyStroke == null && getWindows().isEmpty()) {
            return new KeyStroke(KeyType.EOF);
        } else if (keyStroke != null) {
            return keyStroke;
        } else {
            return super.readKeyStroke();
        }
    }

    @Override
    public synchronized WindowFrame removeWindow(Window window) {
        boolean contained = windowList.removeWindow(window);
        if (!contained) {
            //Didn't contain this window
            return this;
        }
        window.setTextGUI(null);
        windowManager.onRemoved(this, window, windowList.getWindowsInStableOrder());
        invalidate();
        return this;
    }

    @Override
    public synchronized void updateScreen() throws IOException {
        if (getScreen() instanceof VirtualScreen) {
            // If the user has passed in a virtual screen, we should calculate the minimum size required and tell it.
            // Previously the constructor always wrapped the screen in a VirtualScreen, but now we need to check.
            Dimension minimumDimension = Dimension.ZERO;
            for (Window window : getWindows()) {
                if (window.isVisible()) {
                    if (window.isHint(Hint.FULL_SCREEN) ||
                        window.isHint(Hint.FIT_TERMINAL_WINDOW) ||
                        window.isHint(Hint.EXPANDED)) {
                        //Don't take full screen windows or auto-sized windows into account
                        continue;
                    }
                    Point lastPoint = window.getPosition();
                    minimumDimension = minimumDimension.max(
                        //Add position to size to get the bottom-right corner of the window
                        window.getDecoratedSize().withRelative(
                            Math.max(lastPoint.getColumn(), 0),
                            Math.max(lastPoint.getRow(), 0)));
                }
            }
            ((VirtualScreen) getScreen()).setMinimumSize(minimumDimension);
        }
        super.updateScreen();
    }

    @Override
    public void waitForWindowToClose(Window window) {
        while (window.getTextGUI() != null) {
            boolean sleep = true;
            TextUiThread guiThread = getGUIThread();
            if (Thread.currentThread() == guiThread.getThread()) {
                try {
                    sleep = !guiThread.processEventsAndUpdate();
                } catch (EOFException ignore) {
                    //The GUI has closed so allow exit
                    break;
                } catch (IOException e) {
                    throw new RuntimeException("Unexpected IOException while waiting for window to close", e);
                }
            }
            if (sleep) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignore) {
                }
            }
        }
    }
}

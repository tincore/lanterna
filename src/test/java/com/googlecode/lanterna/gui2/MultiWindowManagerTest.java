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
import com.googlecode.lanterna.bundle.LanternaThemes;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiWindowManagerTest extends AbstractGuiTest {

    private static final AtomicInteger WINDOW_COUNTER = new AtomicInteger(0);
    private static int nextTheme = 0;
    private boolean virtualScreenEnabled = true;
    private Button buttonToggleVirtualScreen;

    public static void main(String[] args) throws IOException, InterruptedException {
        new MultiWindowManagerTest().run(args);
    }

    @Override
    public void init(final WindowFrame windowFrame) {
        windowFrame.getBackgroundPane().setComponent(new BackgroundComponent());
        windowFrame.setKeyStrokeListener((k, h, g) -> {
            if (h) {
                return false;
            }
            if ((k.isCtrlDown() && k.isKeyType(KeyType.Tab)) || k.getKeyType() == KeyType.F6) {
                ((WindowFrame) g).cycleActiveWindow(false);
                return true;
            } else if ((k.isCtrlDown() && k.isKeyType(KeyType.ReverseTab)) || k.getKeyType() == KeyType.F7) {
                ((WindowFrame) g).cycleActiveWindow(true);
                return true;
            }

            return false;
        });


        buttonToggleVirtualScreen = new Button("Virtual Screen: Enabled", s -> {
            virtualScreenEnabled = !virtualScreenEnabled;
            buttonToggleVirtualScreen.setLabel("Virtual Screen: " + (virtualScreenEnabled ? "Enabled" : "Disabled"));
        });

        final Window window = new BasicWindow("Multi Window Test")
            .setComponent(Panels.vertical()
                .add(new Button("Add new window", s -> onNewWindow(windowFrame)))
                .add(buttonToggleVirtualScreen)
                .add(new EmptySpace(Dimension.ONE))
                .add(new Button("Close", ON_CLICK_CLOSE_CONTAINER)));
        windowFrame.addWindow(window);
    }

    private void onNewWindow(WindowFrame textGUI) {
        DynamicWindow window = new DynamicWindow();
        List<String> availableThemes = new ArrayList<>(LanternaThemes.getRegisteredThemes());
        String themeName = availableThemes.get(nextTheme++);
        if (nextTheme == availableThemes.size()) {
            nextTheme = 0;
        }
        window.setTheme(LanternaThemes.getTheme(themeName));
        textGUI.addWindow(window);
    }

    private static class DynamicWindow extends BasicWindow {

        private final Label labelWindowSize;
        private final Label labelWindowPosition;
        private final Label labelUnlockWindow;

        public DynamicWindow() {
            super("Window #" + WINDOW_COUNTER.incrementAndGet());

            this.labelWindowPosition = new Label("");
            this.labelWindowSize = new Label("");
            this.labelUnlockWindow = new Label("true");


            addWindowListener(new WindowMoveListenerAdapter() {
                @Override
                public void onMoved(Window window, Point oldPoint, Point newPoint) {
                    labelWindowPosition.setText(newPoint.toString());
                }

                @Override
                public void onResized(Window window, Dimension oldSize, Dimension newSize) {
                    labelWindowSize.setText(newSize.toString());
                }
            });

            setComponent(Panels.grid(1)
                .add(Panels.grid(2,
                    new Label("Position:"),
                    labelWindowPosition,
                    new Label("Size:"),
                    labelWindowSize,
                    new Label("Auto-sized:"),
                    labelUnlockWindow
                ))
                .add(new EmptySpace(Dimension.ONE))
                .add(new Label("Move window with ALT+Arrow\n" + "Resize window with CTRL+Arrow"))
                .add(new EmptySpace(Dimension.ONE)
                    .setLayoutData(
                        GridLayout.createLayoutData(GridLayout.Alignment.FILL, GridLayout.Alignment.FILL, true, true)))
                .add(Panels.horizontal(
                    new Button("Toggle auto-sized", s -> this.toggleManaged()),
                    new Button("Close", s -> this.close()))));
        }

        @Override
        public boolean onInput(KeyStroke keyStroke) {
            boolean handled = super.onInput(keyStroke);
            if (!handled) {
                switch (keyStroke.getKeyType()) {
                    case ArrowDown:
                        if (keyStroke.isAltDown()) {
                            setPosition(getPosition().withRelativeRow(1));
                        } else if (keyStroke.isCtrlDown()) {
                            setFixedSize(getSize().withRelativeRows(1));
                            labelUnlockWindow.setText("false");
                        }
                        handled = true;
                        break;
                    case ArrowLeft:
                        if (keyStroke.isAltDown()) {
                            setPosition(getPosition().withRelativeColumn(-1));
                        } else if (keyStroke.isCtrlDown() && getSize().getColumns() > 1) {
                            setFixedSize(getSize().withRelativeColumns(-1));
                            labelUnlockWindow.setText("false");
                        }
                        handled = true;
                        break;
                    case ArrowRight:
                        if (keyStroke.isAltDown()) {
                            setPosition(getPosition().withRelativeColumn(1));
                        } else if (keyStroke.isCtrlDown()) {
                            setFixedSize(getSize().withRelativeColumns(1));
                            labelUnlockWindow.setText("false");
                        }
                        handled = true;
                        break;
                    case ArrowUp:
                        if (keyStroke.isAltDown()) {
                            setPosition(getPosition().withRelativeRow(-1));
                        } else if (keyStroke.isCtrlDown() && getSize().getRows() > 1) {
                            setFixedSize(getSize().withRelativeRows(-1));
                            labelUnlockWindow.setText("false");
                        }
                        handled = true;
                        break;
                }
            }
            return handled;
        }

        private void toggleManaged() {
            boolean isManaged = !isHint(Hint.FIXED_SIZE);
            isManaged = !isManaged;
            if (isManaged) {
                setHints(Collections.emptyList());
            } else {
                setHints(Hint.FIXED_SIZE);
            }
            labelUnlockWindow.setText(Boolean.toString(isManaged));
        }
    }

    private static class BackgroundComponent extends FrameBackdrop {
        @Override
        protected ComponentRenderer<EmptySpace> createDefaultRenderer() {
            return new ComponentRenderer<EmptySpace>() {
                @Override
                public void drawComponent(TextUiGraphics graphics, EmptySpace component) {
                    graphics.applyThemeStyle(component.getTheme().getDefinition(FrameBackdrop.class).getNormal());
                    graphics.fill('・');
                    String text = "Press <CTRL+Tab>/F6 and <CTRL+Shift+Tab>/F7 to cycle active window";
                    graphics.putString(graphics.getSize().getColumns() - text.length() - 4, graphics.getSize().getRows() - 1, text);
                }

                @Override
                public Dimension getPreferredSize(EmptySpace component) {
                    return Dimension.ONE;
                }
            };
        }


    }
}

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
import com.googlecode.lanterna.Dimension;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.ansi.TelnetTerminal;
import com.googlecode.lanterna.terminal.ansi.TelnetTerminalServer;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import static com.googlecode.lanterna.gui2.Panels.vertical;
import static com.googlecode.lanterna.gui2.AbstractGuiTest.createButtonCloseContainer;

public class GUIOverTelnet {
    private static final List<TextBox> ALL_TEXTBOXES = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        TelnetTerminalServer telnetTerminalServer = new TelnetTerminalServer(1024);
        System.out.println("Listening on port 1024, please connect to it with a separate telnet process");
        //noinspection InfiniteLoopStatement
        while (true) {
            final TelnetTerminal telnetTerminal = telnetTerminalServer.acceptConnection();
            System.out.println("Accepted connection from " + telnetTerminal.getRemoteSocketAddress());
            Thread thread = new Thread(() -> {
                try {
                    runGUI(telnetTerminal);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    telnetTerminal.close();
                } catch (IOException ignore) {
                }
            });
            thread.start();
        }
    }

    @SuppressWarnings({"rawtypes"})
    private static void runGUI(final TelnetTerminal telnetTerminal) throws IOException {
        Screen screen = new TerminalScreen(telnetTerminal);
        screen.start();
        final MultiWindowTextGUI textGUI = new MultiWindowTextGUI(screen);
        textGUI.setBlockingIO(false);
        textGUI.setEOFWhenNoWindows(true);
        try {
            final BasicWindow window = new BasicWindow("Text GUI over Telnet");
            Panel contentArea = Panels.vertical()
                .add(new Button("Button", s2 -> {
                    textGUI.addWindow(new BasicWindow("Response")
                        .setComponent(vertical(
                            new Label("Hello!"),
                            createButtonCloseContainer())));
                }).withBorder(Borders.singleLine("This is a button")));

            final TextBox textBox = new TextBox(new Dimension(40, 4)) {
                @Override
                public KeyStrokeResult onKeyStroke(KeyStroke keyStroke) {
                    try {
                        return super.onKeyStroke(keyStroke);
                    } finally {
                        for (TextBox box : ALL_TEXTBOXES) {
                            if (this != box) {
                                box.setText(getText());
                            }
                        }
                    }
                }
            };
            ALL_TEXTBOXES.add(textBox);

            contentArea.add(textBox.withBorder(Borders.singleLine("Text editor")))
                .add(new AbstractInteractableComponent(Attributes.EMPTY) {
                    String text = "Press any key";

                    @Override
                    protected InteractableRenderer createDefaultRenderer() {
                        return new InteractableRenderer() {
                            @Override
                            public void drawComponent(TextGUIGraphics graphics, Component component) {
                                graphics.putString(0, 0, text);
                            }

                            @Override
                            public Point getCursorLocation(Component component) {
                                return Point.TOP_LEFT_CORNER;
                            }

                            @Override
                            public Dimension getPreferredSize(Component component) {
                                return new Dimension(30, 1);
                            }
                        };
                    }

                    @Override
                    public KeyStrokeResult onKeyStroke(KeyStroke keyStroke) {
                        if (keyStroke.getKeyType() == KeyType.Tab ||
                            keyStroke.getKeyType() == KeyType.ReverseTab) {
                            return super.onKeyStroke(keyStroke);
                        }
                        if (keyStroke.getKeyType() == KeyType.Character) {
                            text = "Character: " + keyStroke.getCharacter() + (keyStroke.isCtrlDown() ? " (ctrl)" : "") +
                                (keyStroke.isAltDown() ? " (alt)" : "");
                        } else {
                            text = "Key: " + keyStroke.getKeyType() + (keyStroke.isCtrlDown() ? " (ctrl)" : "") +
                                (keyStroke.isAltDown() ? " (alt)" : "");
                        }
                        return KeyStrokeResult.HANDLED;
                    }
                }.withBorder(Borders.singleLine("Custom component")));

            contentArea.add(createButtonCloseContainer());
            window.setComponent(contentArea);

            textGUI.addWindowAndWait(window);
        } finally {
            try {
                screen.stop();
            } catch (SocketException ignore) {
                // If the telnet client suddenly quit, we'll get an exception when we try to get the client to exit
                // private mode, but that's fine, no need to report this
            }
        }
    }
}

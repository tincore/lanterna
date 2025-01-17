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

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.BasicTextImage;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextImage;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class FullScreenTextGUITest {
    public static void main(String[] args) throws IOException, InterruptedException {
        Screen screen = new TestTerminalFactory(args).setInitialTerminalSize(new Dimension(80, 25)).createScreen();
        screen.start();

        final AtomicBoolean stop = new AtomicBoolean(false);
        MultiWindowFrame multiWindowFrame = new MultiWindowFrame(screen);
        multiWindowFrame.setKeyStrokeListener((k, h, s) -> {
            if (h) {
                return false;
            }
            if (k.getKeyType() == KeyType.Escape) {
                stop.set(true);
                return true;
            }
            return false;
        });
        try {
            multiWindowFrame.getBackgroundPane().setComponent(new BIOS());
            while (!stop.get()) {
                if (!multiWindowFrame.getGUIThread().processEventsAndUpdate()) {
                    Thread.sleep(1);
                }
            }
        } catch (EOFException ignore) {
            // Terminal closed
        } finally {
            screen.stop();
        }
    }

    private static class BIOS extends Panel {
        private final TextImage background;
        private final Label helpLabel;

        private BIOS() {
            setLayoutManager(new AbsoluteLayout());
            background = createBackground();

            helpLabel = new Label("");
            helpLabel.setForegroundColor(TextColor.ANSI.YELLOW);
            helpLabel.setBackgroundColor(TextColor.ANSI.BLUE);
            helpLabel.addStyle(SGR.BOLD);

            BIOSButton button1 = new BIOSButton("Standard Lanterna Features", "Time, Date, Type...");
            BIOSButton button2 = new BIOSButton("Advanced Lanterna Features", "Well, what could this possibly be?");
            BIOSButton button3 = new BIOSButton("Advanced Terminal Features", "As you can see, I can change the description here");
            BIOSButton button4 = new BIOSButton("Unintegrated Peripherals", "Joystick, VirtualBoy, Coffee Machines, ...");
            BIOSButton button5 = new BIOSButton("Power Management Setup", "Terminal energy-saving mode?");
            BIOSButton button6 = new BIOSButton("Non-PnP/ISA Configurations", "Going back to the '80s");
            BIOSButton button7 = new BIOSButton("Terminal Health Status", "Monitor pixel consistency and feedback latency");
            BIOSButton button8 = new BIOSButton("Frequency/Current Control", "To overclock your terminal; NOT covered by warranty!");
            BIOSButton button9 = new BIOSButton("Load Fail-Safe Defaults", "Restore everything back");
            BIOSButton button10 = new BIOSButton("Load Optimized Defaults", "And still you play the sycophant and revel in my pain");
            BIOSButton button11 = new BIOSButton("Set Supervisor Password", "This is an outright fabrication");
            BIOSButton button12 = new BIOSButton("Set User Password", "What would you even need this for?");
            BIOSButton button13 = new BIOSButton("Save & Exit Setup", "...and then you can have some cake!");
            BIOSButton button14 = new BIOSButton("Exit Without Saving", "僕の事が思い出せなくても泣かないでね");

            button1.setSize(new Dimension(35, 1));
            button1.setPosition(new Point(3, 3));
            button2.setSize(new Dimension(35, 1));
            button2.setPosition(new Point(3, 5));
            button3.setSize(new Dimension(35, 1));
            button3.setPosition(new Point(3, 7));
            button4.setSize(new Dimension(35, 1));
            button4.setPosition(new Point(3, 9));
            button5.setSize(new Dimension(35, 1));
            button5.setPosition(new Point(3, 11));
            button6.setSize(new Dimension(35, 1));
            button6.setPosition(new Point(3, 13));
            button7.setSize(new Dimension(35, 1));
            button7.setPosition(new Point(3, 15));

            button8.setSize(new Dimension(35, 1));
            button8.setPosition(new Point(43, 3));
            button9.setSize(new Dimension(35, 1));
            button9.setPosition(new Point(43, 5));
            button10.setSize(new Dimension(35, 1));
            button10.setPosition(new Point(43, 7));
            button11.setSize(new Dimension(35, 1));
            button11.setPosition(new Point(43, 9));
            button12.setSize(new Dimension(35, 1));
            button12.setPosition(new Point(43, 11));
            button13.setSize(new Dimension(35, 1));
            button13.setPosition(new Point(43, 13));
            button14.setSize(new Dimension(35, 1));
            button14.setPosition(new Point(43, 15));

            helpLabel.setPosition(new Point(2, 22));
            helpLabel.setSize(new Dimension(76, 1));
            add(helpLabel);
            for (BIOSButton button : Arrays.asList(button1, button2, button3, button4, button5, button6, button7, button8, button9, button10, button11, button12, button13, button14)) {
                add(button);
            }
            add(button14);
        }

        private TextImage createBackground() {
            BasicTextImage image = new BasicTextImage(80, 25);
            TextGraphics graphics = image.newTextGraphics();
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.setBackgroundColor(TextColor.ANSI.BLUE);
            graphics.fill(' ');

            graphics.enableModifiers(SGR.BOLD);

            graphics.putString(7, 0, "Reminds you of some BIOS, doesn't it?");
            graphics.setCharacter(0, 1, Symbols.DOUBLE_LINE_TOP_LEFT_CORNER);
            graphics.drawLine(1, 1, 78, 1, Symbols.DOUBLE_LINE_HORIZONTAL);
            graphics.setCharacter(79, 1, Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER);
            graphics.drawLine(79, 2, 79, 23, Symbols.DOUBLE_LINE_VERTICAL);
            graphics.setCharacter(79, 24, Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER);
            graphics.drawLine(1, 24, 78, 24, Symbols.DOUBLE_LINE_HORIZONTAL);
            graphics.setCharacter(0, 24, Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER);
            graphics.drawLine(0, 2, 0, 23, Symbols.DOUBLE_LINE_VERTICAL);

            graphics.setCharacter(0, 17, Symbols.DOUBLE_LINE_T_SINGLE_RIGHT);
            graphics.drawLine(1, 17, 78, 17, Symbols.SINGLE_LINE_HORIZONTAL);
            graphics.setCharacter(79, 17, Symbols.DOUBLE_LINE_T_SINGLE_LEFT);
            graphics.setCharacter(40, 17, Symbols.SINGLE_LINE_T_UP);
            graphics.drawLine(40, 2, 40, 16, Symbols.SINGLE_LINE_VERTICAL);
            graphics.setCharacter(40, 1, Symbols.DOUBLE_LINE_T_SINGLE_DOWN);

            graphics.setCharacter(0, 20, Symbols.DOUBLE_LINE_T_SINGLE_RIGHT);
            graphics.drawLine(1, 20, 78, 20, Symbols.SINGLE_LINE_HORIZONTAL);
            graphics.setCharacter(79, 20, Symbols.DOUBLE_LINE_T_SINGLE_LEFT);

            graphics.putString(2, 18, "Esc : Quit");
            graphics.putString(42, 18, Symbols.ARROW_UP + " " + Symbols.ARROW_DOWN + " " + Symbols.ARROW_RIGHT + " " +
                Symbols.ARROW_LEFT + "   : Select Item");
            graphics.putString(2, 19, "F10 : Save & Exit Setup");
            return image;
        }

        @Override
        protected ComponentRenderer<Panel> createDefaultRenderer() {
            final DefaultPanelRenderer panelRenderer = (DefaultPanelRenderer) super.createDefaultRenderer();

            // Turn off clearing the main area since we'll be using a custom renderer below to prepare the background
            panelRenderer.setFillAreaBeforeDrawingComponents(false);

            return new ComponentRenderer<Panel>() {
                @Override
                public void drawComponent(TextUiGraphics graphics, Panel component) {
                    //Clear all data
                    graphics.setBackgroundColor(TextColor.ANSI.BLACK).fill(' ');

                    //Draw the background image
                    graphics.drawImage(Point.TOP_LEFT_CORNER, background);

                    //Then draw all the child components
                    panelRenderer.drawComponent(graphics, BIOS.this);
                }

                @Override
                public Dimension getPreferredSize(Panel component) {
                    return new Dimension(80, 24);
                }
            };
        }

        private class BIOSButton extends Button {
            private final String description;

            public BIOSButton(String label, String description) {
                super(label);
                this.description = description;
                setRenderer(newRenderer());
            }

            private ButtonRenderer newRenderer() {
                return new ButtonRenderer() {
                    @Override
                    public void drawComponent(TextUiGraphics graphics, Button component) {
                        graphics.setBackgroundColor(TextColor.ANSI.BLUE);
                        graphics.fill(' ');
                        if (isFocused()) {
                            graphics.setForegroundColor(TextColor.ANSI.WHITE);
                            graphics.setBackgroundColor(TextColor.ANSI.RED);
                        } else {
                            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
                            graphics.setBackgroundColor(TextColor.ANSI.BLUE);
                        }
                        graphics.setModifiers(EnumSet.of(SGR.BOLD));
                        graphics.putString(0, 0, "  " + getLabel());
                    }

                    @Override
                    public Point getCursorLocation(Button component) {
                        return null;
                    }

                    @Override
                    public Dimension getPreferredSize(Button component) {
                        return new Dimension(TerminalTextUtils.getColumnWidth(getLabel()), 1);
                    }
                };
            }

            @Override
            public void onFocusGain(FocusChangeDirection direction, Interactable previouslyInFocus) {
                super.onFocusGain(direction, previouslyInFocus);
                helpLabel.setText(description);
            }
        }
    }
}

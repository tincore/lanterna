/*
 * Author Rajatt, modified by Andreas
 */
package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.MouseCaptureMode;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class Issue274 {

    public static void main(String[] args) throws IOException {

        final Terminal ter = new DefaultTerminalFactory()
            .setForceTextTerminal(true)
            .setMouseCaptureMode(MouseCaptureMode.CLICK)
            .setTelnetPort(1024)
            .createTerminal();

        final Screen screen = new TerminalScreen(ter);
        screen.start();
        final MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);

        Panel menubar = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL).setSpacing(1))
            .add(new TextBox(new TerminalSize(10, 10), TextBox.Style.MULTI_LINE))
            .add(new Button("Open", s -> {
                final Window op = new BasicWindow("Select file");
                gui.addWindow(op);
                op.setComponent(new Button("Close", s2 -> op.close()));
            }))
            .add(new Button("Save"))
            .add(new Button("Exit", s -> gui.getActiveWindow().close()));

        Window main = new BasicWindow("Test");
        main.setComponent(menubar);
        try {
            gui.addWindowAndWait(main);
        } finally {
            screen.stop();
        }
    }
}

package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class Issue334 {
    public static void main(String[] args) throws IOException {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.start();

        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));
        panel.add(new Label(""));

        // Create gui and start gui
        MultiWindowFrame gui = new MultiWindowFrame(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));

        // Create window to hold the panel
        final BasicWindow window = new BasicWindow();
        window.setComponent(Panels.vertical(panel));
        window.setOnKeyEscapeClose(true);

        gui.addWindowAndWait(window);
        screen.stop();
        terminal.close();
    }
}

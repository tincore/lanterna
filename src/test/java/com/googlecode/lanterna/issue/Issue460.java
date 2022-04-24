package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

public class Issue460 {
    public static void main(String[] args) throws Exception {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.start();

        final BasicWindow window1 = new BasicWindow();
        window1.setComponent(new Panel(new GridLayout(1))
            .add(new Label("VERTICAL"), GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                true,
                1,
                4
            ))
            .add(new Button("Close", s -> window1.close()), GridLayout.createHorizontallyFilledLayoutData(2)));

        // Create gui and start gui
        MultiWindowFrame gui = new MultiWindowFrame(screen);
        gui.addWindowAndWait(window1);
        screen.stop();
    }
}

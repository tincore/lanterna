package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.gui2.menu.MenuItem;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class Issue446 {

    public static BasicWindow buildWindow() {

        BasicWindow window = new BasicWindow();

        TextBox textBox = new TextBox("A");

        Panel mainPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        mainPanel.add(textBox);
        mainPanel.add(new Button("Quit", s -> window.close()));

        window.setComponent(mainPanel);
        window.setMenuBar(new MenuBar()
            .add(new Menu("Menu 1")
                .add(new MenuItem("MenuItem 1.1"))));
        return window;
    }

    public static void main(String[] args) throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Terminal terminal = terminalFactory.createTerminal();
        TerminalScreen screen = new TerminalScreen(terminal);
        screen.start();
        WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
        textGUI.addWindowAndWait(buildWindow());
    }
}

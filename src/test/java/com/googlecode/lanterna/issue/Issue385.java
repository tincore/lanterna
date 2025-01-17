package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.MultiWindowFrame;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowFrame;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.util.Collections;

public class Issue385 {
    public static void main(String[] args) throws Exception {

        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = terminalFactory.createScreen();
        screen.start();

        final WindowFrame textGUI = new MultiWindowFrame(screen);

        final Window window = new BasicWindow("My Root Window");
        window.setHints(Collections.singletonList(Window.Hint.FULL_SCREEN));

        textGUI.addWindowAndWait(window);

    }
}

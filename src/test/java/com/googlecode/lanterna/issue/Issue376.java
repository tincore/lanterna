package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;

public class Issue376 {
    public static void main(String... args) throws IOException {
        Screen screen = new DefaultTerminalFactory().createScreen();
        screen.start();
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
        Window window = new LabelWithTabWindow();
        gui.addWindow(window);
        gui.waitForWindowToClose(window);
        screen.stop();
    }

    private static class LabelWithTabWindow extends AbstractWindow {
        LabelWithTabWindow() {
            Panel panel = new Panel();
            panel.add(new Label("A label without tab"));
            panel.add(new Label("\tAnd one with tab"));
            setComponent(panel);
        }
    }
}



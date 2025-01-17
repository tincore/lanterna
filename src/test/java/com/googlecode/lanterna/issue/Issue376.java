package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;

public class Issue376 {
    public static void main(String... args) throws IOException {
        Screen screen = new DefaultTerminalFactory().createScreen();
        screen.start();
        MultiWindowFrame gui = new MultiWindowFrame(screen);
        Window window = new LabelWithTabWindow();
        gui.addWindow(window);
        gui.waitForWindowToClose(window);
        screen.stop();
    }

    private static class LabelWithTabWindow extends AbstractWindow {
        LabelWithTabWindow() {
            super(Attributes.EMPTY);
            setComponent(new Panel()
                .add(new Label("A label without tab"))
                .add(new Label("\tAnd one with tab")));
        }
    }
}



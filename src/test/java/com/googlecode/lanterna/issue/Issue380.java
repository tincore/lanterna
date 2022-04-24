package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;
import java.util.Collections;

public class Issue380 {
    public static void main(String[] args) throws IOException {
        Screen screen = new DefaultTerminalFactory().createScreen();
        screen.start();
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
        Window window = new GridWindowWithTwoLargeComponents();
        window.setHints(Collections.singletonList(Window.Hint.EXPANDED));
        gui.addWindow(window);
        gui.waitForWindowToClose(window);
        screen.stop();
    }

    private static class GridWindowWithTwoLargeComponents extends AbstractWindow {
        GridWindowWithTwoLargeComponents() {
            super(Attributes.EMPTY);
            // two column grid
            final Panel p = new Panel(new GridLayout(2));

            // spanning component in the first row
            p.add(new Label("My dummy label"), GridLayout.createLayoutData(
                GridLayout.Alignment.FILL,
                GridLayout.Alignment.BEGINNING,
                true,
                false,
                2,
                1)
            );

            // col 1, row 2
            p.add(new TextBox(), GridLayout.createLayoutData(
                GridLayout.Alignment.FILL,
                GridLayout.Alignment.FILL,
                true,
                true)
            );
            // col 2, row 2
            p.add(this.buildButtonPanel(), GridLayout.createLayoutData(
                GridLayout.Alignment.BEGINNING,
                GridLayout.Alignment.BEGINNING,
                false,
                false));

            // spanning component in row 3
            p.add(this.buildButtonBar(), GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.BEGINNING,
                false,
                false,
                2,
                1)
            );
            setComponent(p);
        }

        private Component buildButtonBar() {
            return new Button("Close", s -> this.close());
        }

        private Component buildButtonPanel() {
            Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
            panel.add(new Button("One"));
            panel.add(new Button("Two"));
            panel.add(new Button("Three"));
            return panel;
        }
    }
}

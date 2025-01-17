package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.util.Collections;

public class Issue374 {

    public static void main(String[] args) throws Exception {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.start();

        final BasicWindow window = new BasicWindow("FocusTraversalTest");
        window.setHints(Collections.singletonList(Window.Hint.FULL_SCREEN));
        MultiWindowFrame gui = new MultiWindowFrame(screen);

        Panel mainPanel = new Panel(new LinearLayout());
        window.setComponent(mainPanel);

        Button disabledInBorder1 = new Button("disabledB1");
        disabledInBorder1.setEnabled(false);
        mainPanel.add(disabledInBorder1.withBorder(Borders.singleLine("border")));

        Button first = new Button("enabled");
        mainPanel.add(first);

        Button disabled = new Button("disabled");
        disabled.setEnabled(false);
        mainPanel.add(disabled);

        Button disabledInBorder2 = new Button("disabledB2");
        disabledInBorder2.setEnabled(false);
        mainPanel.add(disabledInBorder2.withBorder(Borders.singleLine("border")));

        Button anotherFocusable = new Button("focusable");
        mainPanel.add(anotherFocusable);

        mainPanel.add(new Button("Button"));

        first.grabFocus();
        gui.addWindowAndWait(window);
    }

}

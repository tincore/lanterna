package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.Dimension;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;

public class Issue387 {

    public static void main(String[] args) {
        try {
            Screen screen = new DefaultTerminalFactory().createScreen();
            screen.start();

            Window window = new BasicWindow();

            Table<String> table = new Table<>("Column");
            table.setVisibleRows(3);

            table.getTableModel().addRow("row 1");
            table.getTableModel().addRow("row 2");
            table.getTableModel().addRow("row 3");
            table.getTableModel().addRow("row 4");
            table.getTableModel().addRow("row 5");
            table.getTableModel().addRow("row 6");
            table.getTableModel().addRow("row 7");

            Panel panel = new Panel();
            panel.add(new TextBox());
            panel.add(new EmptySpace(new Dimension(15, 1)));
            panel.add(table);
            panel.add(new EmptySpace(new Dimension(15, 1)));
            panel.add(new TextBox());

            window.setComponent(panel);

            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
            gui.addWindowAndWait(window);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

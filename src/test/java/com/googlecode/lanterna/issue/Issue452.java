/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
 *
 * lanterna is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.TextBox.Style;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.gui2.menu.MenuItem;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.MouseCaptureMode;

import java.io.IOException;

/**
 * <p>
 * Serves as showcase for all {@link Interactable} components for manual testing
 * during development of mouse support. Uses Telnet port 23000 as you need
 * something different than swing terminal provided by IDE. After launching main
 * method you can connect to it via terminal "telnet localhost 23000" (or
 * something of that nature)
 * <p>
 * Automatic tests can be found in {@link Issue452Test}.
 */
public class Issue452 {

    private static final int GRID_WIDTH = 100;
    private static final LayoutData LAYOUT_NEW_ROW = GridLayout.createHorizontallyFilledLayoutData(GRID_WIDTH);
    private static int buttonTriggeredCounter = 0;
    private static TextBox actionListTextBox;
    private static TextBox menuTextBox;
    private static TextBox tableTextBox;
    private static int tableTriggeredCounter = 0;

    private static void addInteractableComponentsToContent(Panel content) {
        // for menu bar so you know which menu you have triggered
        menuTextBox = new TextBox("Try menu above");
        content.add(menuTextBox, LAYOUT_NEW_ROW);

        // single line textbox
        content.add(new TextBox("Single line TextBox"), LAYOUT_NEW_ROW);

        // multi line textbox
        content.add(new TextBox(
            "First line of multi line TextBox" + System.lineSeparator() + "Second line of multi line TextBox",
            Style.MULTI_LINE), LAYOUT_NEW_ROW);

        // checkbox
        content.add(new CheckBox("CheckBox"), LAYOUT_NEW_ROW);

        // button
        TextBox textBoxButton = new TextBox("Click the button!");
        Button button = new Button("Button").setClickListener(i -> textBoxButton.setText("Button triggered " + Issue452.buttonTriggeredCounter++ + " times"));

        content.add(button, GridLayout.createHorizontallyFilledLayoutData(1));
        content.add(textBoxButton, GridLayout.createHorizontallyFilledLayoutData(GRID_WIDTH - 1));

        // action list box
        actionListTextBox = new TextBox("Click on something in the action list!");
        ActionListBox actionMenu = new ActionListBox();
        actionMenu.addItem("First menu", s -> actionListTextBox.setText("First menu clicked"));
        actionMenu.addItem("Second menu", s -> actionListTextBox.setText("Second menu clicked"));
        actionMenu.addItem("Third menu", s -> actionListTextBox.setText("Third menu clicked"));
        content.add(actionListTextBox, LAYOUT_NEW_ROW);
        content.add(actionMenu, LAYOUT_NEW_ROW);

        // radiobox list
        RadioBoxList<String> list = new RadioBoxList<>();
        list.addItem("RadioGaga");
        list.addItem("RadioGogo");
        list.addItem("RadioBlaBla");
        content.add(list, LAYOUT_NEW_ROW);

        // Table
        tableTextBox = new TextBox("Try table bellow");
        Table<String> table = new Table<>("Column0000000", "Column111", "Column22222");
        table.getTableModel()
            .addRow("0", "0", "0")
            .addRow("1", "1", "1")
            .addRow("2", "2", "2");
        table.setClickListener(s -> {
            tableTriggeredCounter++;
            tableTextBox.setText("Table's action runned " + tableTriggeredCounter + " times");
        });
        table.setCellSelection(true);
        content.add(tableTextBox, LAYOUT_NEW_ROW);
        content.add(table, LAYOUT_NEW_ROW);
    }

    private static void addMenuBar(Window window) {
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Settings");
        menu.add(new MenuItem("Menu1", s -> {
            menuTextBox.setText("Menu1 clicked");
            menuTextBox.invalidate();
        }));
        menu.add(new MenuItem("Menu2", s -> {
            menuTextBox.setText("Menu2 clicked");
            menuTextBox.invalidate();
        }));
        menu.add(new MenuItem("Menu3", s -> {
            menuTextBox.setText("Menu3 clicked");
            menuTextBox.invalidate();
        }));
        menuBar.add(menu);
        window.setMenuBar(menuBar);
    }

    public static void main(String[] args) throws IOException {
        try (Screen screen = new DefaultTerminalFactory().setTelnetPort(23000)
            .setMouseCaptureMode(MouseCaptureMode.CLICK_RELEASE_DRAG_MOVE).setInitialTerminalSize(new TerminalSize(100, 100))
            .createScreen()) {
            screen.start();
            WindowBasedTextGUI gui = new MultiWindowTextGUI(screen);
            Window window = new BasicWindow("Issue452");
            Panel content = new Panel(new GridLayout(GRID_WIDTH));
            GridLayout gridLayout = (GridLayout) content.getLayoutManager();
            gridLayout.setVerticalSpacing(1);
            addInteractableComponentsToContent(content);
            addMenuBar(window);
            window.setComponent(content);
            gui.addWindowAndWait(window);
        }
    }

}

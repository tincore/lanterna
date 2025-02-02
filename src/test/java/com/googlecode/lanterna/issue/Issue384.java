package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.Dimension;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.gui2.table.DefaultTableRenderer;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.table.TableModel;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import static com.googlecode.lanterna.gui2.Panels.horizontal;
import static com.googlecode.lanterna.gui2.Panels.vertical;
import static com.googlecode.lanterna.gui2.AbstractGuiTest.createButtonCloseContainer;

public class Issue384 {
    private static final Set<Integer> EXPANDABLE_COLUMNS = new TreeSet<>(Collections.singletonList(1));

    public static void main(String[] args) throws IOException {
        final Screen screen = new DefaultTerminalFactory().createScreen();
        screen.start();
        final MultiWindowFrame textGUI = new MultiWindowFrame(screen);
        final Window window = new BasicWindow("Table container test");
        window.setHints(Collections.singletonList(Window.Hint.FIXED_SIZE));
        window.setFixedSize(new Dimension(60, 14));

        final Table<String> table = new Table<>("Column", "Expanded Column", "Column");
        table.setCellSelection(true);
        table.setVisibleRows(10);
        final DefaultTableRenderer<String> tableRenderer = new DefaultTableRenderer<>();
        tableRenderer.setExpandableColumns(Collections.singletonList(1));
        table.setRenderer(tableRenderer);

        final TableModel<String> model = table.getTableModel();
        for (int i = 1; i <= 20; i++) {
            String cellLabel = "Row" + i;
            model.addRow(cellLabel, cellLabel, cellLabel);
        }

        window.setComponent(vertical(
            table.withBorder(Borders.singleLineBevel("Table")),
            horizontal(
                new Button("Change Expandable Columns", s -> showExpandableColumnsEditor(textGUI, tableRenderer)),
                createButtonCloseContainer())));
        table.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        textGUI.addWindow(window);
        textGUI.waitForWindowToClose(window);
        screen.stop();
    }

    private static void showExpandableColumnsEditor(MultiWindowFrame textGUI, final DefaultTableRenderer<String> tableRenderer) {
        final DialogWindow dialogWindow = new DialogWindow("Select expandable columns") {
        };
        Panel contentPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        final CheckBoxList<String> checkBoxList = new CheckBoxList<>();
        checkBoxList.addItem("Column1", EXPANDABLE_COLUMNS.contains(0));
        checkBoxList.addItem("Column2", EXPANDABLE_COLUMNS.contains(1));
        checkBoxList.addItem("Column3", EXPANDABLE_COLUMNS.contains(2));
        contentPanel.add(checkBoxList);
        contentPanel.add(new Button("OK", s -> {
            EXPANDABLE_COLUMNS.clear();
            for (int i = 0; i < 3; i++) {
                if (checkBoxList.isChecked(i)) {
                    EXPANDABLE_COLUMNS.add(i);
                }
            }
            tableRenderer.setExpandableColumns(EXPANDABLE_COLUMNS);
            dialogWindow.close();
        }), LinearLayout.createLayoutData(LinearLayout.Alignment.End));
        dialogWindow.setComponent(contentPanel);
        dialogWindow.show(textGUI);
    }
}


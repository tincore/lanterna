/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
 *
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.ListSelectDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;
import com.googlecode.lanterna.gui2.table.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Test for the Table component
 */
public class
TableTest extends TestBase {
    private int columnCounter = 4;

    public static void main(String[] args) throws IOException, InterruptedException {
        new TableTest().run(args);
    }

    private String askForANumber(WindowBasedTextGUI textGUI, String title) {
        return askForANumber(textGUI, title, "");
    }

    private String askForANumber(WindowBasedTextGUI textGUI, String title, String initialNumber) {
        return new TextInputDialogBuilder()
            .title(title)
            .setInitialContent(initialNumber)
            .setValidationPattern(Pattern.compile("[0-9]+"), "Not a number")
            .build()
            .show(textGUI);
    }

    private String askForAString(WindowBasedTextGUI textGUI, String title) {
        return new TextInputDialogBuilder()
            .title(title)
            .build()
            .show(textGUI);
    }

    private String chooseAString(WindowBasedTextGUI textGUI, String title, String... items) {
        return new ListSelectDialogBuilder<String>()
            .title(title)
            .addListItems(items)
            .build()
            .show(textGUI);
    }

    @Override
    public void init(final WindowBasedTextGUI textGUI) {
        final Window window = new BasicWindow("Table container test").setHints(Window.Hint.FIT_TERMINAL_WINDOW);

        final Table<String> table = new Table<String>("Column 1", "Column 2", "Column 3")
            .setTableCellRenderer(new DefaultTableCellRenderer<String>() {
                @Override
                protected void applyStyle(Table<String> table, String cell, int columnIndex, int rowIndex, boolean isSelected, TextGUIGraphics textGUIGraphics) {
                    super.applyStyle(table, cell, columnIndex, rowIndex, isSelected, textGUIGraphics);
                    if (columnIndex == 1) {
                        textGUIGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
                        textGUIGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                        if (isSelected) {
                            textGUIGraphics.enableModifiers(SGR.REVERSE);
                        }
                    }
                }
            });

        final TableModel<String> model = table.getTableModel();
        for (int i = 1; i < 30; i++) {
            model.addRow("Row" + i, "Row" + i, "Row" + i);
        }

        window.setComponent(Panels.vertical(
            table.withBorder(Borders.singleLineBevel("Table")),
            new Panel(new LinearLayout(Direction.HORIZONTAL))
                .add(new Button("Add...",
                    s -> new ActionListDialogBuilder()
                        .title("Add to table")
                        .item("Row", () -> {
                            List<String> labels = new ArrayList<>();
                            for (int i = 0; i < model.getColumnCount(); i++) {
                                labels.add("Row" + (model.getRowCount() + 1));
                            }
                            model.addRow(labels.toArray(new String[0]));
                            table.invalidate();
                        })
                        .item("5 Rows", () -> {
                            for (int row = 0; row < 5; row++) {
                                List<String> labels = new ArrayList<>();
                                for (int i = 0; i < model.getColumnCount(); i++) {
                                    labels.add("Row" + (model.getRowCount() + 1));
                                }
                                model.addRow(labels.toArray(new String[0]));
                            }
                            table.invalidate();
                        })
                        .item("Column", () -> {
                            List<String> labels = new ArrayList<>();
                            for (int i = 0; i < model.getRowCount(); i++) {
                                labels.add("Row" + (i + 1));
                            }
                            model.addColumn("Column " + (columnCounter++), labels.toArray(new String[0]));
                            table.invalidate();
                        })
                        .build()
                        .show(textGUI)))
                .add(new Button("Modify...", s -> onModify(textGUI, table)))
                .add(new Button("Remove...",
                    s -> new ActionListDialogBuilder()
                        .title("Remove from table")
                        .item("Row", () -> {
                            String numberAsText = askForANumber(textGUI, "Enter row # to remove (0-" + (model.getRowCount() - 1) + ")");
                            if (numberAsText != null) {
                                model.removeRow(Integer.parseInt(numberAsText));
                            }
                        })
                        .item("Column", () -> {
                            String numberAsText = askForANumber(textGUI, "Enter column # to remove (0-" + (model.getColumnCount() - 1) + ")");
                            if (numberAsText != null) {
                                model.removeColumn(Integer.parseInt(numberAsText));
                            }
                        })
                        .build()
                        .show(textGUI)))
                .add(new Button("Close", s -> window.close()))));
        textGUI.addWindow(window);
    }

    private void onModify(WindowBasedTextGUI textGUI, Table<String> table) {
        String[] dialogChoices = new String[]{
            "Change table content",
            "Change table style",
            "Change view size",
            "Force re-calculate/re-draw"
        };
        String choice = chooseAString(textGUI, "Modify what?", dialogChoices);
        //noinspection StatementWithEmptyBody
        if (choice == null) {
        } else if (choice.equals(dialogChoices[0])) {
            onModifyContent(textGUI, table);
        } else if (choice.equals(dialogChoices[1])) {
            onModifyStyle(textGUI, table);
        } else if (choice.equals(dialogChoices[2])) {
            onModifyViewSize(textGUI, table);
        } else if (choice.equals(dialogChoices[3])) {
            table.invalidate();
        }
    }

    private void onModifyContent(WindowBasedTextGUI textGUI, Table<String> table) {
        TableModel<String> model = table.getTableModel();
        String columnIndexAsText = askForANumber(textGUI, "Enter column # to modify (0-" + (model.getColumnCount() - 1) + ")");
        if (columnIndexAsText == null) {
            return;
        }
        String rowIndexAsText = askForANumber(textGUI, "Enter row # to modify (0-" + (model.getRowCount() - 1) + ")");
        if (rowIndexAsText == null) {
            return;
        }
        String newLabel = askForAString(textGUI, "Enter new label for the table cell at row " + rowIndexAsText + " column " + columnIndexAsText);
        if (newLabel != null) {
            model.setCell(Integer.parseInt(columnIndexAsText), Integer.parseInt(rowIndexAsText), newLabel);
        }
    }

    private void onModifyStyle(WindowBasedTextGUI textGUI, Table<String> table) {
        String[] dialogChoices = new String[]{
            "Header border style (vertical)",
            "Header border style (horizontal)",
            "Cell border style (vertical)",
            "Cell border style (horizontal)",
            "Toggle cell selection"
        };
        String choice = chooseAString(textGUI, "Which style do you want to change?", dialogChoices);
        DefaultTableRenderer<String> renderer = (DefaultTableRenderer<String>) table.getRenderer();
        if (choice == null) {
            return;
        } else if (choice.equals(dialogChoices[4])) {
            table.setCellSelection(!table.isCellSelection());
        } else {
            TableCellBorderStyle newStyle = new ListSelectDialogBuilder<TableCellBorderStyle>()
                .title("Choose a new style")
                .addListItems(TableCellBorderStyle.values())
                .build()
                .show(textGUI);
            if (newStyle != null) {
                if (choice.equals(dialogChoices[0])) {
                    renderer.setHeaderVerticalBorderStyle(newStyle);
                } else if (choice.equals(dialogChoices[1])) {
                    renderer.setHeaderHorizontalBorderStyle(newStyle);
                } else if (choice.equals(dialogChoices[2])) {
                    renderer.setCellVerticalBorderStyle(newStyle);
                } else if (choice.equals(dialogChoices[3])) {
                    renderer.setCellHorizontalBorderStyle(newStyle);
                }
            }
        }
        table.invalidate();
    }

    private void onModifyViewSize(WindowBasedTextGUI textGUI, Table<String> table) {
        String verticalViewSize = askForANumber(textGUI, "Enter number of rows to display at once (0 = all)");
        if (verticalViewSize == null) {
            return;
        }
        table.setVisibleRows(Integer.parseInt(verticalViewSize));
        String horizontalViewSize = askForANumber(textGUI, "Enter number of columns to display at once (0 = all)");
        if (horizontalViewSize == null) {
            return;
        }
        table.setVisibleColumns(Integer.parseInt(horizontalViewSize));
    }
}

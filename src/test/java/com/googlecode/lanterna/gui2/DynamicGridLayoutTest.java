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

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.gui2.dialogs.ListSelectDialog;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import java.util.regex.Pattern;

public class DynamicGridLayoutTest extends TestBase {
    private static final TextColor[] GOOD_COLORS = new TextColor[]{
        TextColor.ANSI.RED, TextColor.ANSI.BLUE, TextColor.ANSI.CYAN,
        TextColor.ANSI.GREEN, TextColor.ANSI.MAGENTA, TextColor.ANSI.YELLOW
    };
    private static final Random RANDOM = new Random();

    public static void main(String[] args) throws IOException, InterruptedException {
        new DynamicGridLayoutTest().run(args);
    }

    private TextColor getRandomColor() {
        return GOOD_COLORS[RANDOM.nextInt(GOOD_COLORS.length)];
    }

    @Override
    public void init(final WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("Grid layout test");

        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL).setSpacing(1));

        final Panel gridPanel = new Panel();
        GridLayout gridLayout = newGridLayout(4);
        gridPanel.setLayoutManager(gridLayout);

        for (int i = 0; i < 16; i++) {
            gridPanel.add(new EmptySpace(getRandomColor(), new TerminalSize(4, 1)));
        }

        Panel controlPanel = new Panel();
        controlPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        controlPanel.add(new Button("Add Component", s -> onAddComponent(textGUI, gridPanel)));
        controlPanel.add(new Button("Modify Component", s -> onModifyComponent(textGUI, gridPanel)));
        controlPanel.add(new Button("Modify Grid", s -> onModifyGrid(textGUI, (GridLayout) gridPanel.getLayoutManager())));
        controlPanel.add(new Button("Reset Grid", s -> onResetGrid(textGUI, gridPanel)));
        controlPanel.add(new Button("Exit", s -> window.close()));

        mainPanel.add(gridPanel);
        mainPanel.add(
            new Separator(Direction.HORIZONTAL)
                .setLayoutData(
                    LinearLayout.createLayoutData(LinearLayout.Alignment.Fill)));
        mainPanel.add(controlPanel);

        window.setComponent(mainPanel);
        textGUI.addWindow(window);
    }

    private GridLayout newGridLayout(int columns) {
        GridLayout gridLayout = new GridLayout(columns);
        gridLayout.setTopMarginSize(1);
        gridLayout.setVerticalSpacing(1);
        gridLayout.setHorizontalSpacing(1);
        return gridLayout;
    }

    private void onAddComponent(WindowBasedTextGUI textGUI, Panel gridPanel) {
        SelectableComponentType componentType = ListSelectDialog.showDialog(
            textGUI,
            "Add Component",
            "Select component to add",
            SelectableComponentType.values());
        if (componentType == null) {
            return;
        }
        Component component = null;
        switch (componentType) {
            case Block:
            case TextBox:
                String sizeString = new TextInputDialogBuilder()
                    .setInitialContent(componentType == SelectableComponentType.Block ? "4x1" : "16x1")
                    .title("Add " + componentType)
                    .description("Enter size of " + componentType + " (<columns>x<rows>)")
                    .setValidationPattern(Pattern.compile("[0-9]+x[0-9]+"), "Invalid format, please use <columns>x<rows>")
                    .build()
                    .show(textGUI);
                if (sizeString == null) {
                    return;
                }
                TerminalSize size = new TerminalSize(Integer.parseInt(sizeString.split("x")[0]), Integer.parseInt(sizeString.split("x")[1]));
                component = componentType == SelectableComponentType.Block ? new EmptySpace(getRandomColor(), size) : new TextBox(size);
                break;

            case Label:
                String text = TextInputDialog.showDialog(textGUI, "Add " + componentType, "Enter the text of the new Label", "Label");
                component = new Label(text);
                break;
        }
        gridPanel.add(component);
    }

    private void onModifyComponent(WindowBasedTextGUI textGUI, Panel panel) {
        Component[] components = panel.getChildren().toArray(new Component[panel.getComponentCount()]);
        Component component = ListSelectDialog.showDialog(textGUI, "Modify Component", "Select component to modify", 10, components);
        if (component == null) {
            return;
        }

        GridLayoutDataEditor gridLayoutDataEditor = new GridLayoutDataEditor(component);
        gridLayoutDataEditor.show(textGUI);
    }

    private void onModifyGrid(WindowBasedTextGUI textGUI, GridLayout gridLayout) {
        GridLayoutEditor gridLayoutEditor = new GridLayoutEditor(gridLayout);
        gridLayoutEditor.show(textGUI);
    }

    private void onResetGrid(WindowBasedTextGUI textGUI, Panel gridPanel) {
        BigInteger columns = TextInputDialog.showNumberDialog(textGUI, "Reset Grid", "Reset grid to how many columns?", "4");
        if (columns == null) {
            return;
        }
        BigInteger prepopulate = TextInputDialog.showNumberDialog(
            textGUI,
            "Reset Grid",
            "Pre-populate grid with how many dummy components?",
            columns.toString());
        gridPanel.removeAllComponents();
        gridPanel.setLayoutManager(newGridLayout(columns.intValue()));
        //noinspection ConstantConditions
        for (int i = 0; i < prepopulate.intValue(); i++) {
            gridPanel.add(new EmptySpace(getRandomColor(), new TerminalSize(4, 1)));
        }
    }

    private enum SelectableComponentType {
        Block,
        Label,
        TextBox,
    }

    private static class GridLayoutEditor extends DialogWindow {
        public GridLayoutEditor(final GridLayout gridLayout) {
            super("GridLayoutData Editor");

            Pattern numberPattern = Pattern.compile("[0-9]+");

            Panel contentPane = new Panel();
            contentPane.setLayoutManager(new GridLayout(2));
            contentPane.add(new Label("Horizontal spacing:"));
            final TextBox textBoxHorizontalSpacing = new TextBox();
            textBoxHorizontalSpacing.setText(gridLayout.getHorizontalSpacing() + "");
            textBoxHorizontalSpacing.setValidationPattern(numberPattern);
            contentPane.add(textBoxHorizontalSpacing);

            contentPane.add(new Label("Vertical spacing:"));
            final TextBox textBoxVerticalSpacing = new TextBox();
            textBoxVerticalSpacing.setText(gridLayout.getVerticalSpacing() + "");
            textBoxVerticalSpacing.setValidationPattern(numberPattern);
            contentPane.add(textBoxVerticalSpacing);

            contentPane.add(new Label("Left margin:"));
            final TextBox textBoxLeftMargin = new TextBox();
            textBoxLeftMargin.setText(gridLayout.getLeftMarginSize() + "");
            textBoxLeftMargin.setValidationPattern(numberPattern);
            contentPane.add(textBoxLeftMargin);

            contentPane.add(new Label("Right margin:"));
            final TextBox textBoxRightMargin = new TextBox();
            textBoxRightMargin.setText(gridLayout.getRightMarginSize() + "");
            textBoxRightMargin.setValidationPattern(numberPattern);
            contentPane.add(textBoxRightMargin);

            contentPane.add(new Label("Top margin:"));
            final TextBox textBoxTopMargin = new TextBox();
            textBoxTopMargin.setText(gridLayout.getTopMarginSize() + "");
            textBoxTopMargin.setValidationPattern(numberPattern);
            contentPane.add(textBoxTopMargin);

            contentPane.add(new Label("Bottom margin:"));
            final TextBox textBoxBottomMargin = new TextBox();
            textBoxBottomMargin.setText(gridLayout.getBottomMarginSize() + "");
            textBoxBottomMargin.setValidationPattern(numberPattern);
            contentPane.add(textBoxBottomMargin);

            contentPane.add(
                new EmptySpace(TerminalSize.ONE).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2)));
            contentPane.add(
                new Separator(Direction.HORIZONTAL).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2)));
            contentPane.add(
                new EmptySpace(TerminalSize.ONE).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2)));

            Button okButton = new Button("OK", s -> {
                gridLayout.setHorizontalSpacing(Integer.parseInt(textBoxHorizontalSpacing.getTextOrDefault("0")));
                gridLayout.setVerticalSpacing(Integer.parseInt(textBoxVerticalSpacing.getTextOrDefault("0")));
                gridLayout.setLeftMarginSize(Integer.parseInt(textBoxLeftMargin.getTextOrDefault("0")));
                gridLayout.setRightMarginSize(Integer.parseInt(textBoxRightMargin.getTextOrDefault("0")));
                gridLayout.setTopMarginSize(Integer.parseInt(textBoxTopMargin.getTextOrDefault("0")));
                gridLayout.setBottomMarginSize(Integer.parseInt(textBoxBottomMargin.getTextOrDefault("0")));
                close();
            });
            Button cancelButton = new Button("Cancel", s -> this.close());

            contentPane.add(
                Panels.horizontal(okButton, cancelButton)
                    .setLayoutData(GridLayout.createHorizontallyEndAlignedLayoutData(2)));
            setComponent(contentPane);
        }


    }

    private static class GridLayoutDataEditor extends DialogWindow {
        public GridLayoutDataEditor(final Component component) {
            super("GridLayoutData Editor");

            GridLayout.GridLayoutData gridLayoutData = (GridLayout.GridLayoutData) component.getLayoutData();
            if (gridLayoutData == null) {
                gridLayoutData = (GridLayout.GridLayoutData) GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.BEGINNING);
            }

            Panel contentPane = new Panel();
            contentPane.setLayoutManager(new GridLayout(2));
            contentPane.add(new Label("Horizontal alignment:"));
            final RadioBoxList<GridLayout.Alignment> radioBoxesHorizontalAlignment = new RadioBoxList<>();
            radioBoxesHorizontalAlignment.addItem(GridLayout.Alignment.BEGINNING);
            radioBoxesHorizontalAlignment.addItem(GridLayout.Alignment.CENTER);
            radioBoxesHorizontalAlignment.addItem(GridLayout.Alignment.END);
            radioBoxesHorizontalAlignment.addItem(GridLayout.Alignment.FILL);
            radioBoxesHorizontalAlignment.setCheckedItem(gridLayoutData.horizontalAlignment);
            contentPane.add(radioBoxesHorizontalAlignment);

            contentPane.add(
                new EmptySpace(TerminalSize.ONE).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2)));

            contentPane.add(new Label("Vertical alignment:"));
            final RadioBoxList<GridLayout.Alignment> radioBoxesVerticalAlignment = new RadioBoxList<>();
            radioBoxesVerticalAlignment.addItem(GridLayout.Alignment.BEGINNING);
            radioBoxesVerticalAlignment.addItem(GridLayout.Alignment.CENTER);
            radioBoxesVerticalAlignment.addItem(GridLayout.Alignment.END);
            radioBoxesVerticalAlignment.addItem(GridLayout.Alignment.FILL);
            radioBoxesVerticalAlignment.setCheckedItem(gridLayoutData.verticalAlignment);
            contentPane.add(radioBoxesVerticalAlignment);

            contentPane.add(
                new EmptySpace(TerminalSize.ONE).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2)));

            contentPane.add(new Label("Grab extra horizontal space:"));
            final CheckBox checkBoxGrabExtraHorizontalSpace = new CheckBox("");
            checkBoxGrabExtraHorizontalSpace.setChecked(gridLayoutData.grabExtraHorizontalSpace);
            contentPane.add(checkBoxGrabExtraHorizontalSpace);

            contentPane.add(new Label("Grab extra vertical space:"));
            final CheckBox checkBoxGrabExtraVerticalSpace = new CheckBox("");
            checkBoxGrabExtraVerticalSpace.setChecked(gridLayoutData.grabExtraVerticalSpace);
            contentPane.add(checkBoxGrabExtraVerticalSpace);

            contentPane.add(
                new EmptySpace(TerminalSize.ONE).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2)));

            Pattern numberPattern = Pattern.compile("[1-9][0-9]*");

            contentPane.add(new Label("Horizontal span:"));
            final TextBox textBoxHorizontalSpan = new TextBox(new TerminalSize(5, 1), gridLayoutData.horizontalSpan + "");
            textBoxHorizontalSpan.setValidationPattern(numberPattern);
            contentPane.add(textBoxHorizontalSpan);

            contentPane.add(new Label("Vertical span:"));
            final TextBox textBoxVerticalSpan = new TextBox(new TerminalSize(5, 1), gridLayoutData.verticalSpan + "");
            textBoxVerticalSpan.setValidationPattern(numberPattern);
            contentPane.add(textBoxVerticalSpan);

            contentPane.add(
                new EmptySpace(TerminalSize.ONE).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2)));
            contentPane.add(
                new Separator(Direction.HORIZONTAL).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2)));
            contentPane.add(
                new EmptySpace(TerminalSize.ONE).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2)));

            Button okButton = new Button("OK", s -> {
                component.setLayoutData(
                    GridLayout.createLayoutData(
                        radioBoxesHorizontalAlignment.getCheckedItem(),
                        radioBoxesVerticalAlignment.getCheckedItem(),
                        checkBoxGrabExtraHorizontalSpace.isChecked(),
                        checkBoxGrabExtraVerticalSpace.isChecked(),
                        Integer.parseInt(textBoxHorizontalSpan.getTextOrDefault("1")),
                        Integer.parseInt(textBoxVerticalSpan.getTextOrDefault("1"))));
                close();
            });
            Button cancelButton = new Button("Cancel", s -> this.close());

            contentPane.add(
                Panels.horizontal(okButton, cancelButton)
                    .setLayoutData(GridLayout.createHorizontallyEndAlignedLayoutData(2)));
            setComponent(contentPane);
        }
    }
}

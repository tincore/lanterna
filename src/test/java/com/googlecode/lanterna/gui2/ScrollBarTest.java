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

import java.io.IOException;
import java.util.regex.Pattern;

public class ScrollBarTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new ScrollBarTest().run(args);
    }

    private int getInteger(String text, int defaultValue) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public void init(WindowBasedTextGUI textGUI) {
        final BasicWindow basicWindow = new BasicWindow("ScrollBar test");
        Panel contentPanel = new Panel();
        contentPanel.setLayoutManager(new GridLayout(2));

        Panel controlPanel = new Panel();
        final CheckBox checkVerticalTrackerGrow = new CheckBox().setChecked(true);
        final CheckBox checkHorizontalTrackerGrow = new CheckBox().setChecked(true);
        final TextBox textBoxVerticalSize = new TextBox("10").setValidationPattern(Pattern.compile("[0-9]+"));
        final TextBox textBoxHorizontalSize = new TextBox("10").setValidationPattern(Pattern.compile("[0-9]+"));
        final TextBox textBoxVerticalPosition = new TextBox("0").setValidationPattern(Pattern.compile("[0-9]+"));
        final TextBox textBoxHorizontalPosition = new TextBox("0").setValidationPattern(Pattern.compile("[0-9]+"));
        final TextBox textBoxVerticalMax = new TextBox("100").setValidationPattern(Pattern.compile("[0-9]+"));
        final TextBox textBoxHorizontalMax = new TextBox("100").setValidationPattern(Pattern.compile("[0-9]+"));
        final ScrollBar verticalScroll = new ScrollBar(Direction.VERTICAL);
        final ScrollBar horizontalScroll = new ScrollBar(Direction.HORIZONTAL);
        Button buttonRefresh = new Button("Refresh", s -> {
            ((ScrollBar.DefaultScrollBarRenderer) verticalScroll.getRenderer()).setGrowScrollTracker(checkVerticalTrackerGrow.isChecked());
            verticalScroll.setScrollMaximum(getInteger(textBoxVerticalMax.getText(), 100));
            verticalScroll.setScrollPosition(getInteger(textBoxVerticalPosition.getText(), 100));
            verticalScroll.setViewSize(getInteger(textBoxVerticalSize.getText(), 1));
            ((ScrollBar.DefaultScrollBarRenderer) horizontalScroll.getRenderer()).setGrowScrollTracker(checkHorizontalTrackerGrow.isChecked());
            horizontalScroll.setScrollMaximum(getInteger(textBoxHorizontalMax.getText(), 0));
            horizontalScroll.setScrollPosition(getInteger(textBoxHorizontalPosition.getText(), 0));
            horizontalScroll.setViewSize(getInteger(textBoxHorizontalSize.getText(), 1));
        });
        Button closeButton = new Button("Close", s -> basicWindow.close());

        verticalScroll.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.FILL, false, true));
        horizontalScroll.setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2));
        buttonRefresh.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.BEGINNING, true, true, 2, 1));

        contentPanel.add(controlPanel.withBorder(Borders.singleLine("Control")));
        contentPanel.add(verticalScroll);
        contentPanel.add(horizontalScroll);

        controlPanel.setLayoutManager(new GridLayout(2));
        controlPanel.add(new Label("Vertical tracker grows:")).add(checkVerticalTrackerGrow);
        controlPanel.add(new Label("Vertical view size:")).add(textBoxVerticalSize);
        controlPanel.add(new Label("Vertical scroll position:")).add(textBoxVerticalPosition);
        controlPanel.add(new Label("Vertical scroll max:")).add(textBoxVerticalMax);
        controlPanel.add(new EmptySpace(TerminalSize.ONE)).add(new EmptySpace(TerminalSize.ONE));
        controlPanel.add(new Label("Horizontal tracker grows:")).add(checkHorizontalTrackerGrow);
        controlPanel.add(new Label("Horizontal view size:")).add(textBoxHorizontalSize);
        controlPanel.add(new Label("Horizontal scroll position:")).add(textBoxHorizontalPosition);
        controlPanel.add(new Label("Horizontal scroll max:")).add(textBoxHorizontalMax);
        controlPanel.add(new EmptySpace(TerminalSize.ONE)).add(new EmptySpace(TerminalSize.ONE));
        controlPanel.add(buttonRefresh);
        contentPanel.add(closeButton);

        basicWindow.setComponent(contentPanel);
        textGUI.addWindow(basicWindow);
    }
}

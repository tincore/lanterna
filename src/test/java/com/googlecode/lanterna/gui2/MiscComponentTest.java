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

import com.googlecode.lanterna.Dimension;
import com.googlecode.lanterna.TestUtils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MiscComponentTest extends AbstractGuiTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        new MiscComponentTest().run(args);
    }

    @Override
    public void init(WindowFrame textGUI) {
        final BasicWindow window = new BasicWindow("Grid layout test");
        final Panel leftPanel = new Panel();
        Panel checkBoxPanel = new Panel();
        for (int i = 0; i < 4; i++) {
            CheckBox checkBox = new CheckBox("Checkbox #" + (i + 1));
            checkBoxPanel.add(checkBox);
        }

        Panel textBoxPanel = new Panel();
        textBoxPanel.add(Panels.horizontal(new Label("Normal:   "), new TextBox(new Dimension(12, 1), "Text")));
        textBoxPanel.add(Panels.horizontal(new Label("Password: "), new TextBox(new Dimension(12, 1), "Text").setMask('*')));

        Panel buttonPanel = new Panel();
        buttonPanel.add(new Button("Enable spacing", s -> {
            LinearLayout layoutManager = (LinearLayout) leftPanel.getLayoutManager();
            layoutManager.setSpacing(layoutManager.getSpacing() == 0 ? 1 : 0);
        }));

        leftPanel.add(checkBoxPanel.withBorder(Borders.singleLine("CheckBoxes")));
        leftPanel.add(textBoxPanel.withBorder(Borders.singleLine("TextBoxes")));
        leftPanel.add(buttonPanel.withBorder(Borders.singleLine("Buttons")));

        Panel rightPanel = new Panel();
        textBoxPanel = new Panel();
        TextBox readOnlyTextArea = new TextBox(new Dimension(16, 8));
        readOnlyTextArea.setReadOnly(true);
        readOnlyTextArea.setText(TestUtils.downloadGPL());
        textBoxPanel.add(readOnlyTextArea);
        rightPanel.add(textBoxPanel.withBorder(Borders.singleLine("Read-only")));
        final ProgressBar progressBar = new ProgressBar(0, 100, 16);
        progressBar.setRenderer(new ProgressBar.LargeProgressBarRenderer());
        progressBar.setLabelFormat("%2.0f%%");
        rightPanel.add(progressBar.withBorder(Borders.singleLine("ProgressBar")));
        rightPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));

        final Timer timer = new Timer("ProgressBar-timer", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (progressBar.getValue() == progressBar.getMax()) {
                    progressBar.setValue(0);
                } else {
                    progressBar.setValue(progressBar.getValue() + 1);
                }
            }
        }, 250, 250);

        Panel contentArea = new Panel();
        contentArea.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        contentArea.add(Panels.horizontal(leftPanel, rightPanel));
        contentArea.add(
            new Separator(Direction.HORIZONTAL).setLayoutData(
                LinearLayout.createLayoutData(LinearLayout.Alignment.Fill)));
        Button okButton = new Button("OK", s -> {
            window.close();
            timer.cancel();
        }).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        contentArea.add(okButton);
        window.setComponent(contentArea);
        window.setFocusedInteractable(okButton);
        textGUI.addWindow(window);
    }
}

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
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.dialogs.FileDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.gui2.menu.MenuItem;

import java.io.File;
import java.io.IOException;

public class MenuTest extends AbstractGuiTest {
    private static final Interactable.ClickListener DO_NOTHING = s -> {
    };
    private static final String[] GERMANY_STATES = new String[]{
        "Baden-Württemberg", "Bayern", "Berlin", "Brandenburg", "Bremen", "Hamburg", "Hessen", "Mecklenburg-Vorpommern",
        "Niedersachsen", "Nordrhein-Westfalen", "Rheinland-Pfalz", "Saarland", "Sachsen", "Sachsen-Anhalt",
        "Schleswig-Holstein", "Thüringen",
    };
    private static final String[] JAPAN_PREFECTURES = new String[]{
        "Aichi", "Akita", "Aomori", "Chiba", "Ehime", "Fukui", "Fukuoka", "Fukushima", "Gifu", "Gunma", "Hiroshima", "Hokkaido",
        "Hyōgo", "Ibaraki", "Ishikawa", "Iwate", "Kagawa", "Kagoshima", "Kanagawa", "Kōchi", "Kumamoto", "Kyoto", "Mie",
        "Miyagi", "Miyazaki", "Nagano", "Nagasaki", "Nara", "Niigata", "Ōita", "Okayama", "Okinawa", "Osaka", "Saga", "Saitama",
        "Shiga", "Shimane", "Shizuoka", "Tochigi", "Tokushima", "Tokyo", "Tottori", "Toyama", "Wakayama", "Yamagata",
        "Yamaguchi", "Yamanashi",
    };

    public static void main(String[] args) throws IOException, InterruptedException {
        new MenuTest().run(args);
    }

    @Override
    public void init(final WindowBasedTextGUI textGUI) {
        // Create window to hold the menu
        final BasicWindow window = new BasicWindow();
        Panel contentPane = new Panel(new BorderLayout())
            .add(Panels.vertical(
                new Separator(Direction.HORIZONTAL).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill)),
                new MultiColorComponent(),
                createButtonCloseContainer()));
        window.setComponent(contentPane);

        MenuBar menubar = new MenuBar();
        window.setMenuBar(menubar);

        // "File" menu
        Menu menuFile = new Menu("File");
        menubar.add(menuFile);
        menuFile.add(new MenuItem("Open...", s -> {
            File file = new FileDialogBuilder().build().show(textGUI);
            if (file != null)
                MessageDialog.showMessageDialog(
                    textGUI, "Open", "Selected file:\n" + file, MessageDialogButton.OK);
        }));
        menuFile.add(new MenuItem("Exit", i -> window.close()));

        Menu countryMenu = new Menu("Country");
        menubar.add(countryMenu);

        Menu germanySubMenu = new Menu("Germany");
        countryMenu.add(germanySubMenu);
        for (String state : GERMANY_STATES) {
            germanySubMenu.add(new MenuItem(state, DO_NOTHING));
        }
        Menu japanSubMenu = new Menu("Japan");
        countryMenu.add(japanSubMenu);
        for (String prefecture : JAPAN_PREFECTURES) {
            japanSubMenu.add(new MenuItem(prefecture, DO_NOTHING));
        }

        // "Help" menu
        Menu menuHelp = new Menu("Help");
        menubar.add(menuHelp);
        menuHelp.add(new MenuItem("Homepage", s -> MessageDialog.showMessageDialog(
            textGUI, "Homepage", "https://github.com/mabe02/lanterna", MessageDialogButton.OK)));
        menuHelp.add(new MenuItem("About", s -> MessageDialog.showMessageDialog(
            textGUI, "About", "Lanterna drop-down menu", MessageDialogButton.OK)));

        // Create textGUI and start textGUI
        textGUI.addWindow(window);
    }

    private static class MultiColorComponent extends AbstractComponent<MultiColorComponent> {

        public MultiColorComponent() {
            super(Attributes.EMPTY);
        }

        @Override
        protected ComponentRenderer<MultiColorComponent> createDefaultRenderer() {
            return new ComponentRenderer<>() {
                @Override
                public void drawComponent(TextGUIGraphics graphics, MultiColorComponent component) {
                    graphics.applyThemeStyle(getTheme().getDefaultDefinition().getNormal());
                    graphics.fill(' ');
                    int row = 1;
                    for (TextColor color : TextColor.ANSI.values()) {
                        graphics.applyThemeStyle(getTheme().getDefaultDefinition().getNormal());
                        graphics.putString(1, row, color.toString() + ": ");
                        graphics.setForegroundColor(TextColor.ANSI.BLACK);
                        graphics.setBackgroundColor(color);
                        graphics.putString(20, row++, "     TEXT     ");
                    }
                }

                @Override
                public Dimension getPreferredSize(MultiColorComponent component) {
                    return new Dimension(40, 15);
                }
            };
        }
    }
}

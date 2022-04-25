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
 * Copyright (C) 2017 Bruno Eberhard
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */
package com.googlecode.lanterna.gui2.menu;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of a drop-down menu contained in a {@link MenuBar} and also a sub-menu inside another {@link Menu}.
 */
public class Menu extends MenuItem {
    private final List<MenuItem> subItems = new ArrayList<>();

    /**
     * Creates a menu with the specified label
     *
     * @param label Label to use for the menu item that will trigger this menu to pop up
     */
    public Menu(String label) {
        this(label, Attributes.EMPTY);
    }

    public Menu(String label, Attributes attributes) {
        super(label, attributes);
    }


    /**
     * Adds a new menu item to this menu, this can be either a regular {@link MenuItem} or another {@link Menu}
     *
     * @param menuItem The item to add to this menu
     * @return Itself
     */
    public Menu add(MenuItem menuItem) {
        synchronized (subItems) {
            subItems.add(menuItem);
        }
        return this;
    }

    @Override
    public boolean onClicked() {
        boolean result = true;
        if (subItems.isEmpty()) {
            return result;
        }
        final MenuPopupWindow popupMenu = new MenuPopupWindow(this);
        final AtomicBoolean popupCancelled = new AtomicBoolean(false);
        for (MenuItem menuItem : subItems) {
            popupMenu.addMenuItem(menuItem);
        }
        if (getParent() instanceof MenuBar) {
            final MenuBar menuBar = (MenuBar) getParent();
            popupMenu.addRootPaneKeystrokeInterceptor(new RootPaneKeystrokeInterceptor() {
                @Override
                public boolean onAfterKeyStroke(KeyStroke keyStroke, boolean handled, RootPane rootPane) {
                    if (keyStroke.getKeyType() == KeyType.ArrowLeft) {
                        int thisMenuIndex = menuBar.getChildrenList().indexOf(Menu.this);
                        if (thisMenuIndex > 0) {
                            popupMenu.close();
                            Menu nextSelectedMenu = menuBar.getMenu(thisMenuIndex - 1);
                            nextSelectedMenu.grabFocus();
                            nextSelectedMenu.onClicked();
                            return true;
                        }
                    } else if (keyStroke.getKeyType() == KeyType.ArrowRight) {
                        int thisMenuIndex = menuBar.getChildrenList().indexOf(Menu.this);
                        if (thisMenuIndex >= 0 && thisMenuIndex < menuBar.getMenuCount() - 1) {
                            popupMenu.close();
                            Menu nextSelectedMenu = menuBar.getMenu(thisMenuIndex + 1);
                            nextSelectedMenu.grabFocus();
                            nextSelectedMenu.onClicked();
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
        popupMenu.addRootPaneKeystrokeInterceptor(new RootPaneKeystrokeInterceptor() {
            @Override
            public boolean onAfterKeyStroke(KeyStroke keyStroke, boolean handled, RootPane rootPane) {
                if (keyStroke.getKeyType() == KeyType.Escape) {
                    popupCancelled.set(true);
                    popupMenu.close();
                    return true;
                }
                return false;
            }
        });
        ((WindowFrame) getTextGUI()).addWindowAndWait(popupMenu);
        result = !popupCancelled.get();

        return result;
    }
}

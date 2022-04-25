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

import com.googlecode.lanterna.Dimension;
import com.googlecode.lanterna.Point;
import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of a drop-down menu contained in a {@link MenuBar} and also a sub-menu inside another {@link Menu}.
 */
public class Menu extends AbstractInteractableComponent<Menu> implements MenuSubElement {

    private final String label;
    private final List<MenuSubElement> menuSubElements = new ArrayList<>();

    /**
     * Creates a menu with the specified label
     *
     * @param label Label to use for the menu item that will trigger this menu to pop up
     */
    public Menu(String label) {
        this(label, Attributes.EMPTY);
    }

    public Menu(String label, Attributes attributes) {
        super(attributes);
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("Menu label is not allowed to be null or empty");
        }
        this.label = label.trim();
    }


    /**
     * Adds a new menu item to this menu, this can be either a regular {@link MenuItem} or another {@link Menu}
     *
     * @param menuSubElement The item to add to this menu
     * @return Itself
     */
    public Menu add(MenuSubElement menuSubElement) {
        synchronized (menuSubElements) {
            menuSubElements.add(menuSubElement);
        }
        return this;
    }

    @Override
    protected InteractableRenderer<Menu> createDefaultRenderer() {
        return new Menu.DefaultMenuRenderer();
    }

    /**
     * Returns the label of this menu item
     *
     * @return Label of this menu item
     */
    public String getLabel() {
        return label;
    }

    public boolean onClicked() {
        boolean result = true;
        if (menuSubElements.isEmpty()) {
            return result;
        }
        final MenuPopupWindow popupMenu = new MenuPopupWindow(this);
        final AtomicBoolean popupCancelled = new AtomicBoolean(false);
        for (MenuSubElement menuSubElement : menuSubElements) {
            popupMenu.addMenuItem(menuSubElement);
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

    @Override
    public KeyStrokeResult onKeyStroke(KeyStroke keyStroke) {
        if (isActivationStroke(keyStroke)) {
            if (onClicked()) {
                RootPane rootPane = getRootPane();
                if (rootPane instanceof Window && ((Window) rootPane).isHint(Window.Hint.MENU_POPUP)) {
                    ((Window) rootPane).close();
                }
            }
            return KeyStrokeResult.HANDLED;
        } else if (isMouseMove(keyStroke)) {
            grabFocus();
            return KeyStrokeResult.HANDLED;
        } else {
            return super.onKeyStroke(keyStroke);
        }
    }

    /**
     * Helper interface that doesn't add any new methods but makes coding new menu renderers a little bit more clear
     */
    public static abstract class MenuRenderer implements InteractableRenderer<Menu> {
    }

    /**
     * Default renderer for menu items (both sub-menus and regular items)
     */
    public static class DefaultMenuRenderer extends MenuRenderer {
        @Override
        public void drawComponent(TextUiGraphics graphics, Menu menu) {
            ThemeDefinition themeDefinition = menu.getThemeDefinition();
            if (menu.isFocused()) {
                graphics.applyThemeStyle(themeDefinition.getSelected());
            } else {
                graphics.applyThemeStyle(themeDefinition.getNormal());
            }

            final String label = menu.getLabel();
            final String leadingCharacter = label.substring(0, 1);

            graphics.fill(' ');
            graphics.putString(1, 0, label);
            if (menu instanceof Menu && !(menu.getParent() instanceof MenuBar)) {
                graphics.putString(graphics.getSize().getColumns() - 2, 0, String.valueOf(Symbols.TRIANGLE_RIGHT_POINTING_BLACK));
            }
            if (!label.isEmpty()) {
                if (menu.isFocused()) {
                    graphics.applyThemeStyle(themeDefinition.getActive());
                } else {
                    graphics.applyThemeStyle(themeDefinition.getPreLight());
                }
                graphics.putString(1, 0, leadingCharacter);
            }
        }

        @Override
        public Point getCursorLocation(Menu component) {
            return null;
        }

        @Override
        public Dimension getPreferredSize(Menu component) {
            int preferredWidth = TerminalTextUtils.getColumnWidth(component.getLabel()) + 2;
            if (component instanceof Menu && !(component.getParent() instanceof MenuBar)) {
                preferredWidth += 2;
            }
            return Dimension.ONE.withColumns(preferredWidth);
        }
    }

}

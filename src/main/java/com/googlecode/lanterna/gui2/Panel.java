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
import com.googlecode.lanterna.input.KeyStroke;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

/**
 * This class is the basic building block for creating user interfaces, being the standard implementation of
 * {@code Container} that supports multiple children. A {@code Panel} is a component that can contain one or more
 * other components, including nested panels. The panel itself doesn't have any particular appearance and isn't
 * interactable by itself, although you can set a border for the panel and interactable components inside the panel will
 * receive input focus as expected.
 *
 * @author Martin
 */
public class Panel extends AbstractComponent<Panel> implements Container {
    private final List<Component> components = new CopyOnWriteArrayList<>();

    private LayoutManager layoutManager;
    private Dimension cachedPreferredSize;
    private TextColor fillColorOverride;

    /**
     * Default constructor, creates a new panel with no child components and by default set to a vertical
     * {@code LinearLayout} layout manager.
     */
    public Panel() {
        this(new LinearLayout(), Attributes.EMPTY);
    }

    public Panel(Attributes attributes) {
        this(new LinearLayout(), attributes);
    }

    public Panel(LayoutManager layoutManager) {
        this(layoutManager, Attributes.EMPTY);
    }

    public Panel(LayoutManager layoutManager, Attributes attributes) {
        super(attributes);
        this.layoutManager = Optional.ofNullable(layoutManager).orElseGet(AbsoluteLayout::new);
    }

    /**
     * Adds a new child component to the panel. Where within the panel the child will be displayed is up to the layout
     * manager assigned to this panel. If the component has already been added to another panel, it will first be
     * removed from that panel before added to this one.
     *
     * @param components Child component to add to this panel
     * @return Itself
     */
    public Panel add(Component... components) {
        if (components == null) {
            throw new IllegalArgumentException("Cannot add null component");
        }
        Stream.of(components).forEach(c -> add(c, Integer.MAX_VALUE));
        return this;
    }


    /**
     * Adds a new child component to the panel. Where within the panel the child will be displayed is up to the layout
     * manager assigned to this panel. If the component has already been added to another panel, it will first be
     * removed from that panel before added to this one.
     *
     * @param component Child component to add to this panel
     * @param index     At what index to add the component among the existing components
     * @return Itself
     */
    public Panel add(Component component, int index) {
        if (component == null) {
            throw new IllegalArgumentException("Cannot add null component");
        }
        synchronized (components) {
            if (components.contains(component)) {
                return this;
            }
            if (component.getParent() != null) {
                component.getParent().remove(component);
            }
            if (index > components.size()) {
                index = components.size();
            } else if (index < 0) {
                index = 0;
            }
            components.add(index, component);
        }
        component.onAdded(this);
        invalidate();
        return this;
    }

    /**
     * This method is a shortcut for calling:
     * <pre>
     *     {@code
     *     component.setLayoutData(layoutData);
     *     panel.addComponent(component);
     *     }
     * </pre>
     *
     * @param component  Component to add to the panel
     * @param layoutData Layout data to assign to the component
     * @return Itself
     */
    public Panel add(Component component, LayoutData layoutData) {
        return add(component.setLayoutData(layoutData));
    }

    @Override
    public Dimension calculatePreferredSize() {
        if (cachedPreferredSize != null && !isInvalid()) {
            return cachedPreferredSize;
        }
        return super.calculatePreferredSize();
    }

    @Override
    public boolean contains(Component component) {
        return component != null && component.hasParent(this);
    }

    @Override
    protected ComponentRenderer<Panel> createDefaultRenderer() {
        return new DefaultPanelRenderer();
    }

    @Override
    public Collection<Component> getChildren() {
        return getChildrenList();
    }

    @Override
    public List<Component> getChildrenList() {
        synchronized (components) {
            return new ArrayList<>(components);
        }
    }

    @Override
    public Component getComponent(int index) {
        return components.get(index);
    }

    @Override
    public int getComponentCount() {
        return components.size();
    }

    /**
     * Returns the color used to override the default background color from the theme, if set. Otherwise {@code null} is
     * returned and whatever theme is assigned will be used to derive the fill color.
     *
     * @return The color, if any, used to fill the panel's unused space instead of the theme's color
     */
    public TextColor getFillColorOverride() {
        return fillColorOverride;
    }

    /**
     * Sets an override color to be used instead of the theme's color for Panels when drawing unused space. If called
     * with {@code null}, it will reset back to the theme's color.
     *
     * @param fillColor Color to draw the unused space with instead of what the theme definition says, no {@code null}
     *                  to go back to the theme definition
     */
    public void setFillColorOverride(TextColor fillColor) {
        this.fillColorOverride = fillColor;
    }

    /**
     * Returns the layout manager assigned to this panel
     *
     * @return Layout manager assigned to this panel
     */
    public LayoutManager getLayoutManager() {
        return layoutManager;
    }

    /**
     * Assigns a new layout manager to this panel, replacing the previous layout manager assigned. Please note that if
     * the panel is not empty at the time you assign a new layout manager, the existing components might not show up
     * where you expect them and their layout data property might need to be re-assigned.
     *
     * @param layoutManager New layout manager this panel should be using
     * @return Itself
     */
    public synchronized Panel setLayoutManager(LayoutManager layoutManager) {
        if (layoutManager == null) {
            layoutManager = new AbsoluteLayout();
        }
        this.layoutManager = layoutManager;
        invalidate();
        return this;
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        return false;
    }

    @Override
    public void invalidate() {
        super.invalidate();

        components.forEach(Component::invalidate);
    }

    @Override
    public boolean isInvalid() {
        return components.stream().anyMatch(c -> c.isVisible() && c.isInvalid()) || super.isInvalid() || layoutManager.hasChanged();
    }

    private void layout(Dimension size) {
        layoutManager.doLayout(size, components);
    }

    @Override
    public Interactable nextFocus(Interactable fromThis) {
        boolean chooseNextAvailable = (fromThis == null);

        for (Component component : components) {
            if (!component.isVisible()) {
                continue;
            }
            if (chooseNextAvailable) {
                if (component instanceof Interactable && ((Interactable) component).isEnabled() && ((Interactable) component).isFocusable()) {
                    return (Interactable) component;
                } else if (component instanceof Container) {
                    Interactable firstInteractable = ((Container) (component)).nextFocus(null);
                    if (firstInteractable != null) {
                        return firstInteractable;
                    }
                }
                continue;
            }

            if (component == fromThis) {
                chooseNextAvailable = true;
                continue;
            }

            if (component instanceof Container) {
                Container container = (Container) component;
                if (fromThis.isInside(container)) {
                    Interactable next = container.nextFocus(fromThis);
                    if (next == null) {
                        chooseNextAvailable = true;
                    } else {
                        return next;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Interactable previousFocus(Interactable fromThis) {
        boolean chooseNextAvailable = (fromThis == null);

        List<Component> components = this.components;
        for (int i = components.size() - 1; i >= 0; i--) {
            Component component = components.get(i);
            if (!component.isVisible()) {
                continue;
            }
            if (chooseNextAvailable) {
                if (component instanceof Interactable && ((Interactable) component).isEnabled() && ((Interactable) component).isFocusable()) {
                    return (Interactable) component;
                }
                if (component instanceof Container) {
                    Interactable lastInteractable = ((Container) (component)).previousFocus(null);
                    if (lastInteractable != null) {
                        return lastInteractable;
                    }
                }
                continue;
            }

            if (component == fromThis) {
                chooseNextAvailable = true;
                continue;
            }

            if (component instanceof Container) {
                Container container = (Container) component;
                if (fromThis.isInside(container)) {
                    Interactable next = container.previousFocus(fromThis);
                    if (next == null) {
                        chooseNextAvailable = true;
                    } else {
                        return next;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean remove(Component component) {
        if (component == null) {
            throw new IllegalArgumentException("Cannot remove null component");
        }
        synchronized (components) {
            int index = components.indexOf(component);
            if (index == -1) {
                return false;
            }
            if (getRootPane() != null && getRootPane().getFocusedInteractable() == component) {
                getRootPane().setFocusedInteractable(null);
            }
            components.remove(index);
        }
        component.onRemoved(this);
        invalidate();
        return true;
    }

    /**
     * Removes all child components from this panel
     *
     * @return Itself
     */
    public Panel removeAllComponents() {
        synchronized (components) {
            for (Component component : new ArrayList<>(components)) {
                remove(component);
            }
        }
        return this;
    }

    @Override
    public void updateLookupMap(InteractableLookupMap interactableLookupMap) {
        synchronized (components) {
            for (Component component : components) {
                if (!component.isVisible()) {
                    continue;
                }
                if (component instanceof Container) {
                    ((Container) component).updateLookupMap(interactableLookupMap);
                } else if (component instanceof Interactable && ((Interactable) component).isEnabled() && ((Interactable) component).isFocusable()) {
                    interactableLookupMap.add((Interactable) component);
                }
            }
        }
    }

    public class DefaultPanelRenderer implements ComponentRenderer<Panel> {
        private boolean fillAreaBeforeDrawingComponents = true;

        @Override
        public void drawComponent(TextGUIGraphics graphics, Panel panel) {
            if (isInvalid()) {
                layout(graphics.getSize());
            }

            if (fillAreaBeforeDrawingComponents) {
                // Reset the area
                graphics.applyThemeStyle(getThemeDefinition().getNormal());
                if (fillColorOverride != null) {
                    graphics.setBackgroundColor(fillColorOverride);
                }
                graphics.fill(' ');
            }

            synchronized (components) {
                for (Component child : components) {
                    if (!child.isVisible()) {
                        continue;
                    }
                    TextGUIGraphics componentGraphics = graphics.newTextGraphics(child.getPosition(), child.getSize());
                    child.draw(componentGraphics);
                }
            }
        }

        @Override
        public Dimension getPreferredSize(Panel component) {
            synchronized (components) {
                cachedPreferredSize = layoutManager.getPreferredSize(components);
            }
            return cachedPreferredSize;
        }

        /**
         * If setting this to {@code false} (default is {@code true}), the {@link Panel} will not reset it's drawable
         * area with the space character ' ' before drawing all the components. Usually you <b>do</b> want to reset this
         * area before drawing but you might have a custom renderer that has prepared the area already and just want the
         * panel renderer to layout and draw the components in the panel without touching the existing content. One such
         * example is the {@code FullScreenTextGUITest}.
         *
         * @param fillAreaBeforeDrawingComponents Should the panels area be cleared before drawing components?
         */
        public void setFillAreaBeforeDrawingComponents(boolean fillAreaBeforeDrawingComponents) {
            this.fillAreaBeforeDrawingComponents = fillAreaBeforeDrawingComponents;
        }
    }
}

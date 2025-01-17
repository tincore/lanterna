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

import com.googlecode.lanterna.Point;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This abstract implementation contains common code for the different {@code Composite} implementations. A
 * {@code Composite} component is one that encapsulates a single component, like borders. Because of this, a
 * {@code Composite} can be seen as a special case of a {@code Container} and indeed this abstract class does in fact
 * implement the {@code Container} interface as well, to make the composites easier to work with internally.
 *
 * @param <T> Should always be itself, see {@code AbstractComponent}
 * @author martin
 */
public abstract class AbstractComposite<T extends Container> extends AbstractComponent<T> implements Composite, Container {

    private Component component;

    public AbstractComposite(Attributes attributes) {
        super(attributes);
    }

    @Override
    public boolean contains(Component component) {
        return component != null && component.hasParent(this);
    }

    @Override
    public Collection<Component> getChildren() {
        return getChildrenList();
    }

    @Override
    public List<Component> getChildrenList() {
        return component != null ? Collections.singletonList(component) : Collections.emptyList();
    }

    @Override
    public Component getComponent(int index) {
        if (index > 0 || component == null) {
            throw new IndexOutOfBoundsException("Component index out of range");
        }
        return component;
    }

    @Override
    public Component getComponent() {
        return component;
    }

//    @Override
//    public T setComponent(Component component) {
//        Component oldComponent = this.component;
//        if (oldComponent == component) {
//            return null;
//        }
//        if (oldComponent != null) {
//            remove(oldComponent);
//        }
//        if (component != null) {
//            this.component = component;
//            component.onAdded(this);
//            if (getBasePane() != null) {
//                MenuBar menuBar = getBasePane().getMenuBar();
//                if (menuBar == null || menuBar.isEmptyMenuBar()) {
//                    component.setPosition(TerminalPosition.TOP_LEFT_CORNER);
//                } else {
//                    component.setPosition(TerminalPosition.TOP_LEFT_CORNER.withRelativeRow(1));
//                }
//            }
//            invalidate();
//        }
//        return self();
//    }


    @Override
    public Composite setComponent(Component component) {
        Component oldComponent = this.component;
        if (oldComponent == component) {
            return this;
        }
        if (oldComponent != null) {
            remove(oldComponent);
        }
        if (component != null) {
            this.component = component;
            component.onAdded(this);
            if (getRootPane() != null) {
                MenuBar menuBar = getRootPane().getMenuBar();
                if (menuBar == null || menuBar.isEmptyMenuBar()) {
                    component.setPosition(Point.TOP_LEFT_CORNER);
                } else {
                    component.setPosition(Point.TOP_LEFT_CORNER.withRelativeRow(1));
                }
            }
            invalidate();
        }
        return this;
    }

    @Override
    public int getComponentCount() {
        return component != null ? 1 : 0;
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        return false;
    }

    @Override
    public void invalidate() {
        super.invalidate();

        //Propagate
        if (component != null) {
            component.invalidate();
        }
    }

    @Override
    public boolean isInvalid() {
        return component != null && component.isInvalid();
    }

    @Override
    public Interactable nextFocus(Interactable fromThis) {
        if (fromThis == null && getComponent() instanceof Interactable) {
            Interactable interactable = (Interactable) getComponent();
            if (interactable.isEnabled()) {
                return interactable;
            }
        } else if (getComponent() instanceof Container) {
            return ((Container) getComponent()).nextFocus(fromThis);
        }
        return null;
    }

    @Override
    public Interactable previousFocus(Interactable fromThis) {
        if (fromThis == null && getComponent() instanceof Interactable) {
            Interactable interactable = (Interactable) getComponent();
            if (interactable.isEnabled()) {
                return interactable;
            }
        } else if (getComponent() instanceof Container) {
            return ((Container) getComponent()).previousFocus(fromThis);
        }
        return null;
    }

    @Override
    public boolean remove(Component component) {
        if (this.component == component) {
            this.component = null;
            component.onRemoved(this);
            invalidate();
            return true;
        }
        return false;
    }

    @Override
    public void updateLookupMap(InteractableLookupMap interactableLookupMap) {
        if (getComponent() instanceof Container) {
            ((Container) getComponent()).updateLookupMap(interactableLookupMap);
        } else if (getComponent() instanceof Interactable) {
            interactableLookupMap.add((Interactable) getComponent());
        }
    }
}

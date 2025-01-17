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
import com.googlecode.lanterna.Point;

/**
 * Abstract implementation of {@code Border} interface that has some of the methods filled out. If you want to create
 * your own {@code Border} implementation, should should probably extend from this.
 *
 * @author Martin
 */
public abstract class AbstractBorder extends AbstractComposite<Border> implements Border {

    /**
     * Default constructor
     *
     * @param attributes
     */
    public AbstractBorder(Attributes attributes) {
        super(attributes);
    }

    @Override
    public LayoutData getLayoutData() {
        return getComponent().getLayoutData();
    }

    @Override
    public BorderRenderer getRenderer() {
        return (BorderRenderer) super.getRenderer();
    }

    private Dimension getWrappedComponentSize(Dimension borderSize) {
        return getRenderer().getWrappedComponentSize(borderSize);
    }

    private Point getWrappedComponentTopLeftOffset() {
        return getRenderer().getWrappedComponentTopLeftOffset();
    }

    @Override
    public Border setComponent(Component component) {
        super.setComponent(component);
        if (component != null) {
            component.setPosition(Point.TOP_LEFT_CORNER);
        }
        return this;
    }

    @Override
    public Border setLayoutData(LayoutData ld) {
        getComponent().setLayoutData(ld);
        return this;
    }

    @Override
    public Border setSize(Dimension size) {
        super.setSize(size);
        getComponent().setSize(getWrappedComponentSize(size));
        return self();
    }

    @Override
    public Point toBasePane(Point point) {
        return super.toBasePane(point).withRelative(getWrappedComponentTopLeftOffset());
    }

    @Override
    public Point toGlobal(Point point) {
        return super.toGlobal(point).withRelative(getWrappedComponentTopLeftOffset());
    }
}

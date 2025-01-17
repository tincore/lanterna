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

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.graphics.ThemedTextGraphics;

/**
 * This WindowPostRenderer implementation draws a shadow under the window
 *
 * @author Martin
 */
public class WindowShadowRenderer implements WindowPostRenderer {
    @Override
    public void postRender(
            ThemedTextGraphics textGraphics,
            Frame frame,
            Window window) {

        Point windowPoint = window.getPosition();
        Dimension decoratedWindowSize = window.getDecoratedSize();
        ThemeDefinition themeDefinition = window.getTheme().getDefinition(WindowShadowRenderer.class);
        textGraphics.applyThemeStyle(themeDefinition.getNormal());
        char filler = themeDefinition.getCharacter("FILLER", ' ');
        boolean useDoubleWidth = themeDefinition.getBooleanProperty("DOUBLE_WIDTH", true);
        boolean useTransparency = themeDefinition.getBooleanProperty("TRANSPARENT", false);

        Point lowerLeft = windowPoint.withRelativeColumn(useDoubleWidth ? 2 : 1).withRelativeRow(decoratedWindowSize.getRows());
        Point lowerRight = lowerLeft.withRelativeColumn(decoratedWindowSize.getColumns() - (useDoubleWidth ? 3 : 2));
        for(int column = lowerLeft.getColumn(); column <= lowerRight.getColumn() + 1; column++) {
            char characterToDraw = filler;
            if(useTransparency) {
                TextCharacter tc = textGraphics.getCharacter(column, lowerLeft.getRow());
                if (tc != null) {
                    characterToDraw = tc.getCharacterString().charAt(0);
                }
            }
            textGraphics.setCharacter(column, lowerLeft.getRow(), characterToDraw);
            if (TerminalTextUtils.isCharDoubleWidth(characterToDraw)) {
                column++;
            }
        }

        lowerRight = lowerRight.withRelativeColumn(1);
        Point upperRight = lowerRight.withRelativeRow(-decoratedWindowSize.getRows() + 1);
        boolean hasDoubleWidthShadow = false;
        for(int row = upperRight.getRow(); row < lowerRight.getRow(); row++) {
            char characterToDraw = filler;
            if(useTransparency) {
                TextCharacter tc = textGraphics.getCharacter(upperRight.getColumn(), row);
                if (tc != null) {
                    characterToDraw = tc.getCharacterString().charAt(0);
                }
            }
            textGraphics.setCharacter(upperRight.getColumn(), row, characterToDraw);
            if (TerminalTextUtils.isCharDoubleWidth(characterToDraw)) {
                hasDoubleWidthShadow = true;
            }
        }

        textGraphics.applyThemeStyle(themeDefinition.getNormal());
        if(useDoubleWidth || hasDoubleWidthShadow) {
            //Fill the remaining hole
            upperRight = upperRight.withRelativeColumn(1);
            for(int row = upperRight.getRow(); row <= lowerRight.getRow(); row++) {
                char characterToDraw = filler;
                if(useTransparency) {
                    TextCharacter tc = textGraphics.getCharacter(upperRight.getColumn(), row);
                    if (tc != null && !tc.isDoubleWidth()) {
                        characterToDraw = tc.getCharacterString().charAt(0);
                    }
                }
                TextCharacter neighbour = textGraphics.getCharacter(upperRight.getColumn() - 1, row);
                // Only need to draw this is the character to the left isn't double-width
                if (neighbour != null && !neighbour.isDoubleWidth()) {
                    textGraphics.setCharacter(upperRight.getColumn(), row, characterToDraw);
                }
            }
        }
    }
}

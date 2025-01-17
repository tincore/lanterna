package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.*;
import com.googlecode.lanterna.screen.TabBehaviour;

import java.util.Collection;
import java.util.EnumSet;

/**
 * Created by Martin on 2017-08-11.
 */
public class DefaultTextUiGraphics implements TextUiGraphics {
    private final Frame frame;
    private final TextGraphics backend;

    DefaultTextUiGraphics(Frame frame, TextGraphics backend) {
        this.backend = backend;
        this.frame = frame;
    }

    @Override
    public Frame getTextGUI() {
        return frame;
    }

    @Override
    public DefaultTextUiGraphics newTextGraphics(Point topLeftCorner, Dimension size) throws IllegalArgumentException {
        return new DefaultTextUiGraphics(frame, backend.newTextGraphics(topLeftCorner, size));
    }

    @Override
    public DefaultTextUiGraphics applyThemeStyle(ThemeStyle themeStyle) {
        setForegroundColor(themeStyle.getForeground());
        setBackgroundColor(themeStyle.getBackground());
        setModifiers(themeStyle.getSGRs());
        return this;
    }

    @Override
    public Dimension getSize() {
        return backend.getSize();
    }

    @Override
    public TextColor getBackgroundColor() {
        return backend.getBackgroundColor();
    }

    @Override
    public DefaultTextUiGraphics setBackgroundColor(TextColor backgroundColor) {
        backend.setBackgroundColor(backgroundColor);
        return this;
    }

    @Override
    public TextColor getForegroundColor() {
        return backend.getForegroundColor();
    }

    @Override
    public DefaultTextUiGraphics setForegroundColor(TextColor foregroundColor) {
        backend.setForegroundColor(foregroundColor);
        return this;
    }

    @Override
    public DefaultTextUiGraphics enableModifiers(SGR... modifiers) {
        backend.enableModifiers(modifiers);
        return this;
    }

    @Override
    public DefaultTextUiGraphics disableModifiers(SGR... modifiers) {
        backend.disableModifiers(modifiers);
        return this;
    }

    @Override
    public DefaultTextUiGraphics setModifiers(EnumSet<SGR> modifiers) {
        backend.setModifiers(modifiers);
        return this;
    }

    @Override
    public DefaultTextUiGraphics clearModifiers() {
        backend.clearModifiers();
        return this;
    }

    @Override
    public EnumSet<SGR> getActiveModifiers() {
        return backend.getActiveModifiers();
    }

    @Override
    public TabBehaviour getTabBehaviour() {
        return backend.getTabBehaviour();
    }

    @Override
    public DefaultTextUiGraphics setTabBehaviour(TabBehaviour tabBehaviour) {
        backend.setTabBehaviour(tabBehaviour);
        return this;
    }

    @Override
    public DefaultTextUiGraphics fill(char c) {
        backend.fill(c);
        return this;
    }

    @Override
    public DefaultTextUiGraphics fillRectangle(Point topLeft, Dimension size, char character) {
        backend.fillRectangle(topLeft, size, character);
        return this;
    }

    @Override
    public DefaultTextUiGraphics fillRectangle(Point topLeft, Dimension size, TextCharacter character) {
        backend.fillRectangle(topLeft, size, character);
        return this;
    }

    @Override
    public DefaultTextUiGraphics drawRectangle(Point topLeft, Dimension size, char character) {
        backend.drawRectangle(topLeft, size, character);
        return this;
    }

    @Override
    public DefaultTextUiGraphics drawRectangle(Point topLeft, Dimension size, TextCharacter character) {
        backend.drawRectangle(topLeft, size, character);
        return this;
    }

    @Override
    public DefaultTextUiGraphics fillTriangle(Point p1, Point p2, Point p3, char character) {
        backend.fillTriangle(p1, p2, p3, character);
        return this;
    }

    @Override
    public DefaultTextUiGraphics fillTriangle(Point p1, Point p2, Point p3, TextCharacter character) {
        backend.fillTriangle(p1, p2, p3, character);
        return this;
    }

    @Override
    public DefaultTextUiGraphics drawTriangle(Point p1, Point p2, Point p3, char character) {
        backend.drawTriangle(p1, p2, p3, character);
        return this;
    }

    @Override
    public DefaultTextUiGraphics drawTriangle(Point p1, Point p2, Point p3, TextCharacter character) {
        backend.drawTriangle(p1, p2, p3, character);
        return this;
    }

    @Override
    public DefaultTextUiGraphics drawLine(Point fromPoint, Point toPoint, char character) {
        backend.drawLine(fromPoint, toPoint, character);
        return this;
    }

    @Override
    public DefaultTextUiGraphics drawLine(Point fromPoint, Point toPoint, TextCharacter character) {
        backend.drawLine(fromPoint, toPoint, character);
        return this;
    }

    @Override
    public DefaultTextUiGraphics drawLine(int fromX, int fromY, int toX, int toY, char character) {
        backend.drawLine(fromX, fromY, toX, toY, character);
        return this;
    }

    @Override
    public DefaultTextUiGraphics drawLine(int fromX, int fromY, int toX, int toY, TextCharacter character) {
        backend.drawLine(fromX, fromY, toX, toY, character);
        return this;
    }

    @Override
    public DefaultTextUiGraphics drawImage(Point topLeft, TextImage image) {
        backend.drawImage(topLeft, image);
        return this;
    }

    @Override
    public DefaultTextUiGraphics drawImage(Point topLeft, TextImage image, Point sourceImageTopLeft, Dimension sourceImageSize) {
        backend.drawImage(topLeft, image, sourceImageTopLeft, sourceImageSize);
        return this;
    }

    @Override
    public DefaultTextUiGraphics setCharacter(Point point, char character) {
        backend.setCharacter(point, character);
        return this;
    }

    @Override
    public DefaultTextUiGraphics setCharacter(Point point, TextCharacter character) {
        backend.setCharacter(point, character);
        return this;
    }

    @Override
    public DefaultTextUiGraphics setCharacter(int column, int row, char character) {
        backend.setCharacter(column, row, character);
        return this;
    }

    @Override
    public DefaultTextUiGraphics setCharacter(int column, int row, TextCharacter character) {
        backend.setCharacter(column, row, character);
        return this;
    }

    @Override
    public DefaultTextUiGraphics putString(int column, int row, String string) {
        backend.putString(column, row, string);
        return this;
    }

    @Override
    public DefaultTextUiGraphics putString(Point point, String string) {
        backend.putString(point, string);
        return this;
    }

    @Override
    public DefaultTextUiGraphics putString(int column, int row, String string, SGR extraModifier, SGR... optionalExtraModifiers) {
        backend.putString(column, row, string, extraModifier, optionalExtraModifiers);
        return this;
    }

    @Override
    public DefaultTextUiGraphics putString(Point point, String string, SGR extraModifier, SGR... optionalExtraModifiers) {
        backend.putString(point, string, extraModifier, optionalExtraModifiers);
        return this;
    }

    @Override
    public DefaultTextUiGraphics putString(int column, int row, String string, Collection<SGR> extraModifiers) {
        backend.putString(column, row, string, extraModifiers);
        return this;
    }

    @Override
    public DefaultTextUiGraphics putCSIStyledString(int column, int row, String string) {
        backend.putCSIStyledString(column, row, string);
        return this;
    }

    @Override
    public DefaultTextUiGraphics putCSIStyledString(Point point, String string) {
        backend.putCSIStyledString(point, string);
        return this;
    }

    @Override
    public TextCharacter getCharacter(int column, int row) {
        return backend.getCharacter(column, row);
    }

    @Override
    public TextCharacter getCharacter(Point point) {
        return backend.getCharacter(point);
    }

    @Override
    public DefaultTextUiGraphics setStyleFrom(StyleSet<?> source) {
        setBackgroundColor(source.getBackgroundColor());
        setForegroundColor(source.getForegroundColor());
        setModifiers(source.getActiveModifiers());
        return this;
    }

}

package com.googlecode.lanterna.gui2;

public interface Attributed {

    String ID = "id";

    static Attributes attrs(String id) {
        return new Attributes(ID, id);
    }

    default String getAttribute(String attribute) {
        return getAttributes().get(attribute);
    }

    default String getAttribute(String attribute, String defaultValue) {
        return getAttributes().getOrDefault(attribute, defaultValue);
    }

    Attributes getAttributes();

    default String getId() {
        return getAttribute(ID);
    }
}

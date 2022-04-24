package com.googlecode.lanterna.gui2;

import java.util.Collections;
import java.util.Map;

public class Attributes {
    public static final Attributes EMPTY = new Attributes();
    final Map<String, Object> values;

    public Attributes() {
        this.values = Collections.emptyMap();
    }

    public Attributes(Map<String, Object> values) {
        this.values = values;
    }

    public Attributes(String k1, Object v1) {
        this.values = Map.of(k1, v1);
    }

    public Attributes(String k1, Object v1, String k2, Object v2) {
        this.values = Map.of(k1, v1, k2, v2);
    }

    public Attributes(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        this.values = Map.of(k1, v1, k2, v2, k3, v3);
    }

    public String get(String tagId) {
        return (String) values.get(tagId);
    }

    public int getOrDefault(String tagId, int defaultValue) {
        return (int) values.getOrDefault(tagId, defaultValue);
    }

    public String getOrDefault(String tagId, String defaultValue) {
        return (String) values.getOrDefault(tagId, defaultValue);
    }
}

package com.ceragon.util;


import java.util.HashMap;
import java.util.Map;

public class MavenBuildContext {
    private final Map<String, Object> contextMap = new HashMap<>();

    public Object getValue(String key) {
        return contextMap.get(key);
    }

    public void setValue(String key, Object value) {
        this.contextMap.put(key, value);
    }
}

package io.github.ceragon.util;

import java.util.HashMap;
import java.util.Map;

public class BuildContext {
    private final Map<String, Object> context = new HashMap<>();

    public void clear() {
        context.clear();
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(String key) {
        Object value = context.get(key);
        if (value == null) {
            return null;
        }
        return (T) value;
    }

    public void setValue(String key, Object value) {
        context.put(key, value);
    }
}

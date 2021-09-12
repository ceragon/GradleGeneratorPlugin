package com.ceragon.util;

import com.ceragon.PluginContext;

public class PluginTaskException extends Exception {
    public final static ThreadLocal<String> taskNameLocal = new ThreadLocal<>();

    public PluginTaskException() {
        super(fillMessage(null));
    }

    public PluginTaskException(String message) {
        super(fillMessage(message));
    }

    public PluginTaskException(String message, Throwable cause) {
        super(fillMessage(message), cause);
    }

    public PluginTaskException(Throwable cause) {
        super(fillMessage(null), cause);
    }

    public PluginTaskException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(fillMessage(message), cause, enableSuppression, writableStackTrace);
    }

    private static String fillMessage(String message) {
        StringBuilder builder = PluginContext.stringBuilder()
                .append("taskName:").append(taskNameLocal.get());
        if (message != null) {
            builder.append(",").append("message:").append(message);
        }
        return builder.toString();
    }
}

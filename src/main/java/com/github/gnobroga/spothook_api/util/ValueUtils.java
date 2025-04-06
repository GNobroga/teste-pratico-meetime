package com.github.gnobroga.spothook_api.util;

public final class ValueUtils {
    
    public static <T> T defaultIfNull(T value, T fallback) {
        return value != null ? value : fallback;
    }
}

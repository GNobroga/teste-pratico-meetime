package com.github.gnobroga.spothook_api.util;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class UrlUtils {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static String encode(String value) {
        if (value == null) return "";
        return URLEncoder.encode(value, DEFAULT_CHARSET);
    }
    
}

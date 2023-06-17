package com.timm.ecommerce.analyzer.provider.util;

import java.util.Optional;

import okhttp3.HttpUrl;

public final class UrlUtils {

    private UrlUtils() {}

    public static Optional<String> getHostFromUrl(String urlString) {
        return Optional.ofNullable(HttpUrl.parse(urlString))
                       .map(HttpUrl::host);
    }

    public static Optional<String> getParameterAsString(String urlString, String parameterName) {
        return Optional.ofNullable(HttpUrl.parse(urlString))
                       .map(httpUrl -> httpUrl.queryParameter(parameterName));
    }

    public static Optional<String> getPathParameterAsString(String urlString, int segment) {
        return Optional.ofNullable(HttpUrl.parse(urlString))
                       .filter(httpUrl -> httpUrl.pathSegments().size() > segment)
                       .map(httpUrl -> httpUrl.pathSegments().get(segment));
    }
}

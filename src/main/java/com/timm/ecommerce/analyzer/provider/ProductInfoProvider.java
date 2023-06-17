package com.timm.ecommerce.analyzer.provider;

import static com.timm.ecommerce.analyzer.provider.util.UrlUtils.getHostFromUrl;

import java.util.Optional;
import java.util.Set;

public abstract class ProductInfoProvider {
    private final Set<String> supportedHosts;

    protected ProductInfoProvider(Set<String> supportedHosts) {
        this.supportedHosts = supportedHosts;
    }

    public Optional<ProductInfo> retrieve(String urlString) {
        return getHostFromUrl(urlString)
                .filter(this::isHostSupported)
                .flatMap(__ -> retrieveByUrlString(urlString));
    }

    protected abstract Optional<ProductInfo> retrieveByUrlString(String urlString);

    protected boolean isHostSupported(String domain) {
        return supportedHosts.contains(domain);
    }
}

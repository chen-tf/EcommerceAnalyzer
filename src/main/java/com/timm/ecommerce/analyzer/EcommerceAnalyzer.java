package com.timm.ecommerce.analyzer;

import java.util.List;
import java.util.Optional;

import com.timm.ecommerce.analyzer.provider.ProductInfo;
import com.timm.ecommerce.analyzer.provider.ProductInfoProvider;
import com.timm.ecommerce.analyzer.provider.momo.Momo;
import com.timm.ecommerce.analyzer.provider.momo.MomoDMAPIClient;
import com.timm.ecommerce.analyzer.provider.momo.MomoMobileAPIClient;

import okhttp3.OkHttpClient;

public class EcommerceAnalyzer {

    private OkHttpClient httpClient;
    private List<ProductInfoProvider> productInfoProviders;

    public EcommerceAnalyzer() {
        httpClient = new OkHttpClient();
        productInfoProviders = List.of(
                new Momo(new MomoDMAPIClient(httpClient),
                         new MomoMobileAPIClient(httpClient))
        );
    }

    public Optional<ProductInfo> analyze(String urlString) {
        return productInfoProviders
                .stream()
                .map(productInfoProvider -> productInfoProvider.retrieve(urlString))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }
}

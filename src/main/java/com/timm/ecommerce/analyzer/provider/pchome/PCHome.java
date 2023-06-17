package com.timm.ecommerce.analyzer.provider.pchome;

import static com.timm.ecommerce.analyzer.provider.pchome.Constant.PC_24_HOST;
import static com.timm.ecommerce.analyzer.provider.pchome.Constant.PC_API_HOST;
import static com.timm.ecommerce.analyzer.provider.util.UrlUtils.getHTTPSBaseUrl;
import static com.timm.ecommerce.analyzer.provider.util.UrlUtils.getPathParameterAsString;

import java.util.Optional;
import java.util.Set;

import com.timm.ecommerce.analyzer.provider.ProductInfo;
import com.timm.ecommerce.analyzer.provider.ProductInfoProvider;

public class PCHome extends ProductInfoProvider {

    private final PCAPIClient pcapiClient;

    public PCHome(PCAPIClient pcapiClient) {
        super(Set.of(PC_24_HOST, PC_API_HOST));
        this.pcapiClient = pcapiClient;
    }

    @Override
    protected Optional<ProductInfo> retrieveByUrlString(String urlString) {
        if (isPCProductUrl(urlString)) {
            return Optional.empty();
        }
        return getPathParameterAsString(urlString, 1)
                .flatMap(pcapiClient::getGoodInfo)
                .map(PCProductInfo::toProductInfo);
    }

    private static boolean isPCProductUrl(String urlString) {
        return urlString.startsWith(getHTTPSBaseUrl(PC_24_HOST) + "/prod/");
    }
}

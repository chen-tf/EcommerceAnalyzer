package com.timm.ecommerce.analyzer.provider.pchome;

import java.math.BigDecimal;

import com.timm.ecommerce.analyzer.provider.ProductInfo;
import com.timm.ecommerce.analyzer.provider.ProductSource;
import com.timm.ecommerce.analyzer.provider.ProductStatus;

public record PCProductInfo(
        String id,
        String name,
        BigDecimal price,
        Integer quality
) {
    public ProductInfo toProductInfo() {
        return new ProductInfo(
                id,
                name,
                price,
                quality > 0
                ? ProductStatus.IN_STOCK
                : ProductStatus.OUT_OF_STOCK,
                ProductSource.PCHOME
        );
    }
}

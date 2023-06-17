package com.timm.ecommerce.analyzer.provider;

import java.math.BigDecimal;

public record ProductInfo(
        String id,
        String name,
        BigDecimal price,
        ProductStatus status,
        ProductSource source
) {
}

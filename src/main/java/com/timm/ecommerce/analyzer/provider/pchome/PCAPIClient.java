package com.timm.ecommerce.analyzer.provider.pchome;

import static com.timm.ecommerce.analyzer.provider.pchome.Constant.PC_API_HOST;
import static com.timm.ecommerce.analyzer.provider.util.RetrofitUtils.createScalarRetrofitAPIService;
import static com.timm.ecommerce.analyzer.provider.util.RetrofitUtils.execute;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

@Slf4j
public class PCAPIClient {

    private final PCAPIService pcapiService;
    private static final ObjectMapper mapper = new ObjectMapper();

    public PCAPIClient(OkHttpClient okHttpClient) {
        pcapiService = createScalarRetrofitAPIService(okHttpClient,
                                                      PC_API_HOST,
                                                      PCAPIService.class);
    }

    public Optional<PCProductInfo> getGoodInfo(String productId) {
        return execute(pcapiService.queryProductInfo(productId), log)
                .flatMap(PCAPIClient::extractProductInfo);
    }

    private static Optional<PCProductInfo> extractProductInfo(String content) {
        final var pattern = Pattern.compile("jsonp_prod\\((\\{.*?\\})\\)");

        final var matcher = pattern.matcher(content);
        if (!matcher.find()) {
            return Optional.empty();
        }

        try {
            final var jsonStr = matcher.group(1);
            final var jsonNode = mapper.readTree(jsonStr);
            final var productId = jsonNode.fieldNames().next();
            if (productId == null) {
                log.error("empty product id {}", jsonStr);
                return Optional.empty();
            }
            final var name = jsonNode.get(productId).path("Name").asText();
            final var price = jsonNode.get(productId).path("Price").path("P").asLong();
            final var quality = jsonNode.get(productId).path("Qty").asInt();
            return Optional.of(
                    new PCProductInfo(productId, name, BigDecimal.valueOf(price), quality)
            );
        } catch (JsonProcessingException e) {
            log.error("failed to parse json string.", e);
        }
        return Optional.empty();
    }
}

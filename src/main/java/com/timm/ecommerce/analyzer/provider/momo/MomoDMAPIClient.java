package com.timm.ecommerce.analyzer.provider.momo;

import static com.timm.ecommerce.analyzer.provider.momo.Constant.MOMO_DM_HOST;
import static com.timm.ecommerce.analyzer.provider.util.RetrofitUtils.createRetrofitAPIService;
import static com.timm.ecommerce.analyzer.provider.util.RetrofitUtils.execute;

import java.io.IOException;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

@Slf4j
public class MomoDMAPIClient {

    private final MomoDMAPIService momoDMAPIService;

    public MomoDMAPIClient(OkHttpClient okHttpClient) {
        momoDMAPIService = createRetrofitAPIService(okHttpClient,
                                                    MOMO_DM_HOST,
                                                    MomoDMAPIService.class);
    }

    public Optional<String> getGoodInfo(String shareCode) {
        final var responseBody = execute(momoDMAPIService.getGoodInfo(shareCode), log);
        if (responseBody.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(responseBody.get().string());
        } catch (IOException e) {
            log.error("occurred IOException when getting content string", e);
            return Optional.empty();
        }
    }
}

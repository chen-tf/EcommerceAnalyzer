package com.timm.ecommerce.analyzer.provider.momo;

import static com.timm.ecommerce.analyzer.provider.momo.Constant.MOMO_MOBILE_HOST;
import static com.timm.ecommerce.analyzer.provider.util.RetrofitUtils.createScalarRetrofitAPIService;
import static com.timm.ecommerce.analyzer.provider.util.RetrofitUtils.execute;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

@Slf4j
public class MomoMobileAPIClient {

    private final MomoMobileAPIService momoMobileAPIService;

    public MomoMobileAPIClient(OkHttpClient okHttpClient) {
        momoMobileAPIService = createScalarRetrofitAPIService(okHttpClient,
                                                              MOMO_MOBILE_HOST,
                                                              MomoMobileAPIService.class);
    }

    public Optional<String> getGoodInfo(String iCode) {
        return execute(momoMobileAPIService.getGoodInfo(iCode), log);
    }
}

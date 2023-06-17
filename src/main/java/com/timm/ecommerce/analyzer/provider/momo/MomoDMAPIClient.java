package com.timm.ecommerce.analyzer.provider.momo;

import static com.timm.ecommerce.analyzer.provider.momo.Constant.MOMO_DM_HOST;
import static com.timm.ecommerce.analyzer.provider.util.RetrofitUtils.createScalarRetrofitAPIService;
import static com.timm.ecommerce.analyzer.provider.util.RetrofitUtils.execute;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

@Slf4j
public class MomoDMAPIClient {

    private final MomoDMAPIService momoDMAPIService;

    public MomoDMAPIClient(OkHttpClient okHttpClient) {
        momoDMAPIService = createScalarRetrofitAPIService(okHttpClient,
                                                          MOMO_DM_HOST,
                                                          MomoDMAPIService.class);
    }

    public Optional<String> getGoodInfo(String shareCode) {
        return execute(momoDMAPIService.getGoodInfo(shareCode), log);
    }
}

package com.timm.ecommerce.analyzer.provider.momo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MomoMobileAPIService {

    @GET("goods.momo")
    Call<String> getGoodInfo(@Query("i_code") String iCode);
}

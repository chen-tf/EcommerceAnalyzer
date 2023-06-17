package com.timm.ecommerce.analyzer.provider.momo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MomoDMAPIService {

    @GET("{shareCode}")
    Call<String> getGoodInfo(@Path("shareCode") String shareCode);
}

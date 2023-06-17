package com.timm.ecommerce.analyzer.provider.momo;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MomoDMAPIService {

    @GET("{shareCode}")
    Call<ResponseBody> getGoodInfo(@Path("shareCode") String shareCode);
}

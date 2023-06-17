package com.timm.ecommerce.analyzer.provider.pchome;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PCAPIService {

    @GET("ecshop/prodapi/v2/prod/{productId}&fields=Id,Name,Price,Qty&_callback=jsonp_prod?_callback=jsonp_prod")
    Call<String> queryProductInfo(@Path("productId") String productId);
}

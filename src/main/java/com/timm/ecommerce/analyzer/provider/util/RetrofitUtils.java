package com.timm.ecommerce.analyzer.provider.util;

import static com.timm.ecommerce.analyzer.provider.util.UrlUtils.getHTTPSBaseUrl;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public final class RetrofitUtils {

    private RetrofitUtils() {}

    public static <R> Optional<R> execute(final Call<R> call, Logger log) {
        try {
            final Response<R> response = call.execute();
            return response.isSuccessful()
                   ? Optional.ofNullable(response.body())
                   : Optional.empty();
        } catch (Exception e) {
            log.error("failed to execute API.", e);
            return Optional.empty();
        }
    }

    @NotNull
    public static <T> T createScalarRetrofitAPIService(OkHttpClient httpClient,
                                                       String host,
                                                       Class<T> service) {
        return new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(getHTTPSBaseUrl(host))
                .client(httpClient)
                .build()
                .create(service);
    }
}

package com.timm.ecommerce.analyzer.provider.util;

import java.util.Optional;

import org.slf4j.Logger;

import retrofit2.Call;
import retrofit2.Response;

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
}

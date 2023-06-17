package com.timm.ecommerce.analyzer.provider.momo;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;

import com.timm.ecommerce.analyzer.provider.ProductInfo;
import com.timm.ecommerce.analyzer.provider.ProductInfoProvider;
import com.timm.ecommerce.analyzer.provider.ProductSource;
import com.timm.ecommerce.analyzer.provider.ProductStatus;
import com.timm.ecommerce.analyzer.provider.util.RetrofitUtils;
import com.timm.ecommerce.analyzer.provider.util.UrlUtils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

@Slf4j
public class Momo extends ProductInfoProvider {

    private final MomoMobileAPIService momoMobileAPIService;
    private final MomoDMAPIService momoDMAPIService;

    private static final String MOMO_HOST = "www.momoshop.com.tw";
    private static final String MOMO_MOBILE_HOST = "m.momoshop.com.tw";
    private static final String MOMO_DM_HOST = "momo.dm";

    private final ProductQueryStrategy shareLinkProductQueryStrategy;
    private final ProductQueryStrategy commonProductQueryStrategy;

    public Momo(OkHttpClient httpClient) {
        super(Set.of(MOMO_HOST, MOMO_MOBILE_HOST, MOMO_DM_HOST));
        momoMobileAPIService = createRetrofitAPIService(httpClient,
                                                        MOMO_MOBILE_HOST,
                                                        MomoMobileAPIService.class);
        momoDMAPIService = createRetrofitAPIService(httpClient,
                                                    MOMO_DM_HOST,
                                                    MomoDMAPIService.class);
        shareLinkProductQueryStrategy = new ShareLinkProductQueryStrategy();
        commonProductQueryStrategy = new CommonProductQueryStrategy();
    }

    @NotNull
    private static <T> T createRetrofitAPIService(OkHttpClient httpClient,
                                                  String host,
                                                  Class<T> service) {
        return new Retrofit.Builder()
                .baseUrl(String.format("https://%s/", host))
                .client(httpClient)
                .build()
                .create(service);
    }

    @Override
    protected Optional<ProductInfo> retrieveByUrlString(String urlString) {
        final Optional<ProductPage> queryResultOpt = isMomoShareLink(urlString)
                                                     ? shareLinkProductQueryStrategy.query(urlString)
                                                     : commonProductQueryStrategy.query(urlString);
        return queryResultOpt
                .filter(queryResult -> queryResult.responseBodyOpt.isPresent())
                .flatMap(queryResult -> parse(queryResult.iCodeOpt, queryResult.responseBodyOpt.get()));
    }

    record ProductPage(Optional<String> iCodeOpt, Optional<ResponseBody> responseBodyOpt) {
    }

    @FunctionalInterface
    interface ProductQueryStrategy {
        Optional<ProductPage> query(String urlString);
    }

    class ShareLinkProductQueryStrategy implements ProductQueryStrategy {

        @Override
        public Optional<ProductPage> query(String urlString) {
            final var shareCode = UrlUtils.getPathParameterAsString(urlString, 0);
            if (shareCode.isEmpty()) {
                log.error("can't find share code from the url:{}", urlString);
                return Optional.empty();
            }
            return Optional.of(new ProductPage(
                    Optional.empty(),
                    RetrofitUtils.execute(momoDMAPIService.getGoodInfo(shareCode.get()), log)
            ));
        }
    }

    class CommonProductQueryStrategy implements ProductQueryStrategy {

        @Override
        public Optional<ProductPage> query(String urlString) {
            final var iCodeOpt = UrlUtils.getParameterAsString(urlString, "i_code");
            if (iCodeOpt.isEmpty()) {
                log.error("can't find i_code from the url:{}", urlString);
                return Optional.empty();
            }
            return Optional.of(new ProductPage(
                    iCodeOpt,
                    RetrofitUtils.execute(momoMobileAPIService.getGoodInfo(iCodeOpt.get()), log)
            ));
        }
    }

    private static boolean isMomoShareLink(String urlString) {
        return UrlUtils.getHostFromUrl(urlString)
                       .filter(host -> host.equals(MOMO_DM_HOST))
                       .isPresent();
    }

    private static Optional<ProductInfo> parse(Optional<String> iCodeOpt, ResponseBody responseBody) {
        try {
            final var html = responseBody.string();
            final var doc = Jsoup.parse(html);
            final var titleMeta = doc.selectFirst("meta[property=og:title]");
            final var priceMeta = doc.selectFirst("meta[property=product:price:amount]");
            final var availabilityMeta = doc.selectFirst("meta[property=product:availability]");
            final var iCode = iCodeOpt.orElseGet(() -> matchICode(html));
            if (titleMeta == null || priceMeta == null || availabilityMeta == null || iCode == null) {
                return Optional.empty();
            }

            final var contentAttr = "content";
            final var name = titleMeta.attr(contentAttr);
            final var price = priceMeta.attr(contentAttr).replace(",", "");
            final ProductStatus productStatus;
            if ("in stock".equalsIgnoreCase(availabilityMeta.attr(contentAttr))) {
                productStatus = ProductStatus.IN_STOCK;
            } else {
                productStatus = ProductStatus.OUT_OF_STOCK;
            }
            return Optional.of(
                    new ProductInfo(iCode, name, new BigDecimal(price), productStatus,
                                    ProductSource.MOMO));
        } catch (Exception e) {
            log.error("failed to parse the response and retrieve product information from Momo Shop.", e);
            return Optional.empty();
        }
    }

    @Nullable
    private static String matchICode(String content) {
        final var pattern = "i_code=(\\d+)";

        final var regex = Pattern.compile(pattern);
        final var matcher = regex.matcher(content);
        return matcher.find()
               ? matcher.group(1)
               : null;
    }
}

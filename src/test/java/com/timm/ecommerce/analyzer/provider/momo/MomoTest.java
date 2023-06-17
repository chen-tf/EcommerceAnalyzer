package com.timm.ecommerce.analyzer.provider.momo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.timm.ecommerce.analyzer.provider.ProductInfo;
import com.timm.ecommerce.analyzer.provider.ProductSource;
import com.timm.ecommerce.analyzer.provider.ProductStatus;

@ExtendWith(MockitoExtension.class)
class MomoTest {

    private Momo momo;

    @Mock
    private MomoDMAPIClient momoDMAPIClient;

    @Mock
    private MomoMobileAPIClient momoMobileAPIClient;

    @BeforeEach
    void setUp() {
        momo = new Momo(momoDMAPIClient, momoMobileAPIClient);
    }

    @Nested
    class Retrieve {

        private static final String VALID_COMMON_URL =
                "https://www.momoshop.com.tw/goods/GoodsDetail.jsp?i_code=%s";
        private static final String VALID_SHARE_URL = "https://momo.dm/%s";

        @Test
        void notSupportedDomain_shouldEmpty() {
            assertThat(momo.retrieve("https://not-supported.com"))
                    .isEmpty();

            then(momoDMAPIClient).shouldHaveNoInteractions();
            then(momoMobileAPIClient).shouldHaveNoInteractions();
        }

        @ParameterizedTest
        @CsvSource({
                """
                        <meta property="product:availability" content="in stock">
                        <meta property="product:price:amount" content="30900">
                        """,
                """
                        <meta property="og:title" content="【Apple】iPhone 14 (128G/6.1吋)">
                        <meta property="product:availability" content="in stock">
                        """,
                """
                        <meta property="og:title" content="【Apple】iPhone 14 (128G/6.1吋)">
                        <meta property="product:price:amount" content="30900">
                        """
        })
        void commonPageMissingRequiredMeta_shouldEmpty(String response) {
            given(momoMobileAPIClient.getGoodInfo("168"))
                    .willReturn(Optional.of(response));

            assertThat(momo.retrieve(String.format(VALID_COMMON_URL, "168")))
                    .isEmpty();
        }

        @ParameterizedTest
        @CsvSource({
                """
                        <meta property="og:title" content="【Apple】iPhone 14 (128G/6.1吋)">
                        <meta property="product:availability" content="in stock">
                        <meta property="product:price:amount" content="30900">
                        """,
                """
                        <link rel="canonical" href="https://www.momoshop.com.tw/goods/GoodsDetail.jsp?i_code=168">
                        <meta property="og:title" content="【Apple】iPhone 14 (128G/6.1吋)">
                        <meta property="product:availability" content="in stock">
                        """,
                """
                        <link rel="canonical" href="https://www.momoshop.com.tw/goods/GoodsDetail.jsp?i_code=168">
                        <meta property="og:title" content="【Apple】iPhone 14 (128G/6.1吋)">
                        <meta property="product:price:amount" content="30900">
                        """,
                """
                        <link rel="canonical" href="https://www.momoshop.com.tw/goods/GoodsDetail.jsp?i_code=168">
                        <meta property="og:title" content="【Apple】iPhone 14 (128G/6.1吋)">
                        <meta property="product:price:amount" content="30900">
                        """
        })
        void sharePageMissingRequiredMeta_shouldEmpty(String response) {
            given(momoDMAPIClient.getGoodInfo("f52z3"))
                    .willReturn(Optional.of(response));

            assertThat(momo.retrieve(String.format(VALID_SHARE_URL, "f52z3")))
                    .isEmpty();
        }

        @Test
        void commonPageContainsAllMeta_shouldReturnProductInfo() {
            final var response = """
                    <meta property="og:title" content="【Apple】iPhone 14 (128G/6.1吋)">
                    <meta property="product:availability" content="in stock">
                    <meta property="product:price:amount" content="30900">
                    """;
            given(momoMobileAPIClient.getGoodInfo("168"))
                    .willReturn(Optional.of(response));

            assertThat(momo.retrieve(String.format(VALID_COMMON_URL, "168")))
                    .hasValue(new ProductInfo("168",
                                              "【Apple】iPhone 14 (128G/6.1吋)",
                                              BigDecimal.valueOf(30900),
                                              ProductStatus.IN_STOCK,
                                              ProductSource.MOMO));
        }

        @Test
        void sharePageContainsAllMeta_shouldReturnProductInfo() {
            final var response = """
                    <link rel="canonical" href="https://www.momoshop.com.tw/goods/GoodsDetail.jsp?i_code=168">
                    <meta property="og:title" content="【Apple】iPhone 14 (128G/6.1吋)">
                    <meta property="product:availability" content="in stock">
                    <meta property="product:price:amount" content="30900">
                    """;
            given(momoDMAPIClient.getGoodInfo("f52z3"))
                    .willReturn(Optional.of(response));

            assertThat(momo.retrieve(String.format(VALID_SHARE_URL, "f52z3")))
                    .hasValue(new ProductInfo("168",
                                              "【Apple】iPhone 14 (128G/6.1吋)",
                                              BigDecimal.valueOf(30900),
                                              ProductStatus.IN_STOCK,
                                              ProductSource.MOMO));
        }
    }
}

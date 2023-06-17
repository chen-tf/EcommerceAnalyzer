package com.timm.ecommerce.analyzer;

import java.util.Optional;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        final var analyzer = new EcommerceAnalyzer();
        Stream.of(
//                      "https://m.momoshop.com.tw/goods.momo?i_code=11158539&mdiv=8100200000-bt_8_015_01-bt_8_015_01_P3_1_e1&ctype=B",
//                      "https://momo.dm/fQ6fEu",
                      "https://www.momoshop.com.tw/goods/GoodsDetail.jsp?i_code=8084811&Area=search&mdiv=403&oid=1_2&cid=index&kw=%E6%84%9B%E9%A6%AC%E4%BB%95%E5%A4%A7%E5%9C%B0",
                      "https://24h.pchome.com.tw/prod/DDAHGR-A900A0T3V"
              )
              .map(analyzer::analyze)
              .filter(Optional::isPresent)
              .forEach(System.out::println);
    }
}

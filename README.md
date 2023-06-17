# EcommerceAnalyzer
This is a utility library for analyzing the name, price, ID, and status of products through e-commerce product URLs.

# How to Use
Just create an `EcommerceAnalyzer` object and provide a supported e-commerce website product URL as input. You will then receive the product information, including the ID, name, price, and status.
```java
public static void main(String[] args) {
      final var analyzer = new EcommerceAnalyzer();
      Stream.of(
                    "https://momo.dm/fQ6fEu",
                    "https://www.momoshop.com.tw/goods/GoodsDetail.jsp?i_code=8084811&Area=search&mdiv=403&oid=1_2&cid=index&kw=%E6%84%9B%E9%A6%AC%E4%BB%95%E5%A4%A7%E5%9C%B0",
                    "https://24h.pchome.com.tw/prod/DDAHGR-A900A0T3V"
            )
            .map(analyzer::analyze)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(System.out::println);
            // ProductInfo[id=11468391, name=【Apple】S級福利品 MacBook Air 13.6吋 M2晶片 8核心CPU 與 8核心GPU 8G/256G SSD(官方整新機), price=30900, status=IN_STOCK, source=MOMO]
            // ProductInfo[id=8084811, name=【Hermes 愛馬仕】大地男性淡香水/愛馬仕之光純香淡香精/H24男性淡香水(國際航空版/多款任選), price=2399, status=IN_STOCK, source=MOMO]
            // ProductInfo[id=DDAHGR-A900A0T3V-000, name=HERMES 愛馬仕 大地 男性淡香水 100ml, price=2688, status=IN_STOCK, source=PCHOME]
}
```

## Support
1. MOMO shop (https://www.momoshop.com.tw/main/Main.jsp)
2. PChome (https://24h.pchome.com.tw/)

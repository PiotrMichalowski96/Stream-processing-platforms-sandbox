package com.university.stock.market.model.util;

import com.university.stock.market.model.domain.Stock;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

@UtilityClass
public class StockGenerator {

  private static final MathContext MATH_CONTEXT = new MathContext(4);

  private static final List<String> STOCK_TYPES = List.of("Common Stock", "Index", "Digital Currency",
      "Physical Currency", "ETF");
  private static final List<String> CURRENCIES = List.of("USD", "EUR", "PLN", "GBP", "BTC", "CHF");

  public static List<Stock> generateRandomStockList(int uniqueStocks) {
    return Stream.generate(() -> Stock.builder()
            .ticker(RandomStringUtils.randomAlphabetic(4).toUpperCase())
            .type(randomStringFrom(STOCK_TYPES))
            .exchange(RandomStringUtils.randomAlphabetic(8).toUpperCase())
            .price(RandomUtils.nextDouble(100, 10000))
            .currency(randomStringFrom(CURRENCIES))
            .volume(BigInteger.valueOf(RandomUtils.nextLong(100L, 10000L)))
            .timestamp(LocalDateTime.now())
            .build())
        .limit(uniqueStocks)
        .collect(Collectors.toList());
  }

  public static void updateRandomExchange(Stock stock) {
    BigDecimal currentPrice = stock.getPrice();
    BigDecimal updatedPrice = currentPrice.multiply(new BigDecimal(RandomUtils.nextDouble(0.95, 1.05), MATH_CONTEXT));
    stock.setPrice(updatedPrice);
    stock.setVolume(BigInteger.valueOf(RandomUtils.nextLong(100L, 10000L)));
    stock.setTimestamp(LocalDateTime.now());
  }

  private static String randomStringFrom(List<String> strings) {
    int index = RandomUtils.nextInt(0, strings.size());
    return strings.get(index);
  }
}

package com.university.stock.market.trading.analysis.util;

import com.university.stock.market.model.domain.Stock;
import java.math.BigInteger;
import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TradeTestDataGenerator {

  public static Stock createTradeWithPrice(String ticker, double price, int seconds) {
    return Stock.builder()
        .ticker(ticker)
        .type("Bitcoin")
        .exchange("Stock Common Exchange")
        .price(price)
        .currency("USD")
        .volume(BigInteger.valueOf(1234))
        .timestamp(LocalDateTime.now().plusSeconds(seconds))
        .build();
  }

  public static Stock createTradeWithPrice(double price, int seconds) {
    return createTradeWithPrice("USD/BTC", price, seconds);
  }
}

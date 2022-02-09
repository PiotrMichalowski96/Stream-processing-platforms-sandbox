package com.university.stock.producer.config;

import com.university.stock.model.domain.Stock;
import com.university.stock.model.util.StockGenerator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StockMarketConfig {

  @Value("${stock.market.unique.quotes}")
  private final Integer uniqueQuotes;

  @Bean
  public List<Stock> uniqueStockList() {
    return StockGenerator.generateRandomStockList(uniqueQuotes);
  }
}

package com.university.stock.market.trading.analysis.config;

import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.domain.StockStatus;
import com.university.stock.market.trading.analysis.service.TradingAnalysisService;
import com.university.stock.market.trading.analysis.service.TradingAnalysisServiceImpl;
import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = TradingProperties.class)
public class TradingAnalysisConfig {

  @Bean
  TradingAnalysisService<StockStatus, Stock> tradingAnalysisService(TradingProperties tradingProperties) {
    Duration tradeDuration = Duration.ofMillis(tradingProperties.getTradeDurationInMillis());
    int maxBarCount = tradingProperties.getMaximumBars();
    return new TradingAnalysisServiceImpl(tradeDuration, maxBarCount);
  }
}

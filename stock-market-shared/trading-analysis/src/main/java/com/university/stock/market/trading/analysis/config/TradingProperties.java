package com.university.stock.market.trading.analysis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "trading")
@Data
public class TradingProperties {
  int maximumBars;
  long tradeDurationInMillis;
}

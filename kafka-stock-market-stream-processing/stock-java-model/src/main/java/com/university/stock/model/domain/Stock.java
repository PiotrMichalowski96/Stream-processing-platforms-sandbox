package com.university.stock.model.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Stock {

  private static final MathContext MATH_CONTEXT = new MathContext(4);

  private String ticker;
  private Sector sector;
  private BigDecimal exchange;
  private Instant instant;

  public Stock(String ticker, Sector sector, Double exchange, Instant instant) {
    this.ticker = ticker;
    this.sector = sector;
    this.exchange = new BigDecimal(exchange, MATH_CONTEXT);
    this.instant = instant;
  }
}

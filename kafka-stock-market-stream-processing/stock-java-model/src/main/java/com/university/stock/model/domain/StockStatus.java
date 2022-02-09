package com.university.stock.model.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockStatus {

  private Stock recentQuota;
  private BigDecimal maxExchange;
  private BigDecimal minExchange;
  private BigDecimal diffExchange;
}

package com.university.stock.model.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class StockStatus {

  private Stock recentQuota;
  private BigDecimal maxExchange;
  private BigDecimal minExchange;
  private BigDecimal diffExchange;
}

package com.university.stock.market.model.domain;

import java.io.Serializable;
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
public class StockStatus implements Serializable {

  private Stock recentQuota;
  private BigDecimal maxPrice;
  private BigDecimal minPrice;
  private BigDecimal diffPrice;
}

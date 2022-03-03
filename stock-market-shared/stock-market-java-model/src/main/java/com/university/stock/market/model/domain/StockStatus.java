package com.university.stock.market.model.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class StockStatus implements Serializable {

  private Stock recentQuota;
  private BigDecimal maxPrice;
  private BigDecimal minPrice;
  private BigDecimal diffPrice;
}

package com.university.stock.market.model.dto;

import lombok.Data;

@Data
public class QuoteDTO {
  private String event;
  private String symbol;
  private String currency;
  private String exchange;
  private String type;
  private Long timestamp;
  private Long price;
  private Long day_volume;
}

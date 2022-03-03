package com.university.stock.market.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class QuoteDTO {
  private String event;
  private String symbol;
  private String currency;
  private String exchange;
  private String type;
  private Long timestamp;
  private Double price;
  @JsonProperty("day_volume")
  private Long dayVolume;
}

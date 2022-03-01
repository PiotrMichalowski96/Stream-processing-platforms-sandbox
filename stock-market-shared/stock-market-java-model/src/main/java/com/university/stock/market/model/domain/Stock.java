package com.university.stock.market.model.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Stock implements Serializable {

  private static final MathContext MATH_CONTEXT = new MathContext(4);

  private String ticker;
  private Sector sector;
  private BigDecimal exchange;
  @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime dateTime;

  public Stock(String ticker, Sector sector, Double exchange, LocalDateTime dateTime) {
    this.ticker = ticker;
    this.sector = sector;
    this.exchange = new BigDecimal(exchange, MATH_CONTEXT);
    this.dateTime = dateTime;
  }
}

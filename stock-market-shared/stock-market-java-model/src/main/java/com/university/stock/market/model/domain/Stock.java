package com.university.stock.market.model.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
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
public class Stock implements Serializable {

  private String ticker;
  private String type;
  private String exchange;
  private BigDecimal price;
  private String currency;
  private BigInteger volume;
  @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime timestamp;

  public static class StockBuilder {

    private static final int SCALE = 4;

    private BigDecimal price;

    public StockBuilder price(Double price) {
      this.price = new BigDecimal(price, MathContext.DECIMAL64);
      this.price = this.price.setScale(SCALE, RoundingMode.HALF_UP);
      return this;
    }
  }
}

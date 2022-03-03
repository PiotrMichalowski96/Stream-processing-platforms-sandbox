package com.university.stock.market.common.test.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.university.stock.market.model.domain.Stock;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class JsonUtilTest {

  @Test
  void shouldExtractStockFromJsonFile() {
    //given
    String jsonFilePath = "src/test/resources/stock.json";

    Stock expectedStock = Stock.builder()
        .ticker("WPQLENRN")
        .type("Common Stock")
        .exchange("NASDAQ")
        .price(13532.4590)
        .currency("USD")
        .timestamp(LocalDateTime.of(2022, 2, 11, 20, 49, 48))
        .build();

    //when
    Stock actualStock = JsonUtil.extractFromJson(Stock.class, jsonFilePath);

    //then
    assertThat(actualStock).usingRecursiveComparison().isEqualTo(expectedStock);
  }
}
package com.university.stock.market.common.test.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.university.stock.market.model.domain.Sector;
import com.university.stock.market.model.domain.Stock;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class JsonUtilTest {

  @Test
  void shouldExtractStockFromJsonFile() {
    //given
    String jsonFilePath = "src/test/resources/stock.json";

    Stock expectedStock = new Stock("WPQLENRN", Sector.HEALTH_SERVICE,
        BigDecimal.valueOf(7095.2547), LocalDateTime.of(2022, 2, 11, 20, 49, 48));

    //when
    Stock actualStock = JsonUtil.extractFromJson(Stock.class, jsonFilePath);

    //then
    assertThat(actualStock).usingRecursiveComparison().isEqualTo(expectedStock);
  }
}
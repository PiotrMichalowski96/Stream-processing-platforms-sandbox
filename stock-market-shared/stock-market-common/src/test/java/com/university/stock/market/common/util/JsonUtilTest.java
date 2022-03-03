package com.university.stock.market.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.dto.QuoteDTO;
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

  @Test
  void shouldConvertJsonToQuoteObject() {
    //given
    String json = "{\"event\":\"price\",\"symbol\":\"AAPL\",\"currency\":\"USD\",\"exchange\":\"NASDAQ\",\"type\":\"Common Stock\",\"timestamp\":1646330534,\"price\":166.9400,\"day_volume\":42399195}";

    QuoteDTO expectedQuote = QuoteDTO.builder()
        .event("price")
        .symbol("AAPL")
        .currency("USD")
        .exchange("NASDAQ")
        .type("Common Stock")
        .timestamp(1646330534L)
        .price(166.9400)
        .dayVolume(42399195L)
        .build();

    //when
    QuoteDTO actualQuote = JsonUtil.convertToObjectFrom(QuoteDTO.class, json);

    //then
    assertThat(actualQuote).usingRecursiveComparison().isEqualTo(expectedQuote);
  }
}
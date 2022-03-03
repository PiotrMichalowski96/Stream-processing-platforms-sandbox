package com.university.stock.producer.mapper;


import static org.assertj.core.api.Assertions.assertThat;

import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.dto.QuoteDTO;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class StockMapperTest {

  private final StockMapper stockMapper = new StockMapperImpl();

  @Test
  void shouldMapQuoteDtoToStock() {
    //given
    QuoteDTO quoteDTO = QuoteDTO.builder()
        .event("price")
        .symbol("USD/JPY")
        .currency("USD")
        .exchange("NASDAQ")
        .type("Index")
        .timestamp(1646169749L)
        .price(13532.4590)
        .dayVolume(5356822000L)
        .build();

    Stock expectedStock = Stock.builder()
        .ticker("USD/JPY")
        .type("Index")
        .exchange("NASDAQ")
        .price(13532.4590)
        .currency("USD")
        .timestamp(LocalDateTime.of(2022, 3, 1, 22, 22, 29))
        .build();

    //when
    Stock actualStock = stockMapper.toStock(quoteDTO);

    //then
    assertThat(actualStock).usingRecursiveComparison().isEqualTo(expectedStock);
  }
}
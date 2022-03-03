package com.university.stock.market.model.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.university.stock.market.model.domain.Stock;
import java.util.List;
import org.junit.jupiter.api.Test;

class StockGeneratorTest {

  @Test
  void shouldGenerateRandomStockList() {
    //given
    int stockListSize = 30;

    //when
    List<Stock> stockList = StockGenerator.generateRandomStockList(stockListSize);

    //then
    assertThat(stockList).hasSize(stockListSize);
    stockList.forEach(stock -> assertThat(stock).hasNoNullFieldsOrProperties());
  }
}
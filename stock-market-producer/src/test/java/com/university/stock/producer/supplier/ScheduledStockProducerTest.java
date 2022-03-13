package com.university.stock.producer.supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.university.stock.market.model.domain.Stock;
import com.university.stock.producer.repository.StockMarketRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduledStockProducerTest {

  @Mock
  private StockMarketRepository stockMarketRepository;

  @Captor
  private ArgumentCaptor<Stock> stockCaptor;

  @Test
  void shouldProduceStocks() {
    //given
    int stockListSize = 30;
    ScheduledStockProducer stockProducer = new ScheduledStockProducer(stockMarketRepository, stockListSize);

    //when
    stockProducer.startSendingStocksProcess();

    //then
    verify(stockMarketRepository, times(stockListSize)).send(stockCaptor.capture());
    List<Stock> producedStocks = stockCaptor.getAllValues();
    assertThat(producedStocks).hasSize(stockListSize);
  }
}
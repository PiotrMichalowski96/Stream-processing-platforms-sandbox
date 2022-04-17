package com.university.mongo.stock.consumer.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.university.mongo.stock.consumer.entity.StockStatusDoc;
import com.university.mongo.stock.consumer.repository.StockStatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockStatusDocProducerTest {

  @InjectMocks
  private StockStatusDocProducer stockStatusDocProducer;
  @Mock
  private StockStatusRepository stockStatusRepository;

  @Test
  void shouldSaveStockStatusDoc() {
    //given
    StockStatusDoc stockStatusDoc = StockStatusDoc.builder()
        .streamPlatform("KAFKA_STREAMS")
        .processingTimeInMillis(100L)
        .experimentCase("experiment case")
        .comment("description")
        .message("{json}")
        .build();

    when(stockStatusRepository.save(stockStatusDoc)).thenReturn(stockStatusDoc);

    //when
    stockStatusDocProducer.sendMessage(stockStatusDoc);

    //then
    verify(stockStatusRepository).save(stockStatusDoc);
  }

}
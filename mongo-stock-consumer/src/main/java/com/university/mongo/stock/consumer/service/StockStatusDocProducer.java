package com.university.mongo.stock.consumer.service;

import com.university.mongo.stock.consumer.entity.StockStatusDoc;
import com.university.mongo.stock.consumer.repository.StockStatusRepository;
import com.university.stock.market.consumer.repository.MessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockStatusDocProducer implements MessageProducer<StockStatusDoc> {

  private final StockStatusRepository stockStatusRepository;

  @Override
  public void sendMessage(StockStatusDoc message) {
    StockStatusDoc savedStockStatusDoc = stockStatusRepository.save(message);
    logger.debug("Saved document: {}", savedStockStatusDoc);
  }
}

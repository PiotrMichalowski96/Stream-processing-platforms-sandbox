package com.university.stock.producer.service;

import com.university.stock.model.domain.Stock;
import com.university.stock.model.util.StockGenerator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class StockMarketSchedulerImpl implements StockMarketScheduler {

//  private final StockMarketProducer stockProducer;
  private final List<Stock> uniqueStockList;

  @Override
  @Scheduled(fixedRateString = "${stock.market.schedule}")
  public void scheduleStockMarketUpdate() {
    logger.debug("Start updating stocks");

    uniqueStockList.forEach(stock -> {
      StockGenerator.updateRandomExchange(stock);
      logger.debug("Stock: {}", stock);
//      stockProducer.send(stock);
    });
  }
}

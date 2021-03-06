package com.university.stock.producer.supplier;

import com.university.stock.market.model.domain.InputMetadataDetails;
import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.util.StockGenerator;
import com.university.stock.producer.repository.StockMarketRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@ConditionalOnProperty(name = "twelvedata.webservice.enable", havingValue = "false", matchIfMissing = true)
@Service
public class ScheduledStockProducer implements StockMarketProducer {

  private final StockMarketRepository stockMarketRepository;
  private final List<Stock> stockList;

  public ScheduledStockProducer(StockMarketRepository stockMarketRepository,
      @Value("${stock.market.unique.quotes:30}")Integer uniqueQuotes,
      InputMetadataDetails inputMetadataDetails) {

    this.stockMarketRepository = stockMarketRepository;
    this.stockList = StockGenerator.generateRandomStockList(uniqueQuotes, inputMetadataDetails);
  }

  @Scheduled(fixedRateString = "${stock.market.schedule:500}")
  @Override
  public void startSendingStocksProcess() {
    logger.debug("Start updating stocks");

    stockList.forEach(stock -> {
      StockGenerator.updateRandomExchange(stock);
      logger.debug("Stock: {}", stock);
      stockMarketRepository.send(stock);
    });
  }
}

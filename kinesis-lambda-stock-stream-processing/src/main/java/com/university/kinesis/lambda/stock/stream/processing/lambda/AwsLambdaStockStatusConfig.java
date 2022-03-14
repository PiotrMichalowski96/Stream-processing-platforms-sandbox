package com.university.kinesis.lambda.stock.stream.processing.lambda;

import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.university.kinesis.lambda.stock.stream.processing.kinesis.KinesisStockStatusRepository;
import com.university.stock.market.common.util.JsonUtil;
import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.domain.StockStatus;
import com.university.stock.market.trading.analysis.service.TradingAnalysisService;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//TODO: change logging to debbug level

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AwsLambdaStockStatusConfig {

  private final TradingAnalysisService<StockStatus, Stock> tradingAnalysisService;
  private final KinesisStockStatusRepository stockStatusRepository;
  private final Map<String, StockStatus> actualStockStatusMap = new HashMap<>();

  @Bean
  public Consumer<KinesisEvent> kinesisRecordsProcessing() {
    return kinesisEvent -> {

      logger.info("Executing Spring Adapter for AWS Lambda");

      kinesisEvent.getRecords().stream()
          .map(rec -> rec.getKinesis().getData().array())
          .map(String::new)
          .peek(json -> logger.info("Json: {}", json))
          .map(json -> JsonUtil.convertToObjectFrom(Stock.class, json))
          .filter(Objects::nonNull)
          .map(stock -> {
            String stockKey = stock.getTicker();
            StockStatus previousStockStatus = actualStockStatusMap.getOrDefault(stockKey, new StockStatus());
            StockStatus actualStockStatus = tradingAnalysisService.updateTradeAnalysis(previousStockStatus, stock);
            actualStockStatusMap.put(stockKey, actualStockStatus);
            return actualStockStatus;
          })
          .forEach(stockStatusRepository::send);
    };
  }

}

package com.university.kinesis.lambda.stock.stream.processing.lambda;

import static com.university.kinesis.lambda.stock.stream.processing.util.MetadataDetailsUtil.createResultMetadataDetails;

import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.university.kinesis.lambda.stock.stream.processing.kinesis.KinesisStockStatusRepository;
import com.university.stock.market.common.util.JsonUtil;
import com.university.stock.market.model.domain.ResultMetadataDetails;
import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.domain.StockStatus;
import com.university.stock.market.trading.analysis.config.TradingAnalysisConfig;
import com.university.stock.market.trading.analysis.service.TradingAnalysisService;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Slf4j
@Configuration
@Import(TradingAnalysisConfig.class)
@RequiredArgsConstructor
public class AwsLambdaStockStatusConfig {

  private final TradingAnalysisService<StockStatus, Stock> tradingAnalysisService;
  private final KinesisStockStatusRepository stockStatusRepository;
  private final Map<String, StockStatus> actualStockStatusMap = new HashMap<>();

  @Bean
  public Consumer<KinesisEvent> kinesisRecordsProcessing() {
    return kinesisEvent -> kinesisEvent.getRecords().stream()
        .map(rec -> rec.getKinesis().getData().array())
        .map(String::new)
        .peek(json -> logger.debug("Json: {}", json))
        .map(json -> JsonUtil.convertToObjectFrom(Stock.class, json))
        .filter(Objects::nonNull)
        .map(calculateActualStockStatus())
        .map(updateMetadataDetails())
        .forEach(stockStatusRepository::send);
  }

  private Function<Stock, StockStatus> calculateActualStockStatus() {
    return stock -> {
      String stockKey = stock.getTicker();
      StockStatus previousStockStatus = actualStockStatusMap.getOrDefault(stockKey, new StockStatus());
      StockStatus actualStockStatus = tradingAnalysisService.updateTradeAnalysis(previousStockStatus, stock);
      actualStockStatusMap.put(stockKey, actualStockStatus);
      return actualStockStatus;
    };
  }

  private Function<StockStatus, StockStatus> updateMetadataDetails() {
    return stockStatus -> {
      ResultMetadataDetails resultMetadataDetails = createResultMetadataDetails(stockStatus.getRecentQuota());
      stockStatus.setResultMetadataDetails(resultMetadataDetails);
      return stockStatus;
    };
  }
}

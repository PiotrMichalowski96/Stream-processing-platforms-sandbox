package com.university.spark.stock.processing.stream;

import static com.university.stock.market.model.domain.ResultMetadataDetails.StreamProcessing.SPARK;
import static com.university.stock.market.model.util.ResultMetadataDetailsUtil.createMetadataDetails;

import com.university.spark.stock.processing.repository.StockStatusRepository;
import com.university.spark.stock.processing.repository.StockStatusRepositoryImpl;
import com.university.stock.market.model.domain.ResultMetadataDetails;
import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.domain.StockStatus;
import com.university.stock.market.model.domain.StockStatus.TradeAction;
import com.university.stock.market.trading.analysis.service.TradingAnalysisService;
import com.university.stock.market.trading.analysis.service.TradingAnalysisServiceImpl;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import scala.Tuple2;

@Slf4j
@Getter
public class StockMarketDStream {

  //TODO: check if can be refactored to not be static
  private static final TradingAnalysisService<StockStatus, Stock> TRADING_ANALYSIS_SERVICE = new TradingAnalysisServiceImpl(Duration.ofMillis(100), 400);

  private final JavaStreamingContext streamingContext;
  private final String inputTopic;
  private final String outputTopic;
  private final StockStatusRepository stockStatusRepository;

  public StockMarketDStream(JavaStreamingContext streamingContext, String inputTopic,
      String outputTopic, Properties kafkaProducerProperties) {

    this.streamingContext = streamingContext;
    this.inputTopic = inputTopic;
    this.outputTopic = outputTopic;
    this.stockStatusRepository = new StockStatusRepositoryImpl(outputTopic, kafkaProducerProperties);
  }

  public void stockStream(Map<String, Object> kafkaParams) {

    JavaInputDStream<ConsumerRecord<String, Stock>> stream = KafkaUtils.createDirectStream(streamingContext,
        LocationStrategies.PreferConsistent(),
        ConsumerStrategies.Subscribe(List.of(inputTopic), kafkaParams));

    stream.filter(item -> Objects.nonNull(item.value()))
        .mapToPair(item -> new Tuple2<>(item.value().getTicker(), item.value()))
        .mapValues(StockMarketDStream::mapToStockStatus)
        .reduceByKeyAndWindow((oldStatus, newStatus) -> {
              Stock newTrade = newStatus.getRecentQuota();
              StockStatus updatedStatus = TRADING_ANALYSIS_SERVICE.updateTradeAnalysis(oldStatus, newTrade);
              ResultMetadataDetails resultMetadataDetails = createMetadataDetails(SPARK, newTrade);
              updatedStatus.setResultMetadataDetails(resultMetadataDetails);
              return updatedStatus;
            },
            Durations.seconds(10))
        .foreachRDD(rdd -> rdd.collect()
            .forEach(record -> stockStatusRepository.send(record._1, record._2))
        );
  }

  private static StockStatus mapToStockStatus(Stock stock) {
    BigDecimal price = stock.getPrice();
    return StockStatus.builder()
        .recentQuota(stock)
        .minPrice(price)
        .maxPrice(price)
        .diffPrice(BigDecimal.ZERO)
        .tradeAction(TradeAction.SELL)
        .build();
  }
}

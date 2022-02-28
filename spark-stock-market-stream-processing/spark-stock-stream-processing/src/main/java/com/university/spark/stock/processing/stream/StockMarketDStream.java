package com.university.spark.stock.processing.stream;

import com.university.spark.stock.processing.repository.StockStatusRepository;
import com.university.spark.stock.processing.repository.StockStatusRepositoryImpl;
import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.domain.StockStatus;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
        .mapValues(StockMarketDStream::initializeStockStatus)
        .reduceByKeyAndWindow(StockMarketDStream::calculateStockStatus, Durations.seconds(10))
        .foreachRDD(rdd -> rdd.collect()
            .forEach(record -> stockStatusRepository.send(record._1, record._2))
        );
  }

  private static StockStatus initializeStockStatus(Stock stock) {
    BigDecimal exchange = stock.getExchange();
    return StockStatus.builder()
        .recentQuota(stock)
        .minExchange(exchange)
        .maxExchange(exchange)
        .diffExchange(exchange)
        .build();
  }

  private static StockStatus calculateStockStatus(StockStatus previousStockStatus, StockStatus currentStockStatus) {
    BigDecimal updatedExchange = Optional.ofNullable(currentStockStatus.getRecentQuota())
        .map(Stock::getExchange)
        .orElse(BigDecimal.ZERO);

    Stock previousStock = previousStockStatus.getRecentQuota();

    BigDecimal diff = Optional.ofNullable(previousStock)
        .map(Stock::getExchange)
        .map(previousExchange -> previousExchange.subtract(updatedExchange))
        .orElse(updatedExchange);

    BigDecimal minExchange = Optional.ofNullable(previousStock)
        .map(Stock::getExchange)
        .filter(previousExchange -> previousExchange.compareTo(updatedExchange) < 0)
        .orElse(updatedExchange);

    BigDecimal maxExchange = Optional.ofNullable(previousStock)
        .map(Stock::getExchange)
        .filter(previousExchange -> previousExchange.compareTo(updatedExchange) > 0)
        .orElse(updatedExchange);

    StockStatus stockStatus = StockStatus.builder()
        .recentQuota(currentStockStatus.getRecentQuota())
        .diffExchange(diff)
        .minExchange(minExchange)
        .maxExchange(maxExchange)
        .build();

    logger.debug("Updating stock statistic: {}", stockStatus.toString());

    return stockStatus;
  }
}

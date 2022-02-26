package com.university.spark.stock.processing;

import com.university.spark.stock.processing.config.SparkKafkaConfig;
import com.university.spark.stock.processing.repository.StockStatusRepository;
import com.university.spark.stock.processing.repository.StockStatusRepositoryImpl;
import com.university.stock.model.domain.Stock;
import com.university.stock.model.domain.StockStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

@Slf4j
public class StockMarketDStream {

  private static final String INPUT_TOPIC = "stock_test";
  private static final String OUTPUT_TOPIC = "stock_status_test";

  public static void main(String[] args) throws InterruptedException {

    Logger.getLogger("org.apache").setLevel(Level.WARN);
    Logger.getLogger("org.apache.spark.storage").setLevel(Level.ERROR);

    SparkConf conf = new SparkConf().setAppName("stockMarketDStreamApp").setMaster("local[*]");

    JavaStreamingContext sc = new JavaStreamingContext(conf, Durations.seconds(1));

    JavaInputDStream<ConsumerRecord<String, Stock>> stream = SparkKafkaConfig.createDStream(sc,
        List.of(INPUT_TOPIC));

    StockStatusRepository stockStatusRepository = new StockStatusRepositoryImpl(OUTPUT_TOPIC,
        SparkKafkaConfig.createKafkaProducerProperties());

    stream.filter(item -> Objects.nonNull(item.value()))
        .mapToPair(item -> new Tuple2<>(item.value().getTicker(), item.value()))
        .mapValues(StockMarketDStream::initializeStockStatus)
        .reduceByKeyAndWindow(StockMarketDStream::calculateStockStatus, Durations.seconds(10))
        .foreachRDD(rdd -> {
          rdd.collect().forEach(record -> stockStatusRepository.send(record._1, record._2));
        });

    sc.start();
    sc.awaitTermination();
  }

  private static StockStatus initializeStockStatus(Stock stock) {
    return StockStatus.builder()
        .recentQuota(stock)
        .minExchange(BigDecimal.valueOf(Long.MAX_VALUE))
        .maxExchange(BigDecimal.valueOf(Long.MIN_VALUE))
        .diffExchange(BigDecimal.ZERO)
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

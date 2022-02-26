package com.university.spark.stock.processing;

import com.university.spark.stock.processing.config.SparkKafkaConfig;
import com.university.stock.model.domain.Stock;
import com.university.stock.model.domain.StockStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import scala.Tuple2;

@Slf4j
public class StockMarketDStream {

  public static void main(String[] args) throws InterruptedException {

    Logger.getLogger("org.apache").setLevel(Level.WARN);
    Logger.getLogger("org.apache.spark.storage").setLevel(Level.ERROR);

    SparkConf conf = new SparkConf().setAppName("stockMarketDStreamApp").setMaster("local[*]");

    JavaStreamingContext sc = new JavaStreamingContext(conf, Durations.seconds(1));

    List<String> topics = List.of("stock_test");

    Map<String, Object> params = SparkKafkaConfig.configureKafkaParams();

    JavaInputDStream<ConsumerRecord<String, Stock>> stream = KafkaUtils.createDirectStream(sc,
        LocationStrategies.PreferConsistent(),
        ConsumerStrategies.Subscribe(topics, params));

    stream.filter(item -> Objects.nonNull(item.value()))
        .mapToPair(item -> new Tuple2<>(item.value().getTicker(), item.value()))
        .mapValues(StockMarketDStream::initializeStockStatus)
        .reduceByKeyAndWindow(StockMarketDStream::calculateStockStatus, Durations.seconds(10))
        .foreachRDD(rdd -> {
          rdd.foreach(record -> {
            //TODO: send to kafka
          });
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

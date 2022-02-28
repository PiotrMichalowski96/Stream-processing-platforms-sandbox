package com.university.spark.stock.processing;

import com.university.spark.stock.processing.config.SparkKafkaConfigRetriever;
import com.university.spark.stock.processing.stream.StockMarketDStream;
import java.util.Map;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

@Slf4j
public class SparkStockProcessingApplication {

  private static final SparkKafkaConfigRetriever CONFIG_RETRIEVER = new SparkKafkaConfigRetriever("application.properties");

  private static final String INPUT_TOPIC = CONFIG_RETRIEVER.getInputTopic();
  private static final String OUTPUT_TOPIC = CONFIG_RETRIEVER.getOutputTopic();

  private static final Map<String, Object> KAFKA_PARAMS = CONFIG_RETRIEVER.configureKafkaParams();
  private static final Properties KAFKA_PRODUCER_PROPERTIES = CONFIG_RETRIEVER.createKafkaProducerProperties();

  public static void main(String[] args) throws InterruptedException {

    Logger.getLogger("org.apache").setLevel(Level.WARN);
    Logger.getLogger("org.apache.spark.storage").setLevel(Level.ERROR);

    SparkConf conf = new SparkConf().setAppName("sparkStockProcessingApplication").setMaster("local[*]");

    JavaStreamingContext sc = new JavaStreamingContext(conf, Durations.seconds(1));

    StockMarketDStream stockMarketDStream = new StockMarketDStream(sc, INPUT_TOPIC, OUTPUT_TOPIC, KAFKA_PRODUCER_PROPERTIES);

    stockMarketDStream.stockStream(KAFKA_PARAMS);

    sc.start();
    sc.awaitTermination();
  }
}

package com.university.spark.stock.processing;

import com.university.spark.stock.processing.config.SparkKafkaConfig;
import com.university.spark.stock.processing.stream.StockMarketDStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

@Slf4j
public class SparkStockProcessingApplication {

  private static final String INPUT_TOPIC = SparkKafkaConfig.getInputTopic();
  private static final String OUTPUT_TOPIC = SparkKafkaConfig.getOutputTopic();

  public static void main(String[] args) throws InterruptedException {

    Logger.getLogger("org.apache").setLevel(Level.WARN);
    Logger.getLogger("org.apache.spark.storage").setLevel(Level.ERROR);

    SparkConf conf = new SparkConf().setAppName("sparkStockProcessingApplication").setMaster("local[*]");

    JavaStreamingContext sc = new JavaStreamingContext(conf, Durations.seconds(1));

    StockMarketDStream stockMarketDStream = new StockMarketDStream(sc, INPUT_TOPIC, OUTPUT_TOPIC);

    stockMarketDStream.stockStream();

    sc.start();
    sc.awaitTermination();
  }
}

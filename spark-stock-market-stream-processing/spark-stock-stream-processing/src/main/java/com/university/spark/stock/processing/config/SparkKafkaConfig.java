package com.university.spark.stock.processing.config;

import static java.util.Map.entry;

import com.university.spark.stock.processing.kafka.StockDeserializer;
import com.university.stock.model.domain.Stock;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import lombok.experimental.UtilityClass;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

@UtilityClass
public class SparkKafkaConfig {

  private static Configuration config;

  static {
    Configurations configs = new Configurations();
    try {
      config = configs.properties(new File("application.properties"));
    } catch (ConfigurationException e) {
      e.printStackTrace();
    }
  }

  public static JavaInputDStream<ConsumerRecord<String, Stock>> createDStream(JavaStreamingContext sc,
      List<String> topics) {

    return KafkaUtils.createDirectStream(sc,
        LocationStrategies.PreferConsistent(),
        ConsumerStrategies.Subscribe(topics, configureKafkaParams()));
  }

  private static Map<String, Object> configureKafkaParams() {
    return Map.ofEntries(
        entry("bootstrap.servers", config.getString("kafka.bootstrapAddress")),
        entry("key.deserializer", StringDeserializer.class),
        entry("value.deserializer", StockDeserializer.class),
        entry("group.id", "spark-group"),
        entry("auto.offset.reset", "latest"),
        entry("enable.auto.commit", true)
    );
  }

  public static Properties createKafkaProducerProperties() {
    Properties properties = new Properties();
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
    properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
    properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
    properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
    return properties;
  }
}

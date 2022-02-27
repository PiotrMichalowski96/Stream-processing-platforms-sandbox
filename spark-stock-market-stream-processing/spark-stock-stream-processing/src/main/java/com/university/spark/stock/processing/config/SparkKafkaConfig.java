package com.university.spark.stock.processing.config;

import static java.util.Map.entry;

import com.university.spark.stock.processing.kafka.StockDeserializer;
import java.io.File;
import java.util.Map;
import java.util.Properties;
import lombok.experimental.UtilityClass;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

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

  public static String getInputTopic() {
    return config.getString("topic.input");
  }

  public static String getOutputTopic() {
    return config.getString("topic.output");
  }

  public static Map<String, Object> configureKafkaParams() {
    return Map.ofEntries(
        entry("bootstrap.servers", config.getString("kafka.bootstrapAddress")),
        entry("key.deserializer", StringDeserializer.class),
        entry("value.deserializer", StockDeserializer.class),
        entry("group.id", config.getString("kafka.stream.group.id")),
        entry("auto.offset.reset", config.getString("kafka.stream.auto.offset.reset")),
        entry("enable.auto.commit", config.getBoolean("kafka.stream.enable.auto.commit"))
    );
  }

  public static Properties createKafkaProducerProperties() {
    Properties properties = new Properties();
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getString("kafka.bootstrapAddress"));
    properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, config.getString("kafka.producer.enable.idempotence"));
    properties.setProperty(ProducerConfig.ACKS_CONFIG, config.getString("kafka.producer.acks"));
    properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
    properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, config.getString("kafka.producer.max.requests.connection"));
    return properties;
  }
}

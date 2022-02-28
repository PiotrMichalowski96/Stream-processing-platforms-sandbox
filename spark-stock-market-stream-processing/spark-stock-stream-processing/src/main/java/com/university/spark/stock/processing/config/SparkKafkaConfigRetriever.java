package com.university.spark.stock.processing.config;

import com.university.spark.stock.processing.kafka.StockDeserializer;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

@Slf4j
@Getter
public class SparkKafkaConfigRetriever {

  private final Configuration config;

  public SparkKafkaConfigRetriever(String applicationPropertiesName) {
    Configurations configs = new Configurations();
    try {
      this.config = configs.properties(new File(applicationPropertiesName));
    } catch (ConfigurationException e) {
      logger.error("Config file cannot be loaded", e);
      throw new RuntimeException("Cannot create SparkKafkaConfigRetriever");
    }
  }

  public String getInputTopic() {
    return config.getString("topic.input");
  }

  public String getOutputTopic() {
    return config.getString("topic.output");
  }

  public Map<String, Object> configureKafkaParams() {
    Map<String, Object> properties = new HashMap<>();
    properties.put("bootstrap.servers", config.getString("kafka.bootstrapAddress"));
    properties.put("key.deserializer", StringDeserializer.class);
    properties.put("value.deserializer", StockDeserializer.class);
    properties.put("group.id", config.getString("kafka.stream.group.id"));
    properties.put("auto.offset.reset", config.getString("kafka.stream.auto.offset.reset"));
    properties.put("enable.auto.commit", config.getBoolean("kafka.stream.enable.auto.commit"));
    return properties;
  }

  public Properties createKafkaProducerProperties() {
    Properties properties = new Properties();
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getString("kafka.bootstrapAddress"));
    properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, config.getString("kafka.producer.enable.idempotence"));
    properties.setProperty(ProducerConfig.ACKS_CONFIG, config.getString("kafka.producer.acks"));
    properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
    properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, config.getString("kafka.producer.max.requests.connection"));
    return properties;
  }
}

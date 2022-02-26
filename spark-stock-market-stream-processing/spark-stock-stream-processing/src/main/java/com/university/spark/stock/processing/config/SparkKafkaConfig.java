package com.university.spark.stock.processing.config;

import com.university.spark.stock.processing.kafka.StockDeserializer;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.apache.kafka.common.serialization.StringDeserializer;

@UtilityClass
public class SparkKafkaConfig {

  public static Map<String, Object> configureKafkaParams() {
    Map<String, Object> params = new HashMap<>();
    params.put("bootstrap.servers", "localhost:9092");
    params.put("key.deserializer", StringDeserializer.class);
    params.put("value.deserializer", StockDeserializer.class);
    params.put("group.id", "spark-group");
    params.put("auto.offset.reset", "latest");
    params.put("enable.auto.commit", true);
    return params;
  }
}

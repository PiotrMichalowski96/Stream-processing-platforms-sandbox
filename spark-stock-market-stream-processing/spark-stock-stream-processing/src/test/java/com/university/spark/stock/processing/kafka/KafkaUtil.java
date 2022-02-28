package com.university.spark.stock.processing.kafka;

import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.domain.StockStatus;
import com.university.stock.market.model.kafka.StockMarketDeserializer;
import com.university.stock.market.model.kafka.StockMarketSerializer;
import java.util.Properties;
import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

@UtilityClass
public class KafkaUtil {

  public static KafkaProducer<String, Stock> createProducer(String bootstrapServer) {
    Properties properties = new Properties();
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
    properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
    properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
    properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
    properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
    final Serializer<String> stringSerializer = new StringSerializer();
    final Serializer<Stock> stockSerializer = new StockMarketSerializer<>();
    return new KafkaProducer<>(properties, stringSerializer, stockSerializer);
  }

  public static KafkaConsumer<String, StockStatus> createConsumer(String bootstrapServer) {
    Properties properties = new Properties();
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "groupId");
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");
    final Deserializer<String> stringDeserializer = new StringDeserializer();
    final Deserializer<StockStatus> stockStatusDeserializer = new StockMarketDeserializer<>(StockStatus.class);
    return new KafkaConsumer<>(properties, stringDeserializer, stockStatusDeserializer);
  }
}

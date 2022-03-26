package com.university.stock.market.model.kafka;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

@UtilityClass
public class KafkaUtil {

  public static <T> KafkaProducer<String, T> createProducer(String bootstrapServer, Serializer<T> payloadSerializer) {
    Properties properties = new Properties();
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
    properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
    properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
    properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
    properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
    final Serializer<String> stringSerializer = new StringSerializer();
    return new KafkaProducer<>(properties, stringSerializer, payloadSerializer);
  }

  public static <T> KafkaConsumer<String, T> createConsumer(String bootstrapServer, Deserializer<T> payloadDeserializer) {
    Properties properties = new Properties();
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "groupId");
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");
    final Deserializer<String> stringDeserializer = new StringDeserializer();
    return new KafkaConsumer<>(properties, stringDeserializer, payloadDeserializer);
  }

  public static <T> void createAndSendRecord(KafkaProducer<String, T> producer, String inputTopic, T recordPayload) {
    String key = RandomStringUtils.randomAlphanumeric(10);
    ProducerRecord<String, T> record = new ProducerRecord<>(inputTopic, key, recordPayload);
    producer.send(record);
  }

  public static <T> Map<String, T> readKeyStockStatusToMap(KafkaConsumer<String, T> consumer) {
    Map<String, T> keyMessageMap = new HashMap<>();
    ConsumerRecords<String, T> records = consumer.poll(Duration.ofMillis(100));
    records.forEach(record -> keyMessageMap.put(record.key(), record.value()));
    return keyMessageMap;
  }
}

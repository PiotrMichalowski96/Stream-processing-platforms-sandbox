package com.university.spark.stock.processing.repository;

import com.university.stock.market.model.domain.StockStatus;
import com.university.stock.market.model.kafka.StockMarketSerializer;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;

@Slf4j
public class StockStatusRepositoryImpl implements StockStatusRepository {

  private final String topic;
  private final KafkaProducer<String, StockStatus> producer;

  public StockStatusRepositoryImpl(String topic, Properties producerProperties) {
    Serializer<String> stringSerializer = new StringSerializer();
    Serializer<StockStatus> stockStatusSerializer = new StockMarketSerializer<>();
    this.producer = new KafkaProducer<>(producerProperties, stringSerializer, stockStatusSerializer);
    this.topic = topic;
  }

  @Override
  public void send(String key, StockStatus stockStatus) {

    ProducerRecord<String, StockStatus> record = new ProducerRecord<>(topic, key, stockStatus);

    producer.send(record, (recordMetadata, exception) -> {
      if (exception != null) {
        logger.error("Error during sending", exception);
      }
    });
  }
}

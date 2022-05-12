package com.university.stock.market.consumer.config;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.university.stock.market.consumer.kafka.KafkaConsumerConfig;
import com.university.stock.market.consumer.kafka.KafkaMessageListener;
import com.university.stock.market.consumer.kinesis.AwsKinesisConsumerConfig;
import com.university.stock.market.consumer.kinesis.KinesisMessageListener;
import com.university.stock.market.consumer.mapper.MessageMapper;
import com.university.stock.market.consumer.repository.MessageProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({KafkaConsumerConfig.class, AwsKinesisConsumerConfig.class})
public class StockConsumerConfig<T> {

  @Bean
  @ConditionalOnProperty(value = "consumer.kafka.enable", havingValue = "true")
  public KafkaMessageListener<T> kafkaMessageListener(MessageProducer<T> messageProducer,
      MessageMapper<T> messageMapper) {
    return new KafkaMessageListener<>(messageProducer, messageMapper);
  }

  @Bean
  @ConditionalOnProperty(value = "consumer.kinesis.enable", havingValue = "true")
  public KinesisMessageListener<T> kinesisMessageListener(
      @Value("${consumer.kinesis.aws.stream}") String streamName,
      AmazonKinesis kinesis,
      MessageProducer<T> messageProducer,
      MessageMapper<T> messageMapper) {

    return new KinesisMessageListener<>(streamName, kinesis, messageProducer, messageMapper);
  }
}

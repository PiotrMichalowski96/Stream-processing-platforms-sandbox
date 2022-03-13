package com.university.stock.producer.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
@ConditionalOnProperty(value = "producer.kafka.enable", havingValue = "true")
public class KafkaTopicConfig {

  @Value("${producer.kafka.bootstrapAddress}")
  private String bootstrapAddress;

  @Value("${producer.kafka.topic}")
  private String topic;

  @Bean
  public KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    return new KafkaAdmin(configs);
  }

  @Bean
  public NewTopic stockTopic() {
    return TopicBuilder.name(topic)
        .partitions(3)
        .replicas(1)
        .build();
  }
}

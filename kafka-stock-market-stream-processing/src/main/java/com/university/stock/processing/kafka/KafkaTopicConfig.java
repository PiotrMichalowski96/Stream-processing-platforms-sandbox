package com.university.stock.processing.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {

  @Value("${kafka.bootstrapAddress}")
  private String bootstrapAddress;

  @Value("${kafka.topic.statistic}")
  private String stockStatisticTopic;

  @Value("${kafka.topic.intermediary}")
  private String intermediaryTopic;

  @Bean
  public KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    return new KafkaAdmin(configs);
  }

  @Bean
  public NewTopic statisticTopic() {
    return TopicBuilder.name(stockStatisticTopic)
        .partitions(3)
        .replicas(1)
        .compact()
        .build();
  }

  @Bean
  public NewTopic intermediaryTopic() {
    return TopicBuilder.name(intermediaryTopic)
        .partitions(1)
        .replicas(1)
        .build();
  }
}

package com.university.stock.producer.kafka;

import com.university.stock.market.model.domain.Stock;
import com.university.stock.producer.repository.StockMarketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(value = "producer.kafka.enable", havingValue = "true")
public class KafkaStockRepositoryImpl implements StockMarketRepository {

  private final KafkaTemplate<String, Stock> kafkaTemplate;
  private final NewTopic topic;

  @Override
  public void send(Stock stock) {
    String topicName = topic.name();
    logger.debug("sending stock={} to topic={}", stock.toString(), topicName);
    kafkaTemplate.send(topicName, stock);
  }
}

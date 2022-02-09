package com.university.stock.producer.domain.stock;

import com.university.stock.model.domain.Stock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
//@Component
public class StockMarketRepositoryImpl implements StockMarketRepository {

  private final KafkaTemplate<String, Stock> kafkaTemplate;
  private final NewTopic topic;

  @Override
  public void send(Stock stock) {
    String topicName = topic.name();
    logger.debug("sending stock={} to topic={}", stock.toString(), topicName);
    kafkaTemplate.send(topicName, stock);
  }
}

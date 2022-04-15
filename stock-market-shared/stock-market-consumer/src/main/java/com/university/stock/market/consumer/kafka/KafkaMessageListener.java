package com.university.stock.market.consumer.kafka;

import com.university.stock.market.common.util.JsonUtil;
import com.university.stock.market.consumer.mapper.MessageMapper;
import com.university.stock.market.consumer.repository.MessageProducer;
import com.university.stock.market.model.domain.StockStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@RequiredArgsConstructor
public class KafkaMessageListener<T> {

  private final MessageProducer<T> messageProducer;
  private final MessageMapper<T> messageMapper;

  @KafkaListener(topics = "${consumer.kafka.topic}", containerFactory = "kafkaListenerContainerFactory")
  public void stockStatusListener(String stockStatusJson) {

    StockStatus stockStatus = JsonUtil.convertToObjectFrom(StockStatus.class, stockStatusJson);
    T stockElasticMessage = messageMapper.toMessage(stockStatus, stockStatusJson);

    logger.debug("Message: {}", stockElasticMessage.toString());
    messageProducer.sendMessage(stockElasticMessage);
  }
}

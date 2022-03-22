package com.university.elasticsearch.stock.consumer.kafka;

import com.university.elasticsearch.stock.consumer.mapper.ElasticMessageMapper;
import com.university.elasticsearch.stock.consumer.model.StockElasticMessage;
import com.university.elasticsearch.stock.consumer.service.ElasticProducer;
import com.university.elasticsearch.stock.consumer.service.MessageListener;
import com.university.stock.market.common.util.JsonUtil;
import com.university.stock.market.model.domain.StockStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "consumer.kafka.enable", havingValue = "true")
public class KafkaMessageListener implements MessageListener {

  private final ElasticProducer elasticProducer;
  private final ElasticMessageMapper elasticMessageMapper;

  @KafkaListener(topics = "${consumer.kafka.topic}", containerFactory = "kafkaListenerContainerFactory")
  @Override
  public void stockStatusListener(String stockStatusJson) {

    StockStatus stockStatus = JsonUtil.convertToObjectFrom(StockStatus.class, stockStatusJson);
    StockElasticMessage stockElasticMessage = elasticMessageMapper.toStockElasticMessage(stockStatus, stockStatusJson);

    logger.debug("Elastic message: {}", stockElasticMessage.toString());
    elasticProducer.sendMessage(stockElasticMessage);
  }
}

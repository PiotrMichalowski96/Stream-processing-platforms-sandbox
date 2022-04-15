package com.university.elasticsearch.stock.consumer.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.university.elasticsearch.stock.consumer.model.StockElasticMessage;
import com.university.stock.market.consumer.repository.MessageProducer;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticProducer implements MessageProducer<StockElasticMessage> {

  @Value("${elasticsearch.index}")
  private final String indexName;
  private final ElasticsearchClient client;

  @Override
  public void sendMessage(StockElasticMessage elasticMessage) {
    try {
      client.index(builder -> builder
          .index(indexName)
          .id(elasticMessage.getId())
          .document(elasticMessage)
      );
    } catch (IOException e) {
      e.printStackTrace();
      logger.error("Cannot send message document to ELK cluster.", e);
    }
  }
}

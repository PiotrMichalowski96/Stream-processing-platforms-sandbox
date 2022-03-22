package com.university.elasticsearch.stock.consumer.service;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import com.university.elasticsearch.stock.consumer.model.StockElasticMessage;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ElasticProducerTest {

  private static final String INDEX_NAME = "exampleIndexName";

  @Mock
  private ElasticsearchClient client;

  private ElasticProducer elasticProducer;

  @BeforeEach
  void init() {
    elasticProducer = new ElasticProducer(INDEX_NAME, client);
  }

  @Test
  void shouldSendMessageToElasticsearch() throws IOException {
    //given
    StockElasticMessage elasticMessage = StockElasticMessage.builder()
        .id("id")
        .streamPlatform("KAFKA_STREAMS")
        .processingTimeInMillis(100L)
        .experimentCase("experimentCase")
        .comment("comment")
        .message("message")
        .build();

    //when
    elasticProducer.sendMessage(elasticMessage);

    //then
    verify(client, times(1)).index(any(IndexRequest.class));
  }
}
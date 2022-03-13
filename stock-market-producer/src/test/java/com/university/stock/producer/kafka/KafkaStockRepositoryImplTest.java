package com.university.stock.producer.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.university.stock.market.model.domain.Stock;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class KafkaStockRepositoryImplTest {

  @Mock
  private KafkaTemplate<String, Stock> kafkaTemplate;

  @Mock
  private NewTopic topic;

  @Captor
  private ArgumentCaptor<Stock> stockCaptor;

  @InjectMocks
  private KafkaStockRepositoryImpl stockMarketRepository;

  @Test
  void shouldSendStockToKafka() {
    //given
    String topicName = "topic-kafka";

    Stock stock = Stock.builder()
        .ticker("example-ticker")
        .build();

    //when
    when(topic.name()).thenReturn(topicName);

    stockMarketRepository.send(stock);

    //then
    verify(kafkaTemplate).send(eq(topicName), stockCaptor.capture());
    assertThat(stockCaptor.getValue()).isEqualTo(stock);
  }
}
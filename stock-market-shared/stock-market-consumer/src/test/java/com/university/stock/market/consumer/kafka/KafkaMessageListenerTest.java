package com.university.stock.market.consumer.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.university.stock.market.consumer.mapper.MessageMapper;
import com.university.stock.market.consumer.repository.MessageProducer;
import com.university.stock.market.consumer.util.Message;
import com.university.stock.market.model.domain.StockStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KafkaMessageListenerTest {

  @InjectMocks
  private KafkaMessageListener<Message> kafkaMessageListener;
  @Mock
  private MessageProducer<Message> messageProducer;
  @Mock
  private MessageMapper<Message> messageMapper;
  @Captor
  private ArgumentCaptor<Message> messageCaptor;


  @Test
  void shouldSendStockElasticMessageToKibana() {
    //given
    String stockStatusJson = "{\n"
        + "  \"recentQuota\": {\n"
        + "    \"ticker\": \"USD/JPY\",\n"
        + "    \"type\": \"Physical Currency\",\n"
        + "    \"exchange\": \"NASDAQ\",\n"
        + "    \"price\": 115.2300,\n"
        + "    \"currency\": \"Japanese Yen\",\n"
        + "    \"volume\": 900,\n"
        + "    \"timestamp\": \"2022-02-17 22:24:32\",\n"
        + "    \"inputMetadataDetails\": {\n"
        + "      \"experimentCase\": \"Unknown size of stream\",\n"
        + "      \"description\": \"Real random stream of data from online API\"\n"
        + "    }\n"
        + "  },\n"
        + "  \"maxPrice\": 115.2300,\n"
        + "  \"minPrice\": 114.0230,\n"
        + "  \"diffPrice\": 0.4010,\n"
        + "  \"tradeAction\": \"SELL\",\n"
        + "  \"resultMetadataDetails\": {\n"
        + "    \"streamProcessing\": \"KAFKA_STREAMS\",\n"
        + "    \"processingTimeInMillis\": 100\n"
        + "  }\n"
        + "}";

    Message expectedElasticMessage = Message.builder()
        .id("865609043")
        .timestamp(LocalDateTime.now())
        .message(stockStatusJson)
        .build();

    when(messageMapper.toMessage(any(StockStatus.class), eq(stockStatusJson))).thenReturn(expectedElasticMessage);

    //when
    kafkaMessageListener.stockStatusListener(stockStatusJson);

    //then
    verify(messageProducer).sendMessage(messageCaptor.capture());

    Message actualElasticMessage = messageCaptor.getValue();
    assertThat(actualElasticMessage).usingRecursiveComparison().isEqualTo(expectedElasticMessage);
  }
}
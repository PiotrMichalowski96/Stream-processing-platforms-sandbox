package com.university.elasticsearch.stock.consumer.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.university.elasticsearch.stock.consumer.mapper.ElasticMessageMapper;
import com.university.elasticsearch.stock.consumer.mapper.ElasticMessageMapperImpl;
import com.university.elasticsearch.stock.consumer.model.StockElasticMessage;
import com.university.elasticsearch.stock.consumer.service.ElasticProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KafkaMessageListenerTest {

  @InjectMocks
  private KafkaMessageListener kafkaMessageListener;
  @Mock
  private ElasticProducer elasticProducer;
  @Spy
  private ElasticMessageMapper elasticMessageMapper = new ElasticMessageMapperImpl();
  @Captor
  private ArgumentCaptor<StockElasticMessage> messageCaptor;


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

    StockElasticMessage expectedElasticMessage = StockElasticMessage.builder()
        .id("-865609043")
        .streamPlatform("KAFKA_STREAMS")
        .processingTimeInMillis(100L)
        .experimentCase("Unknown size of stream")
        .comment("Real random stream of data from online API")
        .message(stockStatusJson)
        .build();

    //when
    kafkaMessageListener.stockStatusListener(stockStatusJson);

    //then
    verify(elasticProducer).sendMessage(messageCaptor.capture());
    StockElasticMessage actualElasticMessage = messageCaptor.getValue();
    assertThat(actualElasticMessage).usingRecursiveComparison()
        .ignoringFields("timestamp")
        .isEqualTo(expectedElasticMessage);
  }
}
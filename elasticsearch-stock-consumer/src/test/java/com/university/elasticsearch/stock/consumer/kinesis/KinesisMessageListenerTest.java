package com.university.elasticsearch.stock.consumer.kinesis;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.GetRecordsRequest;
import com.amazonaws.services.kinesis.model.GetRecordsResult;
import com.amazonaws.services.kinesis.model.GetShardIteratorRequest;
import com.amazonaws.services.kinesis.model.GetShardIteratorResult;
import com.amazonaws.services.kinesis.model.Record;
import com.university.elasticsearch.stock.consumer.mapper.ElasticMessageMapper;
import com.university.elasticsearch.stock.consumer.mapper.ElasticMessageMapperImpl;
import com.university.elasticsearch.stock.consumer.model.StockElasticMessage;
import com.university.elasticsearch.stock.consumer.service.ElasticProducer;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KinesisMessageListenerTest {

  @Mock
  private ElasticProducer elasticProducer;
  @Mock
  private AmazonKinesis amazonKinesis;
  @Spy
  private ElasticMessageMapper elasticMessageMapper = new ElasticMessageMapperImpl();
  @Mock
  private GetShardIteratorResult shardIterator;
  @Captor
  private ArgumentCaptor<StockElasticMessage> messageCaptor;

  private KinesisMessageListener kinesisMessageListener;

  @BeforeEach
  void init() {
    when(amazonKinesis.getShardIterator(any(GetShardIteratorRequest.class))).thenReturn(shardIterator);
    kinesisMessageListener = new KinesisMessageListener("streamName", amazonKinesis, elasticProducer, elasticMessageMapper);
  }

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
        + "    \"streamProcessing\": \"AWS_KINESIS_LAMBDA\",\n"
        + "    \"processingTimeInMillis\": 100\n"
        + "  }\n"
        + "}";

    List<Record> recordList = List.of(createRecordFrom(stockStatusJson));

    GetRecordsResult mockRecordsRequest = mock(GetRecordsResult.class);
    when(mockRecordsRequest.getRecords())
        .thenReturn(recordList)
        .thenReturn(recordList)
        .thenReturn(Collections.emptyList());
    when(amazonKinesis.getRecords(any(GetRecordsRequest.class))).thenReturn(mockRecordsRequest);

    StockElasticMessage expectedElasticMessage = StockElasticMessage.builder()
        .id("1574981253")
        .streamPlatform("AWS_KINESIS_LAMBDA")
        .processingTimeInMillis(100L)
        .experimentCase("Unknown size of stream")
        .comment("Real random stream of data from online API")
        .message(stockStatusJson)
        .build();

    //when
    kinesisMessageListener.stockStatusListener();

    //then
    verify(elasticProducer).sendMessage(messageCaptor.capture());
    StockElasticMessage actualElasticMessage = messageCaptor.getValue();
    assertThat(actualElasticMessage).usingRecursiveComparison()
        .ignoringFields("timestamp")
        .isEqualTo(expectedElasticMessage);
  }

  private Record createRecordFrom(String json) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(json.getBytes());
    Record record = new Record();
    record.setData(byteBuffer);
    return record;
  }
}
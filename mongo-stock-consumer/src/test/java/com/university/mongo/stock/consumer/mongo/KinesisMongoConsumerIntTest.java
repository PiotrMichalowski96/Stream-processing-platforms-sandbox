package com.university.mongo.stock.consumer.mongo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.GetRecordsRequest;
import com.amazonaws.services.kinesis.model.GetRecordsResult;
import com.amazonaws.services.kinesis.model.GetShardIteratorRequest;
import com.amazonaws.services.kinesis.model.GetShardIteratorResult;
import com.amazonaws.services.kinesis.model.Record;
import com.university.mongo.stock.consumer.entity.StockStatusDoc;
import com.university.mongo.stock.consumer.mapper.StockStatusDocMapper;
import com.university.mongo.stock.consumer.service.StockStatusDocProducer;
import com.university.stock.market.consumer.kinesis.KinesisMessageListener;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class KinesisMongoConsumerIntTest extends AbstractMongoConsumerIntTest{

  @MockBean
  private AmazonKinesis amazonKinesis;
  @MockBean
  private GetShardIteratorResult shardIterator;
  @Autowired
  private StockStatusDocMapper mapper;
  @Autowired
  private StockStatusDocProducer producer;

  private KinesisMessageListener<StockStatusDoc> kinesisMessageListener;

  @BeforeEach
  void init() {
    when(amazonKinesis.getShardIterator(any(GetShardIteratorRequest.class))).thenReturn(shardIterator);
    kinesisMessageListener = new KinesisMessageListener<>("streamName", amazonKinesis, producer, mapper);
  }

  @Test
  void shouldSaveStockDocumentOnMongoDB() {
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
    mockReadingRecordsFromKinesis(recordList);

    StockStatusDoc expectedDocument = StockStatusDoc.builder()
        .streamPlatform("AWS_KINESIS_LAMBDA")
        .processingTimeInMillis(100L)
        .experimentCase("Unknown size of stream")
        .comment("Real random stream of data from online API")
        .message(stockStatusJson)
        .build();

    //when
    kinesisMessageListener.stockStatusListener();

    //then
    assertDocumentSavedInMongoDB(expectedDocument);
  }

  private void mockReadingRecordsFromKinesis(List<Record> recordList) {
    GetRecordsResult mockRecordsRequest = mock(GetRecordsResult.class);
    when(mockRecordsRequest.getRecords())
        .thenReturn(recordList)
        .thenReturn(recordList)
        .thenReturn(Collections.emptyList());
    when(amazonKinesis.getRecords(any(GetRecordsRequest.class))).thenReturn(mockRecordsRequest);
  }

  private Record createRecordFrom(String json) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(json.getBytes());
    Record record = new Record();
    record.setData(byteBuffer);
    return record;
  }
}
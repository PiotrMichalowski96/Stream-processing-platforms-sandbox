package com.university.mongo.stock.consumer.mongo;

import com.university.mongo.stock.consumer.entity.StockStatusDoc;
import com.university.stock.market.consumer.kafka.KafkaMessageListener;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class KafkaMongoConsumerIntTest extends AbstractMongoConsumerIntTest {

  @Autowired
  private KafkaMessageListener<StockStatusDoc> kafkaMessageListener;

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
        + "    \"streamProcessing\": \"KAFKA_STREAMS\",\n"
        + "    \"processingTimeInMillis\": 100\n"
        + "  }\n"
        + "}";

    StockStatusDoc expectedDocument = StockStatusDoc.builder()
        .streamPlatform("KAFKA_STREAMS")
        .processingTimeInMillis(100L)
        .experimentCase("Unknown size of stream")
        .comment("Real random stream of data from online API")
        .message(stockStatusJson)
        .build();

    //when
    kafkaMessageListener.stockStatusListener(stockStatusJson);

    //then
    assertDocumentSavedInMongoDB(expectedDocument);
  }
}
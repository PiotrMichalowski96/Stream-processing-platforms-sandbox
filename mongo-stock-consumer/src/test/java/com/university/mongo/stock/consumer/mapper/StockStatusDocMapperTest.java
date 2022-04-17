package com.university.mongo.stock.consumer.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.university.mongo.stock.consumer.entity.StockStatusDoc;
import com.university.stock.market.model.domain.InputMetadataDetails;
import com.university.stock.market.model.domain.ResultMetadataDetails;
import com.university.stock.market.model.domain.ResultMetadataDetails.StreamProcessing;
import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.domain.StockStatus;
import org.junit.jupiter.api.Test;

class StockStatusDocMapperTest {

  private final StockStatusDocMapper mapper = new StockStatusDocMapperImpl();

  @Test
  void shouldMapStockStatusToDocument() {
    //given
    Stock stock = Stock.builder()
        .inputMetadataDetails(new InputMetadataDetails("experiment case", "description"))
        .build();

    StockStatus stockStatus = StockStatus.builder()
        .recentQuota(stock)
        .resultMetadataDetails(new ResultMetadataDetails(StreamProcessing.KAFKA_STREAMS, 100L))
        .build();

    String exampleJson = "{recentQuota: {}}";

    StockStatusDoc expectedStockStatusDoc = StockStatusDoc.builder()
        .streamPlatform("KAFKA_STREAMS")
        .processingTimeInMillis(100L)
        .experimentCase("experiment case")
        .comment("description")
        .message(exampleJson)
        .build();

    //when
    StockStatusDoc actualStockStatusDoc = mapper.toMessage(stockStatus, exampleJson);

    //then
    assertThat(actualStockStatusDoc).usingRecursiveComparison()
        .ignoringFields("timestamp")
        .isEqualTo(expectedStockStatusDoc);
  }
}
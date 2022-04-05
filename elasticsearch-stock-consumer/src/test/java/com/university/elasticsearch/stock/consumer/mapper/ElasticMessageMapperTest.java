package com.university.elasticsearch.stock.consumer.mapper;


import static org.assertj.core.api.Assertions.assertThat;

import com.university.elasticsearch.stock.consumer.model.StockElasticMessage;
import com.university.stock.market.model.domain.InputMetadataDetails;
import com.university.stock.market.model.domain.ResultMetadataDetails;
import com.university.stock.market.model.domain.ResultMetadataDetails.StreamProcessing;
import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.domain.StockStatus;
import org.junit.jupiter.api.Test;

class ElasticMessageMapperTest {

  private final ElasticMessageMapper mapper = new ElasticMessageMapperImpl();

  @Test
  void shouldMapStockStatusToElasticMessage() {
    //given
    Stock stock = Stock.builder()
        .inputMetadataDetails(new InputMetadataDetails("experiment case", "description"))
        .build();

    StockStatus stockStatus = StockStatus.builder()
        .recentQuota(stock)
        .resultMetadataDetails(new ResultMetadataDetails(StreamProcessing.KAFKA_STREAMS, 100L))
        .build();

    String exampleJson = "{recentQuota: {}}";
    String hashId = String.valueOf(Math.abs(exampleJson.hashCode()));

    StockElasticMessage expectedElasticMessage = StockElasticMessage.builder()
        .id(hashId)
        .streamPlatform("KAFKA_STREAMS")
        .processingTimeInMillis(100L)
        .experimentCase("experiment case")
        .comment("description")
        .message(exampleJson)
        .build();

    //when
    StockElasticMessage actualElasticMessage = mapper.toStockElasticMessage(stockStatus, exampleJson);

    //then
    assertThat(actualElasticMessage).usingRecursiveComparison()
        .ignoringFields("timestamp")
        .isEqualTo(expectedElasticMessage);
  }
}
package com.university.stock.market.model.util;


import static org.assertj.core.api.Assertions.assertThat;

import com.university.stock.market.model.domain.InputMetadataDetails;
import com.university.stock.market.model.domain.ResultMetadataDetails;
import com.university.stock.market.model.domain.ResultMetadataDetails.StreamProcessing;
import com.university.stock.market.model.domain.Stock;
import org.junit.jupiter.api.Test;

class ResultMetadataDetailsUtilTest {

  @Test
  void shouldCreateResultMetadataDetails() {
    //given
    InputMetadataDetails inputMetadataDetails = InputMetadataDetails.builder()
        .experimentCase("Unknown size of stream")
        .description("Real random stream of data from online API")
        .build();

    Stock stock = Stock.builder()
        .ticker("BTC/USD")
        .inputMetadataDetails(inputMetadataDetails)
        .build();

    ResultMetadataDetails expectedResultMetadata = ResultMetadataDetails.builder()
        .streamProcessing(StreamProcessing.KAFKA_STREAMS)
        .build();

    //when
    ResultMetadataDetails actualResultMetadata = ResultMetadataDetailsUtil.createMetadataDetails(StreamProcessing.KAFKA_STREAMS, stock);

    //then
    assertThat(actualResultMetadata).usingRecursiveComparison()
        .ignoringFields("processingTimeInMillis")
        .isEqualTo(expectedResultMetadata);
  }
}
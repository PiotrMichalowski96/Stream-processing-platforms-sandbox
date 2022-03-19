package com.university.kinesis.lambda.stock.stream.processing.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.university.stock.market.model.domain.InputMetadataDetails;
import com.university.stock.market.model.domain.ResultMetadataDetails;
import com.university.stock.market.model.domain.Stock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;
import org.junit.jupiter.api.Test;

class MetadataDetailsUtilTest {

  @Test
  void shouldCreateMetadataWhenDifferentZoneTime() {
    //given
    ZoneId polandZoneId = ZoneId.of("Europe/Warsaw");
    TimeZone.setDefault(TimeZone.getTimeZone(polandZoneId));

    Stock stock = Stock.builder()
        .timestamp(LocalDateTime.now())
        .inputMetadataDetails(new InputMetadataDetails("Unknown size of stream", "Real random stream of data from online API"))
        .build();

    ZoneId customAwsZoneId = ZoneId.of("US/Eastern");
    TimeZone.setDefault(TimeZone.getTimeZone(customAwsZoneId));

    //when
    ResultMetadataDetails resultMetadataDetails = MetadataDetailsUtil.createResultMetadataDetails(stock);

    //then
    assertThat(resultMetadataDetails.getProcessingTimeInMillis()).isLessThan(1000);
  }
}
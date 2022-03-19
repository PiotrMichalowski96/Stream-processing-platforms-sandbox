package com.university.kinesis.lambda.stock.stream.processing.util;

import com.university.stock.market.model.domain.ResultMetadataDetails;
import com.university.stock.market.model.domain.ResultMetadataDetails.StreamProcessing;
import com.university.stock.market.model.domain.Stock;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MetadataDetailsUtil {

  public static ResultMetadataDetails createResultMetadataDetails(Stock stock) {
    ZoneId polandZoneId = ZoneId.of("Europe/Warsaw");
    long streamProcessingTime = Optional.ofNullable(stock.getTimestamp())
        .map(localTime -> ZonedDateTime.of(localTime, polandZoneId))
        .map(inputDataTime -> Duration.between(inputDataTime, ZonedDateTime.now()))
        .map(Duration::toMillis)
        .orElse(0L);
    return ResultMetadataDetails.builder()
        .streamProcessing(StreamProcessing.AWS_KINESIS_LAMBDA)
        .processingTimeInMillis(streamProcessingTime)
        .build();
  }
}

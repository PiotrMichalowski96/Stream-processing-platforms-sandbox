package com.university.stock.market.model.util;

import com.university.stock.market.model.domain.ResultMetadataDetails;
import com.university.stock.market.model.domain.ResultMetadataDetails.StreamProcessing;
import com.university.stock.market.model.domain.Stock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ResultMetadataDetailsUtil {

  public static ResultMetadataDetails createMetadataDetails(StreamProcessing streamProcessing, Stock stock) {
    long streamProcessingTime = Optional.ofNullable(stock.getTimestamp())
        .map(inputDataTime -> Duration.between(inputDataTime, LocalDateTime.now()))
        .map(Duration::toMillis)
        .orElse(0L);
    return ResultMetadataDetails.builder()
        .streamProcessing(streamProcessing)
        .processingTimeInMillis(streamProcessingTime)
        .build();
  }
}

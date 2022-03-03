package com.university.stock.producer.mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import org.mapstruct.MapperConfig;

@MapperConfig
public interface MapperMethods {

  @TimestampMapper
  default LocalDateTime convertTimestamp(Long timestamp) {
    if (timestamp == null) {
      return LocalDateTime.now();
    }
    Instant unixTimestamp = Instant.ofEpochSecond(timestamp);
    return LocalDateTime.ofInstant(unixTimestamp, TimeZone.getDefault().toZoneId());
  }
}

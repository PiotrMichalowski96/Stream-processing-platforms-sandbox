package com.university.elasticsearch.stock.consumer.mapper;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.MapperConfig;

@MapperConfig
public interface MapperMethods {

  @HashIdMapper
  default String createHashId(String text) {
    if(StringUtils.isBlank(text)) {
      return RandomStringUtils.randomAlphanumeric(30);
    }
    int hashCode = Math.abs(text.hashCode());
    return String.valueOf(hashCode);
  }
}

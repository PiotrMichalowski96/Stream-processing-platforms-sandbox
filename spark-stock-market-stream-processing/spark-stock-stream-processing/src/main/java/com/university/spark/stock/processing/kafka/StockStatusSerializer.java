package com.university.spark.stock.processing.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.university.stock.model.domain.StockStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;

@Slf4j
public class StockStatusSerializer implements Serializer<StockStatus> {

  @Override
  public byte[] serialize(String topic, StockStatus data) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    try {
      return mapper.writeValueAsBytes(data);
    } catch (JsonProcessingException e) {
      logger.error("Couldn't serialize json of StockStatus entity");
      return null;
    }
  }
}

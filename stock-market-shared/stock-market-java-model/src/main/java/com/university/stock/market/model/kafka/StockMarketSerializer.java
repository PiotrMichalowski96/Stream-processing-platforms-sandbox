package com.university.stock.market.model.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.Serializer;

public class StockMarketSerializer<T> implements Serializer<T> {

  @Override
  public byte[] serialize(String topic, T data) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    try {
      return mapper.writeValueAsBytes(data);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }
  }
}

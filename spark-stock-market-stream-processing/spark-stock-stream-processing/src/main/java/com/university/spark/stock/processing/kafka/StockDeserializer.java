package com.university.spark.stock.processing.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.university.stock.model.domain.Stock;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

@Slf4j
@RequiredArgsConstructor
public class StockDeserializer implements Deserializer<Stock> {

  @Override
  public Stock deserialize(String topic, byte[] data) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    try {
      return mapper.readValue(data, Stock.class);
    } catch (IOException e) {
      logger.error("Couldn't deserialize json of stock entity");
      return null;
    }
  }
}

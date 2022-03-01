package com.university.spark.stock.processing.kafka;

import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.kafka.StockMarketDeserializer;

public class StockDeserializer extends StockMarketDeserializer<Stock> {

  public StockDeserializer() {
    super(Stock.class);
  }
}

package com.university.stock.producer.domain.stock;

import com.university.stock.model.domain.Stock;

public interface StockMarketRepository {
  void send(Stock stock);
}
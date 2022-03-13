package com.university.stock.producer.repository;

import com.university.stock.market.model.domain.Stock;

public interface StockMarketRepository {
  void send(Stock stock);
}

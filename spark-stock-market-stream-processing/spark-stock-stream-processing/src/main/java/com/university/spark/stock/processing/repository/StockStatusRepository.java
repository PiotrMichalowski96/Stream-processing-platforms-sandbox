package com.university.spark.stock.processing.repository;

import com.university.stock.market.model.domain.StockStatus;

public interface StockStatusRepository {
  void send(String key, StockStatus stockStatus);
}

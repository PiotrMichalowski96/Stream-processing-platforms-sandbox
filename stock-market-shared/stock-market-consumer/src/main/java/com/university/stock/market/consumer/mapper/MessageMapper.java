package com.university.stock.market.consumer.mapper;

import com.university.stock.market.model.domain.StockStatus;

public interface MessageMapper<T> {
  T toMessage(StockStatus stockStatus, String json);
}

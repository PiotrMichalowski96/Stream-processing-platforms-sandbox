package com.university.stock.market.consumer.repository;

public interface MessageProducer<T> {
  void sendMessage(T message);
}

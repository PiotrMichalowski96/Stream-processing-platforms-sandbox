package com.university.elasticsearch.stock.consumer.service;

public interface MessageListener {
  void stockStatusListener(String stockStatusJson);
}

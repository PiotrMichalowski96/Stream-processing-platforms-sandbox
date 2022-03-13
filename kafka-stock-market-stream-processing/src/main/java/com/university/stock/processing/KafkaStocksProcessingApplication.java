package com.university.stock.processing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaStocksProcessingApplication {

  public static void main(String[] args) {
    SpringApplication.run(KafkaStocksProcessingApplication.class, args);
  }
}
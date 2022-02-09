package com.university.stock.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KafkaStockProducerApplication {

  public static void main(String[] args) {
    SpringApplication.run(KafkaStockProducerApplication.class, args);
  }
}
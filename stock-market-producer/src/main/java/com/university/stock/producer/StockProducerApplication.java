package com.university.stock.producer;

import com.university.stock.producer.supplier.StockMarketProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@RequiredArgsConstructor
@EnableScheduling
@SpringBootApplication
public class StockProducerApplication implements CommandLineRunner {

  private final StockMarketProducer stockMarketProducer;

  public static void main(String[] args) {
    SpringApplication.run(StockProducerApplication.class, args);
  }

  @Override
  public void run(String... args) {
    stockMarketProducer.startSendingStocksProcess();
  }
}
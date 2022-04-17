package com.university.mongo.stock.consumer;

import com.university.stock.market.consumer.config.StockConsumerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(StockConsumerConfig.class)
public class MongoStockConsumerApplication {

  public static void main(String[] args) {
    SpringApplication.run(MongoStockConsumerApplication.class, args);
  }

}

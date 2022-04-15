package com.university.elasticsearch.stock.consumer;

import com.university.stock.market.consumer.config.StockConsumerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(StockConsumerConfig.class)
public class ElasticsearchStockConsumerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ElasticsearchStockConsumerApplication.class, args);
  }

}

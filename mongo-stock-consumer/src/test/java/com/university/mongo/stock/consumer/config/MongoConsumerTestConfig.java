package com.university.mongo.stock.consumer.config;

import com.university.stock.market.consumer.config.StockConsumerConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("com.university.mongo.stock.consumer")
@Import(StockConsumerConfig.class)
public class MongoConsumerTestConfig {
}

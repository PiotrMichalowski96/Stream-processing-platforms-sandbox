package com.university.kinesis.lambda.stock.stream.processing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KinesisLambdaStockStreamProcessingApplication {

  public static void main(String[] args) {
    SpringApplication.run(KinesisLambdaStockStreamProcessingApplication.class, args);
  }

}

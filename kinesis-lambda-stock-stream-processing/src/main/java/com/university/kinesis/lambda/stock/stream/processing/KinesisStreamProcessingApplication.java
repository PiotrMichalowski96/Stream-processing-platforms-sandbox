package com.university.kinesis.lambda.stock.stream.processing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KinesisStreamProcessingApplication {

  public static void main(String[] args) {
    SpringApplication.run(KinesisStreamProcessingApplication.class, args);
  }

}

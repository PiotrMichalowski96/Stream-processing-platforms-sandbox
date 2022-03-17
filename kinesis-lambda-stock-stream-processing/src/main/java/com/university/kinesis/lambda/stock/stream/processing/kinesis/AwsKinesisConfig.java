package com.university.kinesis.lambda.stock.stream.processing.kinesis;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsKinesisConfig {

  @Value("${kinesis.aws.region}")
  private String region;

  @Bean
  public AmazonKinesis buildAmazonKinesis() {
    return AmazonKinesisClientBuilder.standard()
        .withCredentials(new DefaultAWSCredentialsProviderChain())
        .withRegion(region)
        .build();
  }
}

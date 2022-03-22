package com.university.elasticsearch.stock.consumer.kinesis;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(value = "consumer.kinesis.enable", havingValue = "true")
public class AwsKinesisConsumerConfig {

  @Value("${consumer.kinesis.aws.access.key}")
  private String accessKey;

  @Value("${consumer.kinesis.aws.secret.key}")
  private String secretKey;

  @Value("${consumer.kinesis.aws.region}")
  private String region;

  @Bean
  public AmazonKinesis buildAmazonKinesis() {
    BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
    return AmazonKinesisClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
        .withRegion(region)
        .build();
  }
}

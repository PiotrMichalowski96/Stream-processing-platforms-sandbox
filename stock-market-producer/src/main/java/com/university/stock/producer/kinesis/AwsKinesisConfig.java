package com.university.stock.producer.kinesis;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "producer.kinesis.enable", havingValue = "true")
public class AwsKinesisConfig {

  @Value("${producer.kinesis.aws.access.key}")
  private String accessKey;

  @Value("${producer.kinesis.aws.secret.key}")
  private String secretKey;

  @Value("${producer.kinesis.aws.region}")
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

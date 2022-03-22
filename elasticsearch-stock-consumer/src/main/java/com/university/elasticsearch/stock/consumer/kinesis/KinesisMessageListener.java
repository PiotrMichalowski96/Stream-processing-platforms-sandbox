package com.university.elasticsearch.stock.consumer.kinesis;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.GetRecordsRequest;
import com.amazonaws.services.kinesis.model.GetRecordsResult;
import com.amazonaws.services.kinesis.model.GetShardIteratorRequest;
import com.amazonaws.services.kinesis.model.GetShardIteratorResult;
import com.amazonaws.services.kinesis.model.ShardIteratorType;
import com.university.elasticsearch.stock.consumer.mapper.ElasticMessageMapper;
import com.university.elasticsearch.stock.consumer.service.ElasticProducer;
import com.university.stock.market.common.util.JsonUtil;
import com.university.stock.market.model.domain.StockStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(value = "consumer.kinesis.enable", havingValue = "true")
public class KinesisMessageListener {

  private static final String SHARD_ID = "shardId-000000000000";
  private static final int REQUEST_LIMIT = 25;

  private final AmazonKinesis kinesis;
  private final ElasticProducer elasticProducer;
  private final ElasticMessageMapper elasticMessageMapper;
  private final GetShardIteratorResult shardIterator;

  public KinesisMessageListener(@Value("${consumer.kinesis.aws.stream}") String streamName,
      AmazonKinesis kinesis,
      ElasticProducer elasticProducer,
      ElasticMessageMapper elasticMessageMapper) {

    this.kinesis = kinesis;
    this.elasticProducer = elasticProducer;
    this.elasticMessageMapper = elasticMessageMapper;

    GetShardIteratorRequest readShardsRequest = new GetShardIteratorRequest()
        .withStreamName(streamName)
        .withShardId(SHARD_ID)
        .withShardIteratorType(ShardIteratorType.LATEST);

    this.shardIterator = this.kinesis.getShardIterator(readShardsRequest);
  }

  @Scheduled(fixedRateString = "${consumer.kinesis.schedule:1000}")
  public void stockStatusListener() {

    GetRecordsRequest recordsRequest = new GetRecordsRequest()
        .withShardIterator(shardIterator.getShardIterator())
        .withLimit(REQUEST_LIMIT);

    GetRecordsResult recordsResult = kinesis.getRecords(recordsRequest);

    while (!recordsResult.getRecords().isEmpty()) {

      recordsResult.getRecords().stream()
          .map(record -> new String(record.getData().array()))
          .map(stockStatusJson -> {
            StockStatus stockStatus = JsonUtil.convertToObjectFrom(StockStatus.class, stockStatusJson);
            return elasticMessageMapper.toStockElasticMessage(stockStatus, stockStatusJson);
          })
          .peek(elasticMessage -> logger.debug("Elastic message: {}", elasticMessage.toString()))
          .forEach(elasticProducer::sendMessage);

      String shardNextIterator = recordsResult.getNextShardIterator();
      this.shardIterator.setShardIterator(shardNextIterator);

      recordsRequest.setShardIterator(shardNextIterator);
      recordsResult = kinesis.getRecords(recordsRequest);
    }
  }
}

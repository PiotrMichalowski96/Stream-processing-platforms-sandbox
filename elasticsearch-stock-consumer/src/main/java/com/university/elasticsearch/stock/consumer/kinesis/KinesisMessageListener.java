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
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "consumer.kinesis.enable", havingValue = "true")
public class KinesisMessageListener {

  private static final String SHARD_ID = "shardId-000000000000";
  private static final int REQUEST_LIMIT = 25;

  @Value("${consumer.kinesis.aws.stream}")
  private final String streamName;
  private final AmazonKinesis kinesis;

  private final ElasticProducer elasticProducer;
  private final ElasticMessageMapper elasticMessageMapper;

  private GetShardIteratorResult shardIterator;

  @PostConstruct
  private void buildShardIterator() {
    GetShardIteratorRequest readShardsRequest = new GetShardIteratorRequest()
        .withStreamName(streamName)
        .withShardId(SHARD_ID)
        .withShardIteratorType(ShardIteratorType.LATEST);

    this.shardIterator = kinesis.getShardIterator(readShardsRequest);
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

package com.university.stock.market.consumer.kinesis;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.GetRecordsRequest;
import com.amazonaws.services.kinesis.model.GetRecordsResult;
import com.amazonaws.services.kinesis.model.GetShardIteratorRequest;
import com.amazonaws.services.kinesis.model.GetShardIteratorResult;
import com.amazonaws.services.kinesis.model.ShardIteratorType;
import com.university.stock.market.common.util.JsonUtil;
import com.university.stock.market.consumer.mapper.MessageMapper;
import com.university.stock.market.consumer.repository.MessageProducer;
import com.university.stock.market.model.domain.StockStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
public class KinesisMessageListener<T> {

  private static final String SHARD_ID = "shardId-000000000000";
  private static final int REQUEST_LIMIT = 25;

  private final AmazonKinesis kinesis;
  private final MessageProducer<T> messageProducer;
  private final MessageMapper<T> messageMapper;
  private final GetShardIteratorResult shardIterator;

  public KinesisMessageListener(@Value("${consumer.kinesis.aws.stream}") String streamName,
      AmazonKinesis kinesis,
      MessageProducer<T> messageProducer,
      MessageMapper<T> messageMapper) {

    this.kinesis = kinesis;
    this.messageProducer = messageProducer;
    this.messageMapper = messageMapper;

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
            return messageMapper.toMessage(stockStatus, stockStatusJson);
          })
          .peek(elasticMessage -> logger.debug("Message: {}", elasticMessage.toString()))
          .forEach(messageProducer::sendMessage);

      String shardNextIterator = recordsResult.getNextShardIterator();
      this.shardIterator.setShardIterator(shardNextIterator);

      recordsRequest.setShardIterator(shardNextIterator);
      recordsResult = kinesis.getRecords(recordsRequest);
    }
  }
}

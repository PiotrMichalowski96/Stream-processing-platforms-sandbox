package com.university.kinesis.lambda.stock.stream.processing.kinesis;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.university.stock.market.common.util.JsonUtil;
import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.domain.StockStatus;
import java.nio.ByteBuffer;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//TODO: similar code as for Kinesis Producer - check if we can extract code

@Slf4j
@RequiredArgsConstructor
@Component
public class KinesisStockStatusRepository {

  @Value("${producer.kinesis.aws.stream}")
  private final String stream;
  private final AmazonKinesis kinesis;

  public void send(StockStatus stockStatus) {

    String tickerKey = Optional.ofNullable(stockStatus.getRecentQuota())
            .map(Stock::getTicker)
            .orElse(RandomStringUtils.randomAlphanumeric(10));

    Optional.ofNullable(JsonUtil.convertToJson(stockStatus))
        .map(json -> {
          logger.debug("Sending stock status: {}", json);
          return json.getBytes();
        }).map(ByteBuffer::wrap)
        .ifPresentOrElse(byteMessage -> this.sendKinesisRecord(tickerKey, byteMessage),
            () -> logger.warn("Message is not send because wrong conversion to JSON"));

  }

  private void sendKinesisRecord(String partitionKey, ByteBuffer byteMessage) {
    PutRecordRequest createRecordRequest = new PutRecordRequest();
    createRecordRequest.setStreamName(stream);
    createRecordRequest.setPartitionKey(partitionKey);
    createRecordRequest.setData(byteMessage);

    kinesis.putRecord(createRecordRequest);
  }
}

package com.university.stock.producer.kinesis;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.university.stock.market.common.util.JsonUtil;
import com.university.stock.market.model.domain.Stock;
import com.university.stock.producer.repository.StockMarketRepository;
import java.nio.ByteBuffer;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(value = "producer.kinesis.enable", havingValue = "true")
public class KinesisStockRepositoryImpl implements StockMarketRepository {

  @Value("${producer.kinesis.aws.stream}")
  private final String stream;
  private final AmazonKinesis kinesis;

  @Override
  public void send(Stock stock) {

    String tickerKey = stock.getTicker();

    Optional.ofNullable(JsonUtil.convertToJson(stock))
        .map(json -> {
          logger.debug("Sending stock: {}", json);
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

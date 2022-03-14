package com.university.kinesis.lambda.stock.stream.processing.kinesis;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.university.stock.market.common.util.JsonUtil;
import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.domain.StockStatus;
import com.university.stock.market.model.domain.StockStatus.TradeAction;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KinesisStockStatusRepositoryTest {

  @Mock
  private AmazonKinesis kinesis;

  @Captor
  private ArgumentCaptor<PutRecordRequest> recordRequestCaptor;

  @InjectMocks
  private KinesisStockStatusRepository stockStatusRepository;

  @Test
  void shouldSendStockStatusToKinesis() {
    //given
    Stock stock = Stock.builder()
        .ticker("example-ticker")
        .build();

    StockStatus stockStatus = StockStatus.builder()
        .recentQuota(stock)
        .minPrice(BigDecimal.ONE)
        .maxPrice(BigDecimal.ONE)
        .diffPrice(BigDecimal.ONE)
        .tradeAction(TradeAction.SELL)
        .build();

    String stockStatusJson = JsonUtil.convertToJson(stockStatus);
    ByteBuffer expectedByteMessage = ByteBuffer.wrap(stockStatusJson.getBytes());

    //when
    stockStatusRepository.send(stockStatus);

    //then
    verify(kinesis).putRecord(recordRequestCaptor.capture());

    ByteBuffer actualByteMessage = recordRequestCaptor.getValue().getData();
    assertThat(actualByteMessage).isEqualTo(expectedByteMessage);
  }
}
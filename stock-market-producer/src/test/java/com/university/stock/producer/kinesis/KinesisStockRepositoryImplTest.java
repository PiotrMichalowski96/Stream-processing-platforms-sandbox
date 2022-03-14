package com.university.stock.producer.kinesis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.university.stock.market.common.util.JsonUtil;
import com.university.stock.market.model.domain.Stock;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KinesisStockRepositoryImplTest {

  @Mock
  private AmazonKinesis kinesis;

  @Captor
  private ArgumentCaptor<PutRecordRequest> recordRequestCaptor;

  @InjectMocks
  private KinesisStockRepositoryImpl stockMarketRepository;

  @Test
  void shouldSendStockToKinesis() {
    //given
    Stock stock = Stock.builder()
        .ticker("example-ticker")
        .build();

    String stockJson = JsonUtil.convertToJson(stock);
    ByteBuffer expectedByteMessage = ByteBuffer.wrap(stockJson.getBytes());

    //when
    stockMarketRepository.send(stock);

    //then
    verify(kinesis).putRecord(recordRequestCaptor.capture());

    ByteBuffer actualByteMessage = recordRequestCaptor.getValue().getData();
    assertThat(actualByteMessage).isEqualTo(expectedByteMessage);
  }
}
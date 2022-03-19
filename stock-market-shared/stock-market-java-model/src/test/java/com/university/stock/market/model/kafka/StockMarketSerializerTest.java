package com.university.stock.market.model.kafka;


import static org.assertj.core.api.Assertions.assertThat;

import com.university.stock.market.model.domain.InputMetadataDetails;
import com.university.stock.market.model.domain.ResultMetadataDetails;
import com.university.stock.market.model.domain.ResultMetadataDetails.StreamProcessing;
import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.domain.StockStatus;
import com.university.stock.market.model.domain.StockStatus.TradeAction;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class StockMarketSerializerTest {

  private final StockMarketSerializer<StockStatus> serializer = new StockMarketSerializer<>();

  @Test
  void shouldSerializeStockStatus() {
    //given
    Stock stock = Stock.builder()
        .ticker("ABC")
        .type("Type")
        .exchange("The Exchange")
        .price(100.0)
        .currency("USD")
        .volume(BigInteger.valueOf(123L))
        .timestamp(LocalDateTime.of(2022, 3, 13, 20, 20, 20))
        .inputMetadataDetails(new InputMetadataDetails("Unknown size of stream", "Real random stream of data from online API"))
        .build();

    StockStatus stockStatus = StockStatus.builder()
        .recentQuota(stock)
        .maxPrice(BigDecimal.ZERO)
        .minPrice(BigDecimal.ZERO)
        .diffPrice(BigDecimal.ZERO)
        .tradeAction(TradeAction.BUY)
        .resultMetadataDetails(new ResultMetadataDetails(StreamProcessing.SPARK, 100L))
        .build();

    //when
    byte[] serializeMessage = serializer.serialize("topic", stockStatus);

    //then
    assertThat(serializeMessage).isNotNull();
  }
}
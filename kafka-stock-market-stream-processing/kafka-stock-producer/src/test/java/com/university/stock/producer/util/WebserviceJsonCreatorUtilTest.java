package com.university.stock.producer.util;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class WebserviceJsonCreatorUtilTest {

  @Test
  void shouldCreateSubscriptionJson() {
    //given
    List<String> symbols = List.of("AAPL", "INFY", "TRP", "QQQ", "IXIC", "EUR/USD", "USD/JPY");

    String expectedJson = "{\"action\":\"subscribe\",\"params\":{\"symbols\":\"AAPL,INFY,TRP,QQQ,IXIC,EUR/USD,USD/JPY\"}}";

    //when
    String actualJson = WebserviceJsonCreatorUtil.createSubscriptionJson(symbols);

    //then
    assertThat(actualJson).isEqualTo(expectedJson);
  }
}
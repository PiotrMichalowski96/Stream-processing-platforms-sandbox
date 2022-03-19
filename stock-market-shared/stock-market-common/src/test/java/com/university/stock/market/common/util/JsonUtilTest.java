package com.university.stock.market.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.university.stock.market.model.domain.InputMetadataDetails;
import com.university.stock.market.model.domain.Stock;
import com.university.stock.market.model.dto.QuoteDTO;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class JsonUtilTest {

  @Test
  void shouldExtractStockFromJsonFile() {
    //given
    String jsonFilePath = "src/test/resources/stock.json";

    Stock expectedStock = Stock.builder()
        .ticker("WPQLENRN")
        .type("Common Stock")
        .exchange("NASDAQ")
        .price(13532.4590)
        .currency("USD")
        .volume(BigInteger.valueOf(100))
        .timestamp(LocalDateTime.of(2022, 2, 11, 20, 49, 48))
        .inputMetadataDetails(new InputMetadataDetails("Unknown size of stream", "Real random stream of data from online API"))
        .build();

    //when
    Stock actualStock = JsonUtil.extractFromJson(Stock.class, jsonFilePath);

    //then
    assertThat(actualStock).usingRecursiveComparison().isEqualTo(expectedStock);
  }

  @Test
  void shouldConvertObjectToJsonString() {
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

    String expectedJson = "{\"ticker\":\"ABC\",\"type\":\"Type\",\"exchange\":\"The Exchange\",\"price\":100.0000,\"currency\":\"USD\",\"volume\":123,\"timestamp\":\"2022-03-13 20:20:20\",\"inputMetadataDetails\":{\"experimentCase\":\"Unknown size of stream\",\"description\":\"Real random stream of data from online API\"}}";
    //when
    String actualJson = JsonUtil.convertToJson(stock);

    //then
    assertThat(actualJson).isEqualTo(expectedJson);
  }

  @ParameterizedTest
  @MethodSource("provideJson")
  void shouldConvertJsonToQuoteObject(String json, QuoteDTO expectedQuote) {
    //given input
    //when
    QuoteDTO actualQuote = JsonUtil.convertToObjectFrom(QuoteDTO.class, json);

    //then
    assertThat(actualQuote).usingRecursiveComparison().isEqualTo(expectedQuote);
  }

  private static Stream<Arguments> provideJson() {
    String json1 = "{\"event\":\"price\",\"symbol\":\"AAPL\",\"currency\":\"USD\",\"exchange\":\"NASDAQ\",\"type\":\"Common Stock\",\"timestamp\":1646330534,\"price\":166.9400,\"day_volume\":42399195}";

    QuoteDTO expectedQuote1 = QuoteDTO.builder()
        .event("price")
        .symbol("AAPL")
        .currency("USD")
        .exchange("NASDAQ")
        .type("Common Stock")
        .timestamp(1646330534L)
        .price(166.9400)
        .dayVolume(42399195L)
        .build();

    String json2 = "{\"event\":\"price\",\"symbol\":\"EUR/USD\",\"currency_base\":\"Euro\",\"currency_quote\":\"US Dollar\",\"type\":\"Physical Currency\",\"timestamp\":1646332905,\"price\":1.1057}\n";

    QuoteDTO expectedQuote2 = QuoteDTO.builder()
        .event("price")
        .symbol("EUR/USD")
        .currency("US Dollar")
        .type("Physical Currency")
        .timestamp(1646332905L)
        .price(1.1057)
        .build();

    return Stream.of(
        Arguments.of(json1, expectedQuote1),
        Arguments.of(json2, expectedQuote2)
    );
  }
}
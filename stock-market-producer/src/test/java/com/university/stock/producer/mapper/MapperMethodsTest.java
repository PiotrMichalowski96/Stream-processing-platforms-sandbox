package com.university.stock.producer.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class MapperMethodsTest {

  private final MapperMethods mapperMethods = new MapperMethods() {};

  @Test
  void shouldConvertUnixTimestampToLocalDate() {
    //given
    long unixTimestamp = 1646169815;
    LocalDateTime expectedDateTime = LocalDateTime.of(2022, 3, 1, 22, 23, 35);

    //when
    LocalDateTime actualDateTime = mapperMethods.convertTimestamp(unixTimestamp);

    //then
    assertThat(actualDateTime).isEqualTo(expectedDateTime);
  }
}
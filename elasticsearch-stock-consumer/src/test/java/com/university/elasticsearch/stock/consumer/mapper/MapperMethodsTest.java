package com.university.elasticsearch.stock.consumer.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class MapperMethodsTest {

  MapperMethods mapperMethods = new MapperMethods() {};

  @Test
  void shouldCreateHashId() {
    //given
    String exampleText = RandomStringUtils.randomAlphabetic(50);
    String expectedHashId = String.valueOf(Math.abs(exampleText.hashCode()));

    //when
    String actualHashId = mapperMethods.createHashId(exampleText);

    //then
    assertThat(actualHashId).isEqualTo(expectedHashId);
  }

  @NullAndEmptySource
  @ParameterizedTest
  void shouldCreateHashIdWhenEmptyText(String exampleText) {
    //given input
    //when
    String actualHashId = mapperMethods.createHashId(exampleText);

    //then
    assertThat(actualHashId).isNotNull();
  }
}
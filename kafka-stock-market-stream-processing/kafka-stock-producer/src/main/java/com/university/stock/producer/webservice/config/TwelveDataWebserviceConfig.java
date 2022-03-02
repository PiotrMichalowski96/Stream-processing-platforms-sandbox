package com.university.stock.producer.webservice.config;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

@Configuration
@ConditionalOnProperty(value = "twelvedata.webservice.enable", havingValue = "true")
@RequiredArgsConstructor
@Getter
public class TwelveDataWebserviceConfig {

  private static final String API_KEY_HEADER= "X-TD-APIKEY";

  @Value("${twelvedata.webservice.properties.keyPassword}")
  private final String keyPassword;
  @Value("${twelvedata.webservice.properties.url}")
  private final String url;
  @Value("${twelvedata.webservice.properties.subscribeSymbols}")
  private final List<String> subscribeSymbols;

  @Bean
  WebSocketClient webSocketClient() {
  return new ReactorNettyWebSocketClient();
  }

  @Bean
  HttpHeaders httpHeaders() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.set(API_KEY_HEADER, keyPassword);
    return httpHeaders;
  }
}

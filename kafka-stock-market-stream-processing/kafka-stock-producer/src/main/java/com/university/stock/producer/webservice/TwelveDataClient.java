package com.university.stock.producer.webservice;

import com.university.stock.producer.webservice.config.TwelveDataWebserviceConfig;
import java.net.URI;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.Disposable;

@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "twelvedata.webservice.enable", havingValue = "true")
@Component
public class TwelveDataClient {

  @Getter
  private final TwelveDataWebserviceConfig webserviceConfig;
  private final WebSocketClient webSocketClient;
  private final HttpHeaders httpHeaders;

  private Disposable subscription;

  public void start(WebSocketHandler webSocketHandler) {
    String uri = webserviceConfig.getUrl();
    subscription = webSocketClient.execute(URI.create(uri), httpHeaders, webSocketHandler)
        .subscribe();

    logger.debug("Client started.");
  }

  public void stop() {
    if (subscription == null || subscription.isDisposed()) {
      return;
    }
    subscription.dispose();
    logger.debug("Client stopped.");
  }
}

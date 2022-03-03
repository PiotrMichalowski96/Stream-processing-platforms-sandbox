package com.university.stock.producer.supplier;

import com.university.stock.producer.domain.stock.StockMarketRepository;
import com.university.stock.producer.util.JsonCreatorUtil;
import com.university.stock.producer.webservice.TwelveDataClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "twelvedata.webservice.enable", havingValue = "true")
@Service
public class WebserviceStockProducer implements StockMarketProducer {

  private final StockMarketRepository stockMarketRepository;
  private final TwelveDataClient client;

  @Override
  public void startSendingStocksProcess() {
    logger.debug("Setup sending stocks process");

    client.start(this::webSocketHandler);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      logger.debug("Stopping application");
      logger.debug("Shutting down client from TwelveData...");
      client.stop();
      logger.debug("done!");
    }));
  }

  private Mono<Void> webSocketHandler(WebSocketSession session) {
    return sendMessage(session)
        .thenMany(receiveAll(session))
        .then();
  }

  private Mono<Void> sendMessage(WebSocketSession session) {
    List<String> symbols = client.getWebserviceConfig().getSubscribeSymbols();
    String message = JsonCreatorUtil.createSubscriptionJson(symbols);
    return Mono.fromRunnable(() -> logger.debug("Client -> connected id=[{}]", session.getId()))
        .then(session.send(
            Mono.fromCallable(() -> session.textMessage(message)))
        )
        .then(Mono.fromRunnable(
            () -> logger.debug("Client({}) -> sent: [{}]", session.getId(), message))
        );
  }

  private Flux<String> receiveAll(WebSocketSession session) {
    return session .receive()
            .map(WebSocketMessage::getPayloadAsText)
            .doOnNext(textStock -> {
              logger.debug("Client({}) -> received: [{}]", session.getId(), textStock);
              //TODO: convert textStock -> Stock object
              //stockMarketRepository.send(stock);
            });
  }
}
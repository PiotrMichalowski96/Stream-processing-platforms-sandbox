package com.university.stock.producer.webservice.config;

import com.university.stock.market.model.dto.QuoteDTO;
import java.util.function.Predicate;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class TwelveDataWebservicePredicate {

  private static final String SUBSCRIBE_STATUS_EVENT = "subscribe-status";

  public static Predicate<QuoteDTO> nonSubscribeEventPredicate() {
    return quoteDTO -> {
      String event = quoteDTO.getEvent();
      return !StringUtils.equals(SUBSCRIBE_STATUS_EVENT, event);
    };
  }
}

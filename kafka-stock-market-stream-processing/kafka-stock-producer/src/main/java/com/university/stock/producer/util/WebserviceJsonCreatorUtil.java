package com.university.stock.producer.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WebserviceJsonCreatorUtil {

  private static final String SYMBOLS_JSON_FIELD = "symbols";
  private static final String ACTION_JSON_FIELD = "action";
  private static final String ACTION_JSON_VALUE = "subscribe";
  private static final String PARAMS_JSON_FIELD = "params";

  public static String createSubscriptionJson(List<String> symbolsList) {
    String symbols = String.join(",", symbolsList);
    Map<String, String> symbolsMap = Map.of(SYMBOLS_JSON_FIELD, symbols);
    Map<String, Object> map = createJsonWebserviceMap(symbolsMap);
    try {
      return new ObjectMapper().writeValueAsString(map);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Cannot create subscription message to webservice");
    }
  }

  private Map<String, Object> createJsonWebserviceMap(Map<String, String> symbolsMap) {
    Map<String, Object> map = new HashMap<>();
    map.put(ACTION_JSON_FIELD, ACTION_JSON_VALUE);
    map.put(PARAMS_JSON_FIELD, symbolsMap);
    return map;
  }
}

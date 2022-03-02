package com.university.stock.producer.util;

import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonCreatorUtil {

  public static String createSubscriptionJson(List<String> symbolsList) {
    String symbols = String.join(",", symbolsList);
    String jsonMessageFormat = "{\n"
        + "  \"action\": \"subscribe\",\n"
        + "  \"params\": {\n"
        + "\t\"symbols\": \"%s\"\n"
        + "  }\n"
        + "}";
    return String.format(jsonMessageFormat, symbols);
  }
}

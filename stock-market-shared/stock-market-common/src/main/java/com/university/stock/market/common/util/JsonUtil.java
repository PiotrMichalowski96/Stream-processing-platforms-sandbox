package com.university.stock.market.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class JsonUtil {

  public static String readFileAsString(String filePath) {
    try {
      FileInputStream fis = new FileInputStream(filePath);
      return IOUtils.toString(fis, StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static <T> T extractFromJson(Class<T> clazz, String filePath) {
    String jsonText = readFileAsString(filePath);
    if(StringUtils.isBlank(jsonText)) {
      return null;
    }
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(jsonText, clazz);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static <T> T convertToObjectFrom(Class<T> clazz, String json) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      return mapper.readValue(json, clazz);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static <T> String convertToJson(T object) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }
  }
}

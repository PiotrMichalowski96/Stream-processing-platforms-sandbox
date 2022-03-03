package com.university.stock.market.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;

@UtilityClass
public class JsonUtil {

  private String readFileAsString(String filePath) throws IOException {
    FileInputStream fis = new FileInputStream(filePath);
    return IOUtils.toString(fis, StandardCharsets.UTF_8);
  }

  public static <T> T extractFromJson(Class<T> clazz, String filePath) {
    try {
      String jsonText = readFileAsString(filePath);
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
      return mapper.readValue(json, clazz);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }
  }
}

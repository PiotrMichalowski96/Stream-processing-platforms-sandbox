package com.university.stock.market.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSyntaxException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;

@UtilityClass
public class JsonUtil {

  private static final DateTimeFormatter DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private String readFileAsString(String filePath) throws IOException {
    FileInputStream fis = new FileInputStream(filePath);
    return IOUtils.toString(fis, StandardCharsets.UTF_8);
  }

  public static <T> T extractFromJson(Class<T> clazz, String filePath) {
    try {
      String jsonText = readFileAsString(filePath);
      Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
          (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> LocalDateTime.parse(json.getAsString(), DATE_TIME_PATTERN))
          .create();
      return gson.fromJson(jsonText, clazz);
    } catch (IOException | JsonSyntaxException e) {
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

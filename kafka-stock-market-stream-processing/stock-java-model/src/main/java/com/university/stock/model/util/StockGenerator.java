package com.university.stock.model.util;

import com.university.stock.model.domain.Sector;
import com.university.stock.model.domain.Stock;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

@UtilityClass
public class StockGenerator {

  private static final MathContext MATH_CONTEXT = new MathContext(4);

  public static List<Stock> generateRandomStockList(int uniqueStocks) {
    return Stream.generate(() -> new Stock(RandomStringUtils.randomAlphabetic(8).toUpperCase(),
            randomSector(), RandomUtils.nextDouble(100, 10000),
            Instant.now()))
        .limit(uniqueStocks)
        .collect(Collectors.toList());
  }

  public static void updateRandomExchange(Stock stock) {
    BigDecimal currentExchange = stock.getExchange();
    BigDecimal updatedExchange = currentExchange.multiply(new BigDecimal(RandomUtils.nextDouble(0.01, 1), MATH_CONTEXT));
    stock.setExchange(updatedExchange);
    stock.setInstant(Instant.now());
  }

  private static Sector randomSector() {
    List<Sector> sectorList = List.of(Sector.values());
    int index = RandomUtils.nextInt(0, sectorList.size());
    return sectorList.get(index);
  }
}

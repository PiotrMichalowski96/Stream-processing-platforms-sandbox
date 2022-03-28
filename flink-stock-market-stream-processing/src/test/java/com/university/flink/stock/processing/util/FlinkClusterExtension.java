package com.university.flink.stock.processing.util;

import lombok.Getter;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

@Getter
public class FlinkClusterExtension implements BeforeAllCallback, AfterAllCallback {

  private MiniClusterWithClientResource flinkCluster = new MiniClusterWithClientResource(
      new MiniClusterResourceConfiguration.Builder()
          .setNumberSlotsPerTaskManager(2)
          .setNumberTaskManagers(1)
          .build());

  @Override
  public void afterAll(ExtensionContext extensionContext) throws Exception {
    flinkCluster.after();
  }

  @Override
  public void beforeAll(ExtensionContext extensionContext) throws Exception {
    flinkCluster.before();
  }
}

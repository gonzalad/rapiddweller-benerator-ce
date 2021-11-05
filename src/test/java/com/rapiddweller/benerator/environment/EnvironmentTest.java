/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.environment;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.common.CollectionUtil;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link Environment} class.<br/><br/>
 * Created: 03.11.2021 15:28:45
 * @author Volker Bergmann
 * @since 2.10
 */
public class EnvironmentTest {

  static {
    BeneratorFactory.getInstance();
  }

  @Test
  public void testSetting() {
    Environment e = Environment.parse("test", CollectionUtil.buildMap("id_strategy", "hilo"));
    assertEquals(1, e.getSettings().size());
    assertEquals("hilo", e.getSetting("id_strategy"));
  }

  @Test
  public void testDb() {
    Map<String, String> properties = CollectionUtil.buildMap(
        "my.db.url", "jdbc:url",
        "my.db.driver", "com.the.Driver"
    );
    Environment e = Environment.parse("test", properties);
    assertEquals(1, e.getSystems().size());
    SystemRef system = e.getSystem("my");
    assertTrue(system.isDb());
    assertEquals("jdbc:url", system.getProperty("url"));
    assertEquals("com.the.Driver", system.getProperty("driver"));
  }

}
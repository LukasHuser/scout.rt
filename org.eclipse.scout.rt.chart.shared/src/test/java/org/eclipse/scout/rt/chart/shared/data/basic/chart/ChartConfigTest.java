/*
 * Copyright (c) 2010-2020 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
package org.eclipse.scout.rt.chart.shared.data.basic.chart;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.testing.platform.runner.PlatformTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(PlatformTestRunner.class)
public class ChartConfigTest {

  private static final String TYPE1 = "doughnut";
  private static final String TYPE2 = "pie";

  private static final String LEVEL1 = "level1";
  private static final String LEVEL2 = LEVEL1 + ".level2";
  private static final String LEVEL3 = LEVEL2 + ".level3";

  private static final String INT_PROPERTY = LEVEL3 + ".int";
  private static final int INT_PROPERTY_VALUE1 = 42;
  private static final int INT_PROPERTY_VALUE2 = 43;
  private static final String STRING_PROPERTY = LEVEL3 + ".string";
  private static final String STRING_PROPERTY_VALUE = "lorem ipsum dolor";
  private static final String BOOLEAN_PROPERTY = LEVEL2 + ".boolean";
  private static final boolean BOOLEAN_PROPERTY_VALUE = false;

  private static final String LIST = LEVEL1 + ".list";
  private static final String LIST0 = LIST + "[0]";
  private static final String LIST0_PROPERTY = LIST0 + ".property";
  private static final int LIST0_PROPERTY_VALUE = 1;
  private static final String LIST3 = LIST + "[3]";
  private static final String LIST3_PROPERTY = LIST3 + ".property";
  private static final int LIST3_PROPERTY_VALUE = 8;
  private static final String LIST7 = LIST + "[7]";
  private static final String LIST7_PROPERTY = LIST7 + ".property";
  private static final int LIST7_PROPERTY_VALUE1 = 128;
  private static final int LIST7_PROPERTY_VALUE2 = 129;

  @Test
  public void testWithAndRemove() {
    IChartConfig config = BEANS.get(IChartConfig.class)
        .withType(TYPE1)
        .withAnimated(true)
        .withProperty(INT_PROPERTY, INT_PROPERTY_VALUE1)
        .withProperty(STRING_PROPERTY, STRING_PROPERTY_VALUE)
        .withProperty(BOOLEAN_PROPERTY, BOOLEAN_PROPERTY_VALUE);

    Assert.assertEquals(config.getType(), TYPE1);
    Assert.assertTrue(config.isAnimated());
    Assert.assertEquals(config.getProperty(INT_PROPERTY), INT_PROPERTY_VALUE1);
    Assert.assertEquals(config.getProperty(STRING_PROPERTY), STRING_PROPERTY_VALUE);
    Assert.assertEquals(config.getProperty(BOOLEAN_PROPERTY), BOOLEAN_PROPERTY_VALUE);

    config
        .withType(TYPE2)
        .withProperty(INT_PROPERTY, INT_PROPERTY_VALUE2);

    Assert.assertEquals(config.getType(), TYPE2);
    Assert.assertEquals(config.getProperty(INT_PROPERTY), INT_PROPERTY_VALUE2);

    config.removeProperty(INT_PROPERTY);

    Assert.assertNull(config.getProperty(INT_PROPERTY));
    Assert.assertEquals(config.getProperty(STRING_PROPERTY), STRING_PROPERTY_VALUE);
    Assert.assertEquals(config.getProperty(BOOLEAN_PROPERTY), BOOLEAN_PROPERTY_VALUE);

    config.removeProperty(LEVEL3);

    Assert.assertNull(config.getProperty(INT_PROPERTY));
    Assert.assertNull(config.getProperty(STRING_PROPERTY));
    Assert.assertEquals(config.getProperty(BOOLEAN_PROPERTY), BOOLEAN_PROPERTY_VALUE);
  }

  @Test
  public void testWithAndRemoveList() {
    IChartConfig config = BEANS.get(IChartConfig.class)
        .withProperty(LIST7_PROPERTY, LIST7_PROPERTY_VALUE1);

    Assert.assertNotNull(config.getProperty(LIST));
    Assert.assertNotNull(config.getProperty(LIST0));
    Assert.assertNotNull(config.getProperty(LIST3));
    Assert.assertNull(config.getProperty(LIST3_PROPERTY));
    Assert.assertNotNull(config.getProperty(LIST7));
    Assert.assertEquals(config.getProperty(LIST7_PROPERTY), LIST7_PROPERTY_VALUE1);

    config.withProperty(LIST3_PROPERTY, LIST3_PROPERTY_VALUE);

    Assert.assertEquals(config.getProperty(LIST3_PROPERTY), LIST3_PROPERTY_VALUE);

    config.removeProperty(LIST7_PROPERTY);

    Assert.assertEquals(config.getProperty(LIST3_PROPERTY), LIST3_PROPERTY_VALUE);
    Assert.assertNotNull(config.getProperty(LIST7));
    Assert.assertNull(config.getProperty(LIST7_PROPERTY));

    config.removeProperty(LIST3);

    Assert.assertNotNull(config.getProperty(LIST3));
    Assert.assertNull(config.getProperty(LIST3_PROPERTY));
    Assert.assertNotNull(config.getProperty(LIST7));

    config.removeProperty(LIST);

    Assert.assertNull(config.getProperty(LIST));
    Assert.assertNull(config.getProperty(LIST0));
    Assert.assertNull(config.getProperty(LIST3));
    Assert.assertNull(config.getProperty(LIST7));
  }

  @Test
  public void testAddProperties() {
    IChartConfig source = BEANS.get(IChartConfig.class)
        .withType(TYPE1)
        .withProperty(INT_PROPERTY, INT_PROPERTY_VALUE1)
        .withProperty(STRING_PROPERTY, STRING_PROPERTY_VALUE)
        .withProperty(LIST3_PROPERTY, LIST3_PROPERTY_VALUE)
        .withProperty(LIST7_PROPERTY, LIST7_PROPERTY_VALUE1);

    IChartConfig target = BEANS.get(IChartConfig.class)
        .withType(TYPE2)
        .withProperty(INT_PROPERTY, INT_PROPERTY_VALUE2)
        .withProperty(BOOLEAN_PROPERTY, BOOLEAN_PROPERTY_VALUE)
        .withProperty(LIST0_PROPERTY, LIST0_PROPERTY_VALUE)
        .withProperty(LIST7_PROPERTY, LIST7_PROPERTY_VALUE2);

    target.addProperties(source, false);

    Assert.assertEquals(target.getType(), TYPE2);
    Assert.assertEquals(target.getProperty(INT_PROPERTY), INT_PROPERTY_VALUE2);
    Assert.assertEquals(target.getProperty(STRING_PROPERTY), STRING_PROPERTY_VALUE);
    Assert.assertEquals(target.getProperty(BOOLEAN_PROPERTY), BOOLEAN_PROPERTY_VALUE);
    Assert.assertEquals(target.getProperty(LIST0_PROPERTY), LIST0_PROPERTY_VALUE);
    Assert.assertEquals(target.getProperty(LIST3_PROPERTY), LIST3_PROPERTY_VALUE);
    Assert.assertEquals(target.getProperty(LIST7_PROPERTY), LIST7_PROPERTY_VALUE2);

    target.addProperties(source, true);

    Assert.assertEquals(target.getType(), TYPE1);
    Assert.assertEquals(target.getProperty(INT_PROPERTY), INT_PROPERTY_VALUE1);
    Assert.assertEquals(target.getProperty(LIST7_PROPERTY), LIST7_PROPERTY_VALUE1);
  }
}
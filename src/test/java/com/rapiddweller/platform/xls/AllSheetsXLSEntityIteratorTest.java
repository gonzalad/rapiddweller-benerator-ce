/*
 * (c) Copyright 2006-2020 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from rapiddweller GmbH & Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.rapiddweller.platform.xls;

import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.common.converter.NoOpConverter;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.util.DataUtil;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.DefaultDescriptorProvider;
import com.rapiddweller.model.data.DescriptorProvider;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.PartDescriptor;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests the {@link AllSheetsXLSEntityIterator} class.<br/>
 * <br/>
 * Created at 29.01.2009 11:06:33
 *
 * @author Volker Bergmann
 * @since 0.5.8
 */
public class AllSheetsXLSEntityIteratorTest extends XLSTest {

  private static final String PRODUCT_XLS = "com/rapiddweller/platform/xls/product-singlesheet.ent.xls";
  private static final String IMPORT_XLS = "com/rapiddweller/platform/xls/import-multisheet.ent.xls";
  private static final String PRODUCT_COLUMNS_XLS = "com/rapiddweller/platform/xls/product-columns.ent.xls";

  /**
   * The Context.
   */
  BeneratorContext context;

  /**
   * Sets up.
   */
  @Before
  public void setUp() {
    context = new DefaultBeneratorContext();
  }

  /**
   * Test import all sheets.
   *
   * @throws Exception the exception
   */
  @Test
  public void testImportAllSheets() throws Exception {
    AllSheetsXLSEntityIterator iterator = new AllSheetsXLSEntityIterator(IMPORT_XLS);
    try (iterator) {
      iterator.setContext(context);
      assertProduct(PROD1, DataUtil.nextNotNullData(iterator));
      Entity next = DataUtil.nextNotNullData(iterator);
      assertProduct(PROD2, next);
      assertPerson(PERSON1, DataUtil.nextNotNullData(iterator));
      assertNull(iterator.next(new DataContainer<>()));
    }
  }

  /**
   * Test import predefined entity type.
   *
   * @throws Exception the exception
   */
  @Test
  public void testImportPredefinedEntityType() throws Exception {
    ComplexTypeDescriptor entityDescriptor = new ComplexTypeDescriptor("XYZ", context.getLocalDescriptorProvider());
    AllSheetsXLSEntityIterator iterator = new AllSheetsXLSEntityIterator(IMPORT_XLS, new NoOpConverter<>(), entityDescriptor, false);
    try (iterator) {
      iterator.setContext(context);
      assertXYZ(XYZ11, DataUtil.nextNotNullData(iterator));
      Entity next = DataUtil.nextNotNullData(iterator);
      assertXYZ(XYZ12, next);
      Entity entity = DataUtil.nextNotNullData(iterator);
      assertEquals("XYZ", entity.type());
      assertNull(iterator.next(new DataContainer<>()));
      assertEquals("Alice", entity.get("name"));
    }
  }

  /**
   * Test parse all.
   *
   * @throws Exception the exception
   */
  @Test
  public void testParseAll() throws Exception {
    List<Entity> entities = AllSheetsXLSEntityIterator.parseAll(IMPORT_XLS, null, false);
    assertEquals(3, entities.size());
    assertProduct(PROD1, entities.get(0));
    assertProduct(PROD2, entities.get(1));
    assertPerson(PERSON1, entities.get(2));
  }

  /**
   * Test types.
   *
   * @throws Exception the exception
   */
  @Test
  public void testTypes() throws Exception {
    DescriptorProvider dp = new DefaultDescriptorProvider("test", new DataModel());
    // Create descriptor
    final ComplexTypeDescriptor descriptor = new ComplexTypeDescriptor("Product", dp);
    descriptor.setComponent(new PartDescriptor("ean", dp, "string"));
    SimpleTypeDescriptor priceTypeDescriptor = new SimpleTypeDescriptor("priceType", dp, "big_decimal");
    priceTypeDescriptor.setGranularity("0.01");
    descriptor.setComponent(new PartDescriptor("price", dp, priceTypeDescriptor));
    descriptor.setComponent(new PartDescriptor("date", dp, "date"));
    descriptor.setComponent(new PartDescriptor("available", dp, "boolean"));
    descriptor.setComponent(new PartDescriptor("updated", dp, "timestamp"));
    context.getDataModel().addDescriptorProvider(dp);

    // test import
    AllSheetsXLSEntityIterator iterator = new AllSheetsXLSEntityIterator(PRODUCT_XLS);
    try (iterator) {
      iterator.setContext(context);
      assertProduct(PROD1, DataUtil.nextNotNullData(iterator));
      assertProduct(PROD2, DataUtil.nextNotNullData(iterator));
      assertNull(iterator.next(new DataContainer<>()));
    } finally {
      context.getDataModel().removeDescriptorProvider("test");
    }
  }

  /**
   * Test column iteration.
   *
   * @throws Exception the exception
   */
  @Test
  public void testColumnIteration() throws Exception {
    // test import
    AllSheetsXLSEntityIterator iterator = new AllSheetsXLSEntityIterator(PRODUCT_COLUMNS_XLS);
    try (iterator) {
      iterator.setContext(context);
      iterator.setRowBased(false);
      assertProduct(PROD1, DataUtil.nextNotNullData(iterator));
      assertProduct(PROD2, DataUtil.nextNotNullData(iterator));
      assertNull(iterator.next(new DataContainer<>()));
    }
  }

}

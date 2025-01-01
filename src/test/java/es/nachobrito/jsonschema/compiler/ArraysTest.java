/*
 *    Copyright 2024 Nacho Brito
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package es.nachobrito.jsonschema.compiler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ArraysTest extends CompilerTest {

  @DisplayName("Array field types are treated as native arrays")
  @Test
  void expectArrayTypesHandledProperly()
      throws IOException, ClassNotFoundException, NoSuchFieldException {
    var cls = compileSampleSchemaFromFile("classpath:test-schemas/Arrays.json", "Product");
    assertNotNull(cls);

    assertEquals(Double[].class, cls.getDeclaredField("references").getType());
    assertEquals(String[].class, cls.getDeclaredField("names").getType());
    assertEquals(Object[].class, cls.getDeclaredField("address").getType());

    var itemsCls = cls.getDeclaredField("vegetables").getType().componentType();
    assertEquals(String.class, itemsCls.getDeclaredField("veggieName").getType());
    assertEquals(Boolean.class, itemsCls.getDeclaredField("veggieLike").getType());

  }
}

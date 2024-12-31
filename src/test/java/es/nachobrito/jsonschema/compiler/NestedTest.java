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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class NestedTest extends CompilerTest{

    @DisplayName("Schema files with nested structures produce multiple classes")
    @Test
    void expectNestedStructuresHandledProperly() throws IOException, ClassNotFoundException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var cls = compileSampleSchemaFromFile("classpath:test-schemas/Nested.json", "Product");
        assertNotNull(cls);
        var dimensionsCls = cls.getDeclaredField("dimensions").getType();

        assertEquals(3, dimensionsCls.getDeclaredFields().length);
        assertEquals(Double.class, dimensionsCls.getDeclaredField("length").getType());
        assertEquals(Double.class, dimensionsCls.getDeclaredField("width").getType());
        assertEquals(Double.class, dimensionsCls.getDeclaredField("height").getType());

        var json = """
{
"productId": 999,
"productName": "Product Name",
"price": 9.99,
"dimensions": {
    "length": 7.99,
    "width" : 3.45,
    "height": 4.99
  }
}
""";
        var mapper = createObjectMapper();
        var product = mapper.readValue(json, cls);

        assertEquals(999, cls.getDeclaredMethod("productId").invoke(product));
        assertEquals("Product Name", cls.getDeclaredMethod("productName").invoke(product));
        assertEquals(9.99, cls.getDeclaredMethod("price").invoke(product));

        var dimensions = cls.getDeclaredMethod("dimensions").invoke(product);
        assertEquals(7.99, dimensionsCls.getDeclaredMethod("length").invoke(dimensions));
        assertEquals(3.45, dimensionsCls.getDeclaredMethod("width").invoke(dimensions));
        assertEquals(4.99, dimensionsCls.getDeclaredMethod("height").invoke(dimensions));

    }
}

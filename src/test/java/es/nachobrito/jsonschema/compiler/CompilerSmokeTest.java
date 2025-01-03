/*
 *    Copyright 2025 Nacho Brito
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

import static java.util.Comparator.comparing;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

/** Verifies basic schema compilation, and general cases for toString/hashCode/equals methods. */
public class CompilerSmokeTest extends CompilerTest{


  @Test
  void expectSimpleFileCompiled()
      throws IOException,
          ClassNotFoundException,
          InvocationTargetException,
          InstantiationException,
          IllegalAccessException {
    var cls = compileSampleSchemaFromFile("classpath:test-schemas/Person.json", "Person");
    assertNotNull(cls);
    var fields = cls.getDeclaredFields();
    assertEquals(3, fields.length);
    var constructors = cls.getDeclaredConstructors();
    assertEquals(1, constructors.length);
    var methods = cls.getDeclaredMethods();
    assertEquals(6, methods.length);

    var person = constructors[0].newInstance(47, "Nacho", "Brito");
    assertNotNull(person);

    Arrays.sort(methods, 0, methods.length, comparing(m -> m.getName()));
    /*
    Expected order:
    0: public java.lang.Integer Person.age()
    1: public final boolean Person.equals(java.lang.Object)
    2: public java.lang.String Person.firstName()
    3: public final int Person.hashCode()
    4: public java.lang.String Person.lastName()
    5: public final java.lang.String Person.toString()
     */
    assertEquals(47, methods[0].invoke(person));
    assertEquals("Nacho", methods[2].invoke(person));
    assertEquals("Brito", methods[4].invoke(person));

    // System.out.printf("Person created %s%n", person);
  }
  @Test
  void expectSimpleStringCompiled()
      throws IOException,
          ClassNotFoundException,
          InvocationTargetException,
          InstantiationException,
          IllegalAccessException {
    var jsonSchema = """
{
  "$id": "https://example.com/person.schema.json",
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "title": "Person",
  "type": "object",
  "properties": {
    "firstName": {
      "type": "string",
      "description": "The person's first name."
    },
    "lastName": {
      "type": "string",
      "description": "The person's last name."
    },
    "age": {
      "description": "Age in years which must be equal to or greater than zero.",
      "type": "integer",
      "minimum": 0
    }
  }
}
""";
    var cls = compileSampleSchemaFromString(jsonSchema, "Person");
    assertNotNull(cls);
    var fields = cls.getDeclaredFields();
    assertEquals(3, fields.length);
    var constructors = cls.getDeclaredConstructors();
    assertEquals(1, constructors.length);
    var methods = cls.getDeclaredMethods();
    assertEquals(6, methods.length);

    var person = constructors[0].newInstance(47, "Nacho", "Brito");
    assertNotNull(person);

    Arrays.sort(methods, 0, methods.length, comparing(m -> m.getName()));
    /*
    Expected order:
    0: public java.lang.Integer Person.age()
    1: public final boolean Person.equals(java.lang.Object)
    2: public java.lang.String Person.firstName()
    3: public final int Person.hashCode()
    4: public java.lang.String Person.lastName()
    5: public final java.lang.String Person.toString()
     */
    assertEquals(47, methods[0].invoke(person));
    assertEquals("Nacho", methods[2].invoke(person));
    assertEquals("Brito", methods[4].invoke(person));

    // System.out.printf("Person created %s%n", person);
  }

  @Test
  void expectEqualsAndHashCodeMethodsAreLegal()
      throws IOException,
          ClassNotFoundException,
          InvocationTargetException,
          InstantiationException,
          IllegalAccessException {
    var cls = compileSampleSchemaFromFile("classpath:test-schemas/Person.json", "Person");
    var constructors = cls.getDeclaredConstructors();
    var person = constructors[0].newInstance(47, "Nacho", "Brito");

    var person2 = constructors[0].newInstance(47, "Nacho", "Brito");
    assertEquals(person2, person);
    assertEquals(person2.hashCode(), person.hashCode());

    var person3 = constructors[0].newInstance(48, "Nacho", "Brito");
    assertNotEquals(person3, person);
    assertNotEquals(person3.hashCode(), person.hashCode());

    var person4 = constructors[0].newInstance(47, "Pancho", "Brito");
    assertNotEquals(person4, person);
    assertNotEquals(person4.hashCode(), person.hashCode());

    var person5 = constructors[0].newInstance(47, "Nacho", "Lopez");
    assertNotEquals(person5, person);
    assertNotEquals(person5.hashCode(), person.hashCode());

    assertNotEquals("A string", person);
  }

  @Test
  void expectToStringMethodIsLegal()
      throws IOException,
          ClassNotFoundException,
          InvocationTargetException,
          InstantiationException,
          IllegalAccessException {
    var person = instantiateSampleSchema("classpath:test-schemas/Person.json", "Person", 47, "Nacho", "Brito");
    var expected = new Person(47, "Nacho", "Brito");
    assertEquals(expected.toString(), person.toString());
  }

}

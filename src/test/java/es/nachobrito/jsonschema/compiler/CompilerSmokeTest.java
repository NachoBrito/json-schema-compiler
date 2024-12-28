package es.nachobrito.jsonschema.compiler;

import static java.util.Comparator.comparing;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

/** Verifies basic schema compilation, and general cases for toString/hashCode/equals methods. */
public class CompilerSmokeTest extends CompilerTest{

  public static final String TARGET_GENERATED_CLASSES = "target/generated-classes";

  @Test
  void expectSimpleFileCompiled()
      throws IOException,
          ClassNotFoundException,
          InvocationTargetException,
          InstantiationException,
          IllegalAccessException {
    var cls = compileSampleSchema("classpath:test-schemas/Person.json", "Person");
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
    var cls = compileSampleSchema("classpath:test-schemas/Person.json", "Person");
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

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

import es.nachobrito.jsonschema.compiler.domain.Compiler;
import es.nachobrito.jsonschema.compiler.infrastructure.jsonrefparser.JsonSchemaReader;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompilerTest {
  /**
   * @param filePath
   * @param expectedName
   * @param initArgs
   * @return
   * @throws IOException
   * @throws ClassNotFoundException
   * @throws InvocationTargetException
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
  protected static Object instantiateSampleSchema(
      String filePath, String expectedName, Object... initArgs)
      throws IOException,
          ClassNotFoundException,
          InvocationTargetException,
          InstantiationException,
          IllegalAccessException {
    var cls = compileSampleSchemaFromFile(filePath, expectedName);
    var constructors = cls.getDeclaredConstructors();
    var person = constructors[0].newInstance(initArgs);
    return person;
  }

  /**
   * @param filePath
   * @param expectedName
   * @return
   * @throws IOException
   * @throws ClassNotFoundException
   */
  protected static Class<?> compileSampleSchemaFromFile(String filePath, String expectedName)
      throws IOException, ClassNotFoundException {
    var uri = URI.create(filePath);
    var destURL =
        Path.of(CompilerSmokeTest.TARGET_GENERATED_CLASSES)
            .toAbsolutePath()
            .toFile()
            .toURI()
            .toURL();
    var destPath =
        Path.of("%s/%s.class".formatted(CompilerSmokeTest.TARGET_GENERATED_CLASSES, expectedName));
    var compiler = new Compiler(destPath, "", new JsonSchemaReader());

    Files.deleteIfExists(destPath);
    compiler.compile(uri);
    assertTrue(destPath.toFile().exists());

    var cls = new URLClassLoader(new URL[] {destURL}).loadClass(expectedName);
    return cls;
  }

  /**
   * @param jsonSchema
   * @param expectedName
   * @return
   * @throws IOException
   * @throws ClassNotFoundException
   */
  protected static Class<?> compileSampleSchemaFromString(String jsonSchema, String expectedName)
          throws IOException, ClassNotFoundException {
    var destURL =
            Path.of(CompilerSmokeTest.TARGET_GENERATED_CLASSES)
                    .toAbsolutePath()
                    .toFile()
                    .toURI()
                    .toURL();
    var destPath =
            Path.of("%s/%s.class".formatted(CompilerSmokeTest.TARGET_GENERATED_CLASSES, expectedName));
    var compiler = new Compiler(destPath, "", new JsonSchemaReader());

    Files.deleteIfExists(destPath);
    compiler.compile(jsonSchema);
    assertTrue(destPath.toFile().exists());

    var cls = new URLClassLoader(new URL[] {destURL}).loadClass(expectedName);
    return cls;
  }

  @BeforeAll
  static void beforeAll() throws IOException {
    Files.createDirectories(Path.of(CompilerSmokeTest.TARGET_GENERATED_CLASSES));
  }

  static void afterAll() throws IOException {
    Files.deleteIfExists(Path.of(CompilerSmokeTest.TARGET_GENERATED_CLASSES));
  }
}

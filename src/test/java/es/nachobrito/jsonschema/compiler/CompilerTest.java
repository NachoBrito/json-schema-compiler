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

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import es.nachobrito.jsonschema.compiler.domain.Compiler;
import es.nachobrito.jsonschema.compiler.domain.runtimeconfiguration.RuntimeConfigurationRecord;
import es.nachobrito.jsonschema.compiler.infrastructure.jsonrefparser.JsonSchemaReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import es.nachobrito.jsonschema.compiler.infrastructure.jsonrefparser.JsonSchemaReaderFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class CompilerTest {
  public static final String TARGET_GENERATED_CLASSES = "target/generated-classes";

  protected static Object instantiateSampleSchema(
      String filePath, String expectedName, Object... initArgs)
      throws IOException,
          ClassNotFoundException,
          InvocationTargetException,
          InstantiationException,
          IllegalAccessException {
    var cls = compileSampleSchemaFromFile(filePath, expectedName);
    var constructors = cls.getDeclaredConstructors();
    return constructors[0].newInstance(initArgs);
  }

  protected static Class<?> compileSampleSchemaFromFile(String filePath, String expectedName)
      throws IOException, ClassNotFoundException {
    var uri = URI.create(filePath);
    Path destPath = Path.of(CompilerSmokeTest.TARGET_GENERATED_CLASSES);
    var destURL = destPath.toAbsolutePath().toFile().toURI().toURL();
    var compiler =
        new Compiler(new RuntimeConfigurationRecord(destPath, ""), new JsonSchemaReaderFactory());
    compiler.compile(uri);
    assertTrue(Files.exists(destPath));

    return new URLClassLoader(new URL[] {destURL}).loadClass(expectedName);
  }

  protected static Class<?> compileSampleSchemaFromString(String jsonSchema, String expectedName)
      throws IOException, ClassNotFoundException {
    Path destPath = Path.of(CompilerSmokeTest.TARGET_GENERATED_CLASSES);
    var destURL = destPath.toAbsolutePath().toFile().toURI().toURL();
    var compiler =
        new Compiler(new RuntimeConfigurationRecord(destPath, ""), new JsonSchemaReaderFactory());
    compiler.compile(jsonSchema);
    assertTrue(Files.exists(destPath));

    return new URLClassLoader(new URL[] {destURL}).loadClass(expectedName);
  }

  @BeforeEach
  void beforeAll() throws IOException {
    Files.createDirectories(Path.of(CompilerSmokeTest.TARGET_GENERATED_CLASSES));
  }

  @AfterEach
  void afterAll() throws IOException {
    var pathToBeDeleted = Path.of(CompilerSmokeTest.TARGET_GENERATED_CLASSES);
    Files.walk(pathToBeDeleted)
        .sorted(Comparator.reverseOrder())
        .map(Path::toFile)
        .forEach(File::delete);
  }

  protected ObjectMapper createObjectMapper() {
    return new ObjectMapper().registerModule(new JavaTimeModule());
  }


  protected Path compileSampleSchemaFromFileToJar(String schemaFile, String packageName) {
    var uri = URI.create(schemaFile);
    Path destPath = Path.of(CompilerSmokeTest.TARGET_GENERATED_CLASSES + "/generated.jar");
    var compiler =
            new Compiler(new RuntimeConfigurationRecord(destPath, packageName), new JsonSchemaReaderFactory());
    compiler.compile(uri);
    assertTrue(Files.exists(destPath));
    return destPath;
  }
}

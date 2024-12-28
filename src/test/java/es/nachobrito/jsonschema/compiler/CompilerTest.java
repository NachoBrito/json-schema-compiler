package es.nachobrito.jsonschema.compiler;

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
     *
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
    protected static Object instantiateSampleSchema(String filePath, String expectedName, Object... initArgs)
        throws IOException,
            ClassNotFoundException,
            InvocationTargetException,
            InstantiationException,
            IllegalAccessException {
      var cls = compileSampleSchema(filePath, expectedName);
      var constructors = cls.getDeclaredConstructors();
      var person = constructors[0].newInstance(initArgs);
      return person;
    }

    /**
     *
     * @param filePath
     * @param expectedName
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    protected static Class<?> compileSampleSchema(String filePath, String expectedName) throws IOException, ClassNotFoundException {
      var uri = URI.create(filePath);
      var compiler = new Compiler();
      var destURL = Path.of(CompilerSmokeTest.TARGET_GENERATED_CLASSES).toAbsolutePath().toFile().toURI().toURL();
      var destPath = Path.of("%s/%s.class".formatted(CompilerSmokeTest.TARGET_GENERATED_CLASSES, expectedName));

      Files.deleteIfExists(destPath);
      compiler.compile(uri, destPath);
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

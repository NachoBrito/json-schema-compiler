package es.nachobrito.jsonschema.compiler;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.classfile.ClassFile;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static java.util.Comparator.comparing;
import static org.junit.jupiter.api.Assertions.*;

public class CompilerTest {

    public static final String TARGET_GENERATED_CLASSES = "target/generated-classes";

    @BeforeAll
    static void beforeAll() throws IOException {
        Files.createDirectories(Path.of(TARGET_GENERATED_CLASSES));
    }

    static void afterAll() throws IOException {
        Files.deleteIfExists(Path.of(TARGET_GENERATED_CLASSES));
    }

    @Test
    void expectSimpleFileCompiled() throws IOException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var filePath = "classpath:test-schemas/Person.json";
        var uri = URI.create(filePath);
        var compiler = new Compiler();
        var destURL = Path.of(TARGET_GENERATED_CLASSES).toAbsolutePath().toFile().toURI().toURL();
        var destPath = Path.of(TARGET_GENERATED_CLASSES + "/Person.class");
        var expectedName = "Person";

        Files.deleteIfExists(destPath);
        compiler.compile(uri, destPath);
        assertTrue(destPath.toFile().exists());

        var cls = new URLClassLoader(new URL[]{destURL}).loadClass(expectedName);
        assertNotNull(cls);
        var fields = cls.getDeclaredFields();
        assertEquals(3, fields.length);
        var constructors = cls.getDeclaredConstructors();
        assertEquals(1, constructors.length);
        var methods = cls.getDeclaredMethods();
        assertEquals(5, methods.length);

        var person = constructors[0].newInstance(47, "Nacho", "Brito");
        assertNotNull(person);

        Arrays.sort(methods, 0, methods.length, comparing(m -> m.getName()));
        assertEquals(47, methods[0].invoke(person));
        assertEquals("Nacho", methods[1].invoke(person));
        assertEquals("Brito", methods[3].invoke(person));

        var expected = new Person(47, "Nacho", "Brito");
        assertEquals(expected.toString(), person.toString());

        var person2 = constructors[0].newInstance(47, "Nacho", "Brito");
        assertEquals(person2.hashCode(), person.hashCode());

        var person3 = constructors[0].newInstance(48, "Nacho", "Brito");
        assertNotEquals(person3.hashCode(), person.hashCode());

        var person4 = constructors[0].newInstance(47, "Pancho", "Brito");
        assertNotEquals(person4.hashCode(), person.hashCode());

        var person5 = constructors[0].newInstance(47, "Nacho", "Lopez");
        assertNotEquals(person5.hashCode(), person.hashCode());

        //System.out.printf("Person created %s%n", person);
    }

}

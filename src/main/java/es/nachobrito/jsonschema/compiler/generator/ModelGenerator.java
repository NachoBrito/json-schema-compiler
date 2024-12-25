package es.nachobrito.jsonschema.compiler.generator;

import java.lang.classfile.ClassBuilder;
import java.lang.constant.ClassDesc;
import java.util.Set;
import java.util.SortedMap;

public interface ModelGenerator {

  static Set<ModelGenerator> of(
      ClassDesc classDesc, ClassBuilder classBuilder, SortedMap<String, ClassDesc> properties) {
    return Set.of(
        new ConstructorGenerator(classDesc, classBuilder, properties),
        new PropertiesGenerator(classDesc, classBuilder, properties),
        new EqualsGenerator(classDesc, classBuilder, properties),
        new HashCodeGenerator(classDesc, classBuilder, properties),
        new ToStringGenerator(classDesc, classBuilder, properties));
  }

  void generatePart();
}

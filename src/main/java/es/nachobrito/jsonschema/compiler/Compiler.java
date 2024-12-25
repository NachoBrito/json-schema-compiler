package es.nachobrito.jsonschema.compiler;

import es.nachobrito.jsonschema.compiler.generator.*;

import java.io.IOException;
import java.lang.classfile.ClassBuilder;
import java.lang.classfile.ClassFile;
import java.lang.constant.ClassDesc;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.SortedMap;

import static java.lang.classfile.ClassFile.ACC_FINAL;
import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.constant.ClassDesc.of;

public class Compiler {

  public void compile(URI schemaURI, Path destinationPath) {
    var schemaReader = SchemaReader.of(schemaURI);
    var className = schemaReader.getClassName();
    var properties = schemaReader.getProperties();

    try {
      ClassFile.of()
          .buildTo(
              destinationPath,
              of(className),
              classBuilder -> writeRecord(className, classBuilder, properties));
    } catch (IOException e) {
      throw new CompilerException(e);
    }
  }

  private void writeRecord(
      String className, ClassBuilder classBuilder, SortedMap<String, ClassDesc> properties) {
    classBuilder.withFlags(ACC_PUBLIC | ACC_FINAL).withSuperclass(of("java.lang.Record"));

    var classDesc = of(className);
    ModelGenerator.of(classDesc, classBuilder, properties).forEach(ModelGenerator::generatePart);
  }
}

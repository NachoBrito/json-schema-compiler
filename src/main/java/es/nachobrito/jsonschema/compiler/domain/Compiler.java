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

package es.nachobrito.jsonschema.compiler.domain;

import static java.lang.classfile.ClassFile.ACC_FINAL;
import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.constant.ClassDesc.of;

import es.nachobrito.jsonschema.compiler.domain.generator.ModelGenerator;
import es.nachobrito.jsonschema.compiler.domain.runtimeconfiguration.RuntimeConfiguration;
import es.nachobrito.jsonschema.compiler.domain.schemareader.SchemaReader;
import java.io.IOException;
import java.lang.classfile.ClassBuilder;
import java.lang.classfile.ClassFile;
import java.net.URI;
import java.nio.file.Path;
import java.util.SortedMap;

public class Compiler {
  private final RuntimeConfiguration runtimeConfiguration;
  private final SchemaReader schemaReader;

  public Compiler(RuntimeConfiguration runtimeConfiguration, SchemaReader schemaReader) {
    this.runtimeConfiguration = runtimeConfiguration;
    this.schemaReader = schemaReader;
  }

  /**
   * Compiles the schema defined in a file, represented by the schemaURI param
   *
   * @param schemaURI the uri of the file to compile
   */
  public void compile(URI schemaURI) {
    compile(schemaReader.read(schemaURI));
  }

  /**
   * Compiles the schema defined in a String with the JSON
   *
   * @param jsonSchema the schema definition
   */
  public void compile(String jsonSchema) {
    compile(schemaReader.read(jsonSchema));
  }

  private void compile(Schema schema) {
    var className =
        runtimeConfiguration
            .getPackageName()
            .map(pkg -> "%s.%s".formatted(pkg, schema.className()))
            .orElse(schema.className());
    var destinationPath = buildDestinationPath(className);
    var properties = schema.properties();

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

  private Path buildDestinationPath(String className) {
    var parts = "%s.class".formatted(className.replace('.', '/'));
    var path = Path.of(runtimeConfiguration.getOutputFolder().toAbsolutePath().toString(), parts);
    path.getParent().toFile().mkdirs();
    return path;
  }

  private void writeRecord(
      String className, ClassBuilder classBuilder, SortedMap<String, Property> properties) {
    classBuilder.withFlags(ACC_PUBLIC | ACC_FINAL).withSuperclass(of("java.lang.Record"));

    var classDesc = of(className);
    ModelGenerator.of(runtimeConfiguration, classDesc, classBuilder, properties)
        .forEach(ModelGenerator::generatePart);
  }
}

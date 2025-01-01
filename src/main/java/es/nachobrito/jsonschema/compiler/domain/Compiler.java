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

import es.nachobrito.jsonschema.compiler.domain.generator.ClassGenerationParams;
import es.nachobrito.jsonschema.compiler.domain.generator.ModelGenerator;
import es.nachobrito.jsonschema.compiler.domain.runtimeconfiguration.RuntimeConfiguration;
import es.nachobrito.jsonschema.compiler.domain.schemareader.SchemaReaderFactory;
import java.lang.classfile.ClassBuilder;
import java.lang.classfile.ClassFile;
import java.net.URI;
import java.util.List;
import java.util.SortedMap;

public class Compiler {
  private final RuntimeConfiguration runtimeConfiguration;
  private final SchemaReaderFactory schemaReaderFactory;
  private final GeneratedClassesHandler generatedClassesHandler;

  public Compiler(
      RuntimeConfiguration runtimeConfiguration, SchemaReaderFactory schemaReaderFactory) {
    this.runtimeConfiguration = runtimeConfiguration;
    this.schemaReaderFactory = schemaReaderFactory;
    this.generatedClassesHandler = runtimeConfiguration.getGeneratedClassesHandler();
  }

  /**
   * Compiles the schema defined in a file, represented by the schemaURI param
   *
   * @param schemaURI the uri of the file to compile
   */
  public void compile(URI schemaURI) {
    compileAll(schemaReaderFactory.makeSchemaReader().read(schemaURI));
  }

  /**
   * Compiles the schema defined in a String with the JSON
   *
   * @param jsonSchema the schema definition
   */
  public void compile(String jsonSchema) {
    compileAll(schemaReaderFactory.makeSchemaReader().read(jsonSchema));
  }

  private void compileAll(List<Schema> schemas) {
    generatedClassesHandler.beforeCompile();
    schemas.forEach(this::compileSchema);
    generatedClassesHandler.afterCompile();
  }

  private void compileSchema(Schema schema) {
    var className =
        runtimeConfiguration
            .getPackageName()
            .map(pkg -> "%s.%s".formatted(pkg, schema.className()))
            .orElse(schema.className());
    var properties = schema.properties();

    var bytes =
        ClassFile.of()
            .build(of(className), classBuilder -> writeRecord(className, classBuilder, properties));

    generatedClassesHandler.handleGeneratedClass(className, bytes);
  }

  private void writeRecord(
      String className, ClassBuilder classBuilder, SortedMap<String, Property> properties) {
    classBuilder.withFlags(ACC_PUBLIC | ACC_FINAL).withSuperclass(of("java.lang.Record"));

    var classDesc = of(className);
    var params = new ClassGenerationParams(classDesc, classBuilder, properties);
    ModelGenerator.of(runtimeConfiguration, params).forEach(ModelGenerator::generatePart);
  }
}

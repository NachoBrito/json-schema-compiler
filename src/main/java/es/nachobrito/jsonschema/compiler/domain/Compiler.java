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
import java.io.IOException;
import java.lang.classfile.ClassBuilder;
import java.lang.classfile.ClassFile;
import java.lang.constant.ClassDesc;
import java.net.URI;
import java.nio.file.Path;
import java.util.SortedMap;

public class Compiler {
  private final Path destinationFolder;
  private final String packageName;
  private final SchemaReader schemaReader;

  public Compiler(Path destinationFolder, String packageName, SchemaReader schemaReader) {
    this.destinationFolder = destinationFolder;
    this.packageName = packageName;
    this.schemaReader = schemaReader;
  }

  public void compile(URI schemaURI) {
    compile(schemaReader.read(schemaURI));
  }

  public void compile(String jsonSchema) {
    compile(schemaReader.read(jsonSchema));
  }

  private void compile(Schema schema) {
    var className = this.packageName + schema.className();
    var destinationPath = destinationFolder;
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

  private void writeRecord(
      String className, ClassBuilder classBuilder, SortedMap<String, ClassDesc> properties) {
    classBuilder.withFlags(ACC_PUBLIC | ACC_FINAL).withSuperclass(of("java.lang.Record"));

    var classDesc = of(className);
    ModelGenerator.of(classDesc, classBuilder, properties).forEach(ModelGenerator::generatePart);
  }
}

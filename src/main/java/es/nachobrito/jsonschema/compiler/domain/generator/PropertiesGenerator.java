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

package es.nachobrito.jsonschema.compiler.domain.generator;

import static java.lang.classfile.ClassFile.*;

import java.lang.classfile.ClassBuilder;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.SortedMap;

record PropertiesGenerator(
    ClassDesc classDesc, ClassBuilder classBuilder, SortedMap<String, ClassDesc> properties)
    implements ModelGenerator {
  @Override
  public void generatePart() {
    properties
        .entrySet()
        .forEach(entry -> buildProperty(classDesc, entry.getKey(), entry.getValue(), classBuilder));
  }

  private void buildProperty(
      ClassDesc className, String name, ClassDesc type, ClassBuilder classBuilder) {
    buildField(name, type, classBuilder);
    buildAccessor(className, name, type, classBuilder);
  }

  private void buildAccessor(
      ClassDesc classDesc, String name, ClassDesc type, ClassBuilder classBuilder) {
    classBuilder.withMethodBody(
        name,
        MethodTypeDesc.of(type),
        ACC_PUBLIC,
        builder -> builder.aload(0).getfield(classDesc, name, type).areturn());
  }

  private void buildField(String name, ClassDesc type, ClassBuilder classBuilder) {
    classBuilder.withField(name, type, ACC_PRIVATE | ACC_FINAL);
  }
}

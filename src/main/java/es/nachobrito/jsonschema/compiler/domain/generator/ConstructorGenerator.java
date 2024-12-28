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

import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.constant.ClassDesc.of;
import static java.lang.constant.ConstantDescs.CD_void;
import static java.lang.constant.ConstantDescs.INIT_NAME;

import java.lang.classfile.ClassBuilder;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.SortedMap;

record ConstructorGenerator(
    ClassDesc classDesc, ClassBuilder classBuilder, SortedMap<String, ClassDesc> properties)
    implements ModelGenerator {
  @Override
  public void generatePart() {
    var propertyTypes = properties.values().toArray(ClassDesc[]::new);

    classBuilder.withMethodBody(
        INIT_NAME,
        MethodTypeDesc.of(CD_void, propertyTypes),
        ACC_PUBLIC,
        builder -> {
          // Invoke parent constructor
          builder
              .aload(0)
              .invokespecial(of("java.lang.Record"), INIT_NAME, MethodTypeDesc.of(CD_void));
          // Set properties:
          int index = 1;
          for (var entry : properties.entrySet()) {
            builder.aload(0).aload(index++).putfield(classDesc, entry.getKey(), entry.getValue());
          }
          builder.return_();
        });
  }
}

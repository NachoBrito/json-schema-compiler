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

import static java.lang.classfile.ClassFile.ACC_FINAL;
import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.constant.ClassDesc.of;
import static java.lang.constant.ConstantDescs.*;

import java.lang.classfile.ClassBuilder;
import java.lang.constant.*;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.Collectors;

record ToStringGenerator(
    ClassDesc classDesc, ClassBuilder classBuilder, SortedMap<String, ClassDesc> properties)
    implements ModelGenerator {
  @Override
  public void generatePart() {
    // loosely based on:
    // https://github.com/openjdk/babylon/blob/490332b12e479d8a0c164cb32dab1def982d8fce/hat/hat/src/main/java/hat/ifacemapper/ByteCodeGenerator.java#L36
    var nonArrayGetters =
        properties.entrySet().stream()
            .filter(entry -> !entry.getValue().isArray())
            .map(
                entry ->
                    MethodHandleDesc.ofField(
                        DirectMethodHandleDesc.Kind.GETTER,
                        classDesc,
                        entry.getKey(),
                        entry.getValue()))
            .toList();

    var recipe =
        properties.entrySet().stream()
            .map(
                entry ->
                    entry.getValue().isArray()
                        ? String.format(
                            "%s=%s%s",
                            entry.getKey(), entry.getValue().arrayType().displayName(), "[]")
                        : String.format("%s=\u0001", entry.getKey()))
            .collect(Collectors.joining(", ", classDesc.displayName() + "[", "]"));

    DirectMethodHandleDesc bootstrap =
        ofCallsiteBootstrap(
            of("java.lang.invoke.StringConcatFactory"),
            "makeConcatWithConstants",
            CD_CallSite,
            CD_String,
            CD_Object.arrayType());

    List<ClassDesc> getDescriptions =
        properties.values().stream().filter(it -> !it.isArray()).toList();

    DynamicCallSiteDesc desc =
        DynamicCallSiteDesc.of(
            bootstrap,
            "toString",
            MethodTypeDesc.of(CD_String, getDescriptions), // String, g0, g1, ...
            recipe);

    classBuilder.withMethodBody(
        "toString",
        MethodTypeDesc.of(CD_String),
        ACC_PUBLIC | ACC_FINAL,
        cob -> {
          for (int i = 0; i < nonArrayGetters.size(); i++) {
            var name = nonArrayGetters.get(i).methodName();
            cob.aload(0);
            // Method gi:()?
            cob.invokevirtual(classDesc, name, MethodTypeDesc.of(getDescriptions.get(i)));
          }
          cob.invokedynamic(desc);
          cob.areturn();
        });
  }
}
/*
 *    Copyright 2025 Nacho Brito
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

import com.fasterxml.jackson.annotation.JsonProperty;
import es.nachobrito.jsonschema.compiler.domain.Property;
import es.nachobrito.jsonschema.compiler.domain.runtimeconfiguration.RuntimeConfiguration;
import java.lang.classfile.Annotation;
import java.lang.classfile.AnnotationElement;
import java.lang.classfile.ClassBuilder;
import java.lang.classfile.attribute.RuntimeVisibleParameterAnnotationsAttribute;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.List;
import java.util.SortedMap;

record ConstructorGenerator(
    RuntimeConfiguration runtimeConfiguration,
    ClassGenerationParams params
)
    implements ModelGenerator {
  @Override
  public void generatePart() {
    var propertyTypes =
        params.properties().values().stream().map(Property::type).toArray(ClassDesc[]::new);

    params.classBuilder().withMethod(
        INIT_NAME,
        MethodTypeDesc.of(CD_void, propertyTypes),
        ACC_PUBLIC,
        methodBuilder -> {
          methodBuilder.withCode(
              codeBuilder -> {
                // Invoke parent constructor
                codeBuilder
                    .aload(0)
                    .invokespecial(of("java.lang.Record"), INIT_NAME, MethodTypeDesc.of(CD_void));
                // Set params.properties():
                int index = 1;
                for (var entry : params.properties().entrySet()) {
                  codeBuilder
                      .aload(0)
                      .aload(index++)
                      .putfield(
                          params.classDesc(), entry.getValue().formattedName(), entry.getValue().type());
                }
                codeBuilder.return_();
              });
          if (runtimeConfiguration.withJacksonAnnotations()) {
            var annotations =
                params.properties().values().stream()
                    .map(
                        property ->
                            List.of(
                                Annotation.of(
                                    ClassDesc.of(JsonProperty.class.getName()),
                                    AnnotationElement.ofString("value", property.key()))))
                    .toList();

            methodBuilder.with(RuntimeVisibleParameterAnnotationsAttribute.of(annotations));
          }
        });
  }
}

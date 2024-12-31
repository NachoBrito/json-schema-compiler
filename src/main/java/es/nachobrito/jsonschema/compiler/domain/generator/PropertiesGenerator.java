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

import com.fasterxml.jackson.annotation.JsonProperty;
import es.nachobrito.jsonschema.compiler.domain.Property;
import es.nachobrito.jsonschema.compiler.domain.runtimeconfiguration.RuntimeConfiguration;
import java.lang.classfile.Annotation;
import java.lang.classfile.AnnotationElement;
import java.lang.classfile.ClassBuilder;
import java.lang.classfile.attribute.RuntimeInvisibleAnnotationsAttribute;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.List;
import java.util.SortedMap;

record PropertiesGenerator(
    RuntimeConfiguration runtimeConfiguration,
    ClassDesc classDesc,
    ClassBuilder classBuilder,
    SortedMap<String, Property> properties)
    implements ModelGenerator {
  @Override
  public void generatePart() {
    properties
        .entrySet()
        .forEach(entry -> buildProperty(classDesc, entry.getValue(), classBuilder));
  }

  private void buildProperty(ClassDesc className, Property property, ClassBuilder classBuilder) {
    buildField(property, classBuilder);
    buildAccessor(className, property, classBuilder);
  }

  private void buildAccessor(ClassDesc classDesc, Property property, ClassBuilder classBuilder) {
    var name = property.formattedName();
    var type = property.type();
    classBuilder.withMethodBody(
        name,
        MethodTypeDesc.of(type),
        ACC_PUBLIC,
        builder -> builder.aload(0).getfield(classDesc, name, type).areturn());
  }

  private void buildField(Property property, ClassBuilder classBuilder) {

    classBuilder.withField(
        property.formattedName(),
        property.type(),
        fieldBuilder -> {
          fieldBuilder.withFlags(ACC_PRIVATE | ACC_FINAL);

          if (runtimeConfiguration.withJacksonAnnotations()) {
            fieldBuilder.with(
                RuntimeInvisibleAnnotationsAttribute.of(
                    List.of(
                        Annotation.of(
                            ClassDesc.of(JsonProperty.class.getName()),
                            AnnotationElement.ofString("value", property.key())))));
          }
        });
  }
}

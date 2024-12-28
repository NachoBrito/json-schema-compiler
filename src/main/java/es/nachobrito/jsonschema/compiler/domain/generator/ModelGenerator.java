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

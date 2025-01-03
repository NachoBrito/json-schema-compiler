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

package es.nachobrito.jsonschema.compiler.domain.runtimeconfiguration;

import es.nachobrito.jsonschema.compiler.domain.GeneratedClassesHandler;
import es.nachobrito.jsonschema.compiler.infrastructure.IndividualFilesHandler;
import es.nachobrito.jsonschema.compiler.infrastructure.JarFileHandler;
import java.nio.file.Path;
import java.util.Optional;

public interface RuntimeConfiguration {
  /**
   * @return the base package name for generated classes
   */
  default Optional<String> getPackageName() {
    return Optional.empty();
  }

  /**
   * @return the output folder for generated class files
   */
  default Path getOutputPath() {
    return Path.of(".");
  }

  default Optional<Path> getJsonSchemaFile() {
    return Optional.empty();
  }

  default Optional<String> getJsonSchemaCode() {
    return Optional.empty();
  }

  default boolean withJacksonAnnotations() {
    return true;
  }

  default GeneratedClassesHandler getGeneratedClassesHandler(){
    var path = getOutputPath().toAbsolutePath();
    if (path.toString().endsWith(".jar")) {
      return new JarFileHandler(path);
    }
    return new IndividualFilesHandler(path);
  }
}

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

package es.nachobrito.jsonschema.compiler.application.cli;

import es.nachobrito.jsonschema.compiler.domain.Compiler;
import es.nachobrito.jsonschema.compiler.infrastructure.jsonrefparser.JsonSchemaReader;
import es.nachobrito.jsonschema.compiler.infrastructure.jsonrefparser.JsonSchemaReaderFactory;

public class App {
  public static void main(String[] args) {
    var params = RuntimeConfiguration.of(args);

    var compiler = new Compiler(params, new JsonSchemaReaderFactory());
    var jsonSchemaFile = params.getJsonSchemaFile();
    var jsonSchemaCode = params.getJsonSchemaCode();
    if (jsonSchemaFile.isPresent()) {
      compiler.compile(jsonSchemaFile.get().toUri());
    } else if (jsonSchemaCode.isPresent()) {
      compiler.compile(jsonSchemaCode.get());
    } else {
      System.err.println(
          "Nothing to compile. Provide a path to a json schema file as an argument, or the JSON code through stdin");
      System.exit(1);
    }
  }
}

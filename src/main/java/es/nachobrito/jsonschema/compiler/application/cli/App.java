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

package es.nachobrito.jsonschema.compiler.application.cli;

import com.beust.jcommander.JCommander;
import es.nachobrito.jsonschema.compiler.application.jcommander.Params;
import es.nachobrito.jsonschema.compiler.domain.Compiler;
import es.nachobrito.jsonschema.compiler.infrastructure.jsonrefparser.JsonSchemaReader;

import java.net.URI;

public class App {
  public static void main(String[] args) {
    var params = new Params();
    JCommander.newBuilder()
            .addObject(params)
            .build()
            .parse(args);

    var compiler = new Compiler(params, new JsonSchemaReader());
    var uri = URI.create(params.getSchemaFile());
    compiler.compile(uri);
  }
}

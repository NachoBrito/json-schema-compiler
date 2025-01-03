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

package es.nachobrito.jsonschema.compiler.infrastructure.jsonrefparser;

import es.nachobrito.jsonschema.compiler.domain.CompilerException;
import es.nachobrito.jsonschema.compiler.domain.schemareader.AbstractSchemaReader;
import io.zenwave360.jsonrefparser.$RefParser;
import io.zenwave360.jsonrefparser.$Refs;
import java.io.IOException;
import java.net.URI;
import java.util.*;

public class JsonSchemaReader extends AbstractSchemaReader {

  @Override
  protected Map<String, Object> loadModels(URI uri) {
    try {
      $RefParser parser = new $RefParser(uri);
      $Refs refs = parser.parse().dereference().mergeAllOf().getRefs();
      return refs.schema();
    } catch (IOException e) {
      throw new CompilerException(e);
    }
  }

  @Override
  protected Map<String, Object> loadModels(String jsonSchema) {
    try {
      $RefParser parser = new $RefParser(jsonSchema);
      $Refs refs = parser.parse().dereference().mergeAllOf().getRefs();
      return refs.schema();
    } catch (IOException e) {
      throw new CompilerException(e);
    }
  }
}

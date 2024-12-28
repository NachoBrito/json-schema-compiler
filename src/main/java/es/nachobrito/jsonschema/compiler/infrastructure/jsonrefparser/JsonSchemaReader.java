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

package es.nachobrito.jsonschema.compiler.infrastructure.jsonrefparser;

import static java.lang.constant.ConstantDescs.*;
import static java.util.stream.Collectors.toMap;

import es.nachobrito.jsonschema.compiler.domain.CompilerException;
import es.nachobrito.jsonschema.compiler.domain.Schema;
import es.nachobrito.jsonschema.compiler.domain.SchemaReader;
import io.zenwave360.jsonrefparser.$RefParser;
import io.zenwave360.jsonrefparser.$Refs;
import java.io.IOException;
import java.lang.constant.ClassDesc;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class JsonSchemaReader implements SchemaReader {

  public String getClassName(Map<String, Object> models) {
    return String.valueOf(models.getOrDefault("title", "UnknownClassName"));
  }

  public SortedMap<String, ClassDesc> getProperties(Map<String, Object> models) {
    var definitions =
        ((Map<String, Map<String, ?>>) models.getOrDefault("properties", Collections.emptyMap()));
    return definitions.entrySet().stream()
        .collect(
            toMap(
                entry -> entry.getKey(),
                entry -> getJavaType((String) entry.getValue().get("type")),
                (v1, v2) -> {
                  throw new CompilerException("Duplicate property found!");
                },
                TreeMap::new));
  }

  private ClassDesc getJavaType(String jsonSchemaType) {
    return switch (jsonSchemaType) {
      case "string" -> CD_String;
      case "integer" -> CD_Integer;
      default -> CD_Object;
    };
  }

  private static Map<String, Object> loadModels(URI uri) {
    try {
      $RefParser parser = new $RefParser(uri);
      $Refs refs = parser.parse().dereference().mergeAllOf().getRefs();
      return refs.schema();
    } catch (IOException e) {
      throw new CompilerException(e);
    }
  }

  private Map<String, Object> loadModels(String jsonSchema) {
    try {
      $RefParser parser = new $RefParser(jsonSchema);
      $Refs refs = parser.parse().dereference().mergeAllOf().getRefs();
      return refs.schema();
    } catch (IOException e) {
      throw new CompilerException(e);
    }
  }

  @Override
  public Schema read(URI uri) {
    var models = loadModels(uri);
    return new Schema(getClassName(models), getProperties(models));
  }

  @Override
  public Schema read(String jsonSchema) {
    var models = loadModels(jsonSchema);
    return new Schema(getClassName(models), getProperties(models));
  }
}

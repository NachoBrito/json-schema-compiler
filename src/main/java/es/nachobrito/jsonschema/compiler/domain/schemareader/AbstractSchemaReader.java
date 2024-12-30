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

package es.nachobrito.jsonschema.compiler.domain.schemareader;

import static java.lang.constant.ClassDesc.of;
import static java.lang.constant.ConstantDescs.*;
import static java.util.stream.Collectors.toMap;

import es.nachobrito.jsonschema.compiler.domain.CompilerException;
import es.nachobrito.jsonschema.compiler.domain.Property;
import es.nachobrito.jsonschema.compiler.domain.Schema;
import java.io.IOException;
import java.lang.constant.ClassDesc;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.*;
import java.util.*;

public abstract class AbstractSchemaReader implements SchemaReader {
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

  public String getClassName(Map<String, Object> models) {
    return String.valueOf(models.getOrDefault("title", "UnknownClassName"));
  }

  public SortedMap<String, Property> getProperties(Map<String, Object> models) {
    var definitions =
        ((Map<String, Map<String, ?>>) models.getOrDefault("properties", Collections.emptyMap()));
    return definitions.entrySet().stream()
        .collect(
            toMap(
                Map.Entry::getKey,
                entry ->
                    createProperty(
                        entry.getKey(),
                        (String) entry.getValue().get("type"),
                        (String) entry.getValue().get("format")),
                (v1, v2) -> {
                  throw new CompilerException("Duplicate property found!");
                },
                TreeMap::new));
  }

  protected Property createProperty(String key, String type, String format) {
    return new Property(key, getJavaType(type, format));
  }

  protected ClassDesc getJavaType(String jsonSchemaType, String jsonSchemaFormat) {
    return switch (jsonSchemaType) {
      case "string" -> getJavaStringType(jsonSchemaFormat);
      case "integer" -> CD_Integer;
      case "boolean" -> CD_Boolean;
      //      case "array" -> ?;
      //      case "object" -> ?;
      default -> CD_Object;
    };
  }

  private ClassDesc getJavaStringType(String jsonSchemaFormat) {
    if (jsonSchemaFormat == null) {
      return CD_String;
    }
    return switch (jsonSchemaFormat) {
      case "date-time" -> of(OffsetDateTime.class.getName());
      case "time" -> of(OffsetTime.class.getName());
      case "date" -> of(LocalDate.class.getName());
      case "duration" -> of(Duration.class.getName());

      case "ipv4" -> of(Inet4Address.class.getName());
      case "ipv6" -> of(Inet6Address.class.getName());

      case "uuid" -> of(UUID.class.getName());
      case "uri", "uri-reference", "iri", "iri-reference" -> of(URI.class.getName());
      // "email", "idn-email", "hostname", "idn-hostname"
      default -> CD_String;
    };
  }

  protected abstract Map<String, Object> loadModels(String jsonSchema);

  protected Map<String, Object> loadModels(URI uri) {
    String json = null;
    try {
      json = Files.readString(Path.of(uri));
    } catch (IOException e) {
      throw new CompilerException(e);
    }
    return loadModels(json);
  }
}

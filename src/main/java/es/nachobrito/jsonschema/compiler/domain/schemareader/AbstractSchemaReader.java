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
import es.nachobrito.jsonschema.compiler.domain.JavaName;
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
  private Map<String, Object> models;
  private Map<String, Schema> schemas = new HashMap<>();

  @Override
  public List<Schema> read(URI uri) {
    this.models = loadModels(uri);
    return createSchemas();
  }

  @Override
  public List<Schema> read(String jsonSchema) {
    this.models = loadModels(jsonSchema);
    return createSchemas();
  }

  private List<Schema> createSchemas() {
    schemas.clear();
    registerSchema(new Schema(getRootClassName(), processProperties(getRootProperties())));
    return schemas.values().stream().toList();
  }

  private void registerSchema(Schema schema) {
    schemas.put(schema.className(), schema);
  }

  private String getRootClassName() {
    return String.valueOf(models.getOrDefault("title", "UnknownClassName"));
  }

  private SortedMap<String, Property> processProperties(Map<String, Map<String, ?>> definitions) {
    return definitions.entrySet().stream()
        .collect(
            toMap(
                Map.Entry::getKey,
                entry -> createProperty(entry.getKey(), definitions),
                (v1, v2) -> {
                  throw new CompilerException("Duplicate property found!");
                },
                TreeMap::new));
  }

  private Property createProperty(String key, Map<String, Map<String, ?>> propertyDefinitions) {
    return new Property(key, getJavaType(key, propertyDefinitions));
  }

  private ClassDesc getJavaType(
      String propertyKey, Map<String, Map<String, ?>> propertyDefinitions) {
    var property = getModelPropertyDefinition(propertyKey, propertyDefinitions);
    var jsonSchemaType = (String) property.get("type");
    var jsonSchemaFormat = (String) property.get("format");
    return switch (jsonSchemaType) {
      case "string" -> getJavaStringType(jsonSchemaFormat);
      case "integer" -> CD_Integer;
      case "number" -> CD_Double;
      case "boolean" -> CD_Boolean;
      //      case "array" -> ?;
      case "object" -> getJavaObjectType(propertyKey);
      default -> CD_Object;
    };
  }

  private ClassDesc getJavaObjectType(String propertyKey) {
    var definition = getModelPropertyDefinition(propertyKey, getRootProperties());
    var properties = definition.get("properties");
    var name = getPropertyName(propertyKey, definition);
    var schema =
        schemas.computeIfAbsent(
            name,
            it -> new Schema(it, processProperties((Map<String, Map<String, ?>>) properties)));
    return ClassDesc.of(schema.className());
  }

  private String getPropertyName(String propertyKey, Map<String, ?> definition) {
    if (definition.containsKey("title")) {
      return (String) definition.get("title");
    }

    return JavaName.fromJsonIdentifier("%s_%s".formatted(getRootClassName(), propertyKey));
  }

  private Map<String, ?> getModelPropertyDefinition(
      String propertyKey, Map<String, Map<String, ?>> properties) {
    var definition = properties.get(propertyKey);
    if (definition == null) {
      throw new CompilerException("Cannot load property %s".formatted(propertyKey));
    }
    return definition;
  }

  private Map<String, Map<String, ?>> getRootProperties() {
    //noinspection unchecked
    return ((Map<String, Map<String, ?>>)
        models.getOrDefault("properties", Collections.emptyMap()));
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

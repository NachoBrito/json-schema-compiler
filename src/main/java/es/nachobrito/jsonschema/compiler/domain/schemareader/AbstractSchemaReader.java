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

package es.nachobrito.jsonschema.compiler.domain.schemareader;

import static java.lang.constant.ConstantDescs.*;
import static java.util.stream.Collectors.toMap;

import es.nachobrito.jsonschema.compiler.domain.CompilerException;
import es.nachobrito.jsonschema.compiler.domain.JavaName;
import es.nachobrito.jsonschema.compiler.domain.Property;
import es.nachobrito.jsonschema.compiler.domain.Schema;
import java.io.IOException;
import java.lang.constant.ClassDesc;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public abstract class AbstractSchemaReader implements SchemaReader {
  private Map<String, Object> models;
  private final Map<String, Schema> schemas = new HashMap<>();

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
      case "integer" -> CD_Integer;
      case "number" -> CD_Double;
      case "boolean" -> CD_Boolean;
      case "array" -> getArrayType(propertyKey);
      case "object" -> getJavaObjectType(propertyKey);
      case "string" -> StringFormat.toClassDesc(jsonSchemaFormat);
      default -> CD_Object;
    };
  }

  private ClassDesc getArrayType(String propertyKey) {
    var definition = getModelPropertyDefinition(propertyKey, getRootProperties());
    @SuppressWarnings("unchecked")
    var items = (Map<String, ?>) definition.get("items");

    // todo: there is no support for Tuples in Java, so arrays with prefixItems will be treated as
    // Object[] for now, until a better solution is found.
    // see: https://json-schema.org/understanding-json-schema/reference/array#tupleValidation
    if (items == null) {
      return CD_Object.arrayType();
    }

    var itemsTypeDefinition = (String) items.get("type");
    var itemsTypeFormat = (String) items.get("format");
    var properties = (Map<String, Map<String, ?>>) items.get("properties");

    return switch (itemsTypeDefinition) {
      case "integer" -> CD_Integer.arrayType();
      case "number" -> CD_Double.arrayType();
      case "boolean" -> CD_Boolean.arrayType();
      // case "array" -> getArrayType(propertyKey);
      case "object" ->
          getJavaObjectType(
                  JavaName.classFromJsonIdentifier("%s_item".formatted(propertyKey)), properties)
              .arrayType();
      case "string" -> StringFormat.toClassDesc(itemsTypeFormat).arrayType();
      default -> CD_Object;
    };
  }

  private ClassDesc getJavaObjectType(String propertyKey) {
    var definition = getModelPropertyDefinition(propertyKey, getRootProperties());
    var properties = definition.get("properties");
    var name = getPropertyName(propertyKey, definition);
    return getJavaObjectType(name, (Map<String, Map<String, ?>>) properties);
  }

  private ClassDesc getJavaObjectType(String name, Map<String, Map<String, ?>> properties) {
    var schema = schemas.computeIfAbsent(name, it -> new Schema(it, processProperties(properties)));
    return ClassDesc.of(schema.className());
  }

  private String getPropertyName(String propertyKey, Map<String, ?> definition) {
    if (definition.containsKey("title")) {
      return (String) definition.get("title");
    }

    return JavaName.variableFromJsonIdentifier("%s_%s".formatted(getRootClassName(), propertyKey));
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

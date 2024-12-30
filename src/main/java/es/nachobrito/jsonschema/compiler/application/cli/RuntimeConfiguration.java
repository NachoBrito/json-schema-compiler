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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Parses allowed input parameters and implements {@link
 * es.nachobrito.jsonschema.compiler.domain.runtimeconfiguration.RuntimeConfiguration}
 */
public class RuntimeConfiguration implements es.nachobrito.jsonschema.compiler.domain.runtimeconfiguration.RuntimeConfiguration {
  private static final String JSON_SCHEMA_FILE = "JSON_SCHEMA_FILE";
  private static final String PACKAGE = "PACKAGE";
  private static final String OUTPUT = "OUTPUT";

  private static final String[] PARAM_PACKAGE = new String[] {"-p", "--package-name"};
  private static final String[] PARAM_OUTPUT = new String[] {"-o", "--output"};

  private static final Map<String, String> PARAMS_TO_KEYS = buildParamsToKeys();

  private static Map<String, String> buildParamsToKeys() {
    var map = new HashMap<String, String>(PARAM_OUTPUT.length + PARAM_PACKAGE.length);
    for (String k : PARAM_PACKAGE) map.put(k, PACKAGE);
    for (String k : PARAM_OUTPUT) map.put(k, OUTPUT);
    return map;
  }

  private final Map<String, String> arguments;

  private RuntimeConfiguration(Map<String, String> arguments) {
    this.arguments = arguments;
  }

  @Override
  public Optional<String> getPackageName() {
    return Optional.ofNullable(arguments.get(PACKAGE));
  }

  @Override
  public Path getOutputFolder() {
    return Path.of(arguments.getOrDefault(OUTPUT, "."));
  }

  @Override
  public Optional<String> getJsonSchemaCode() {
      try {
          return readInput();
      } catch (IOException e) {
          throw new RuntimeException(e);
      }
  }

  private Optional<String> readInput() throws IOException {
    if (System.in.available() == 0) {
      return Optional.empty();
    }
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    var content = reader.lines().collect(Collectors.joining("\n"));
    return Optional.of(content);
  }

  @Override
  public Optional<Path> getJsonSchemaFile() {
    return Optional.ofNullable(arguments.get(JSON_SCHEMA_FILE)).map(Path::of);
  }

  public static RuntimeConfiguration of(String[] args) {
    var arguments = new HashMap<String, String>();
    String k = null;
    for (String argument : args) {
      if (argument.charAt(0) == '-') {
        if (k != null) {
          arguments.put(k, "true");
        }
        k = argument;
        continue;
      }
      if (k != null) {
        saveParameter(arguments, k, argument);
        k = null;
      } else {
        arguments.putIfAbsent(JSON_SCHEMA_FILE, argument);
      }
    }
    return new RuntimeConfiguration(arguments);
  }

  private static void saveParameter(
      HashMap<String, String> arguments, String name, String argument) {
    var key = PARAMS_TO_KEYS.get(name);
    if (arguments.containsKey(key)) {
      throw new IllegalArgumentException(
          "Argument %s detected multiple times: '%s','%s'"
              .formatted(key, arguments.get(key), argument));
    }
    arguments.put(key, argument);
  }
}

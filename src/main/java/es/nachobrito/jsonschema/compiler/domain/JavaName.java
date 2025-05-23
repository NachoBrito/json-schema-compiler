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

package es.nachobrito.jsonschema.compiler.domain;

import java.util.Objects;
import java.util.regex.Pattern;

public class JavaName {
  private static final Pattern jsonIdPattern = Pattern.compile("[\\W_]([a-z])");

  public static String variableFromJsonIdentifier(String identifier) {
    Objects.requireNonNull(identifier);
    return jsonIdPattern.matcher(identifier).replaceAll(m -> m.group(1).toUpperCase());
  }

  public static String classFromJsonIdentifier(String identifier) {
    Objects.requireNonNull(identifier);
    var variableName = variableFromJsonIdentifier(identifier);
    return variableName.substring(0, 1).toUpperCase() + variableName.substring(1);
  }
}

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

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InputParametersTest {

  @Test
  void expectDefaultValues() {
    String[] args;
    InputParameters params;

    args = new String[] {"-p", "es.nachobrito.test", "path/to/json.schema"};
    params = InputParameters.of(args);
    assertEquals(Path.of("."), params.getOutputFolder());
    assertEquals(Optional.of(args[1]), params.getPackageName());

    args = new String[] {"-o", "/dest/folder", "path/to/json.schema"};
    params = InputParameters.of(args);
    assertEquals(Path.of(args[1]), params.getOutputFolder());
    assertEquals(Optional.empty(), params.getPackageName());

    args = new String[] {"path/to/json.schema"};
    params = InputParameters.of(args);
    assertEquals(Path.of("."), params.getOutputFolder());
    assertEquals(Optional.empty(), params.getPackageName());

    args = new String[] {};
    params = InputParameters.of(args);
    assertEquals(Path.of("."), params.getOutputFolder());
    assertEquals(Optional.empty(), params.getPackageName());
  }

  @Test
  void expectValidArgumentsParsed() {
    String[] args;
    InputParameters params;

    args = new String[] {"-o", "/dest/folder", "-p", "es.nachobrito.test", "path/to/json.schema"};
    params = InputParameters.of(args);
    assertEquals(Path.of(args[1]), params.getOutputFolder());
    assertEquals(Optional.of(args[3]), params.getPackageName());

    args = new String[] {"-p", "es.nachobrito.test", "-o", "/dest/folder", "path/to/json.schema"};
    params = InputParameters.of(args);
    assertEquals(Path.of(args[3]), params.getOutputFolder());
    assertEquals(Optional.of(args[1]), params.getPackageName());

    args = new String[] {"path/to/json.schema", "-p", "es.nachobrito.test", "-o", "/dest/folder"};
    params = InputParameters.of(args);
    assertEquals(Path.of(args[4]), params.getOutputFolder());
    assertEquals(Optional.of(args[2]), params.getPackageName());
  }

  @Test
  void expectInvalidArgumentsThrowException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          InputParameters.of(
              new String[] {
                "-o", "/dest/folder", "-o", "es.nachobrito.test", "path/to/json.schema"
              });
        });

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          InputParameters.of(
              new String[] {
                "-p", "/dest/folder", "-p", "es.nachobrito.test", "path/to/json.schema"
              });
        });

  }
}
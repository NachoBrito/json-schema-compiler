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

package es.nachobrito.jsonschema.compiler.infrastructure;

import es.nachobrito.jsonschema.compiler.domain.CompilerException;
import es.nachobrito.jsonschema.compiler.domain.GeneratedClassesHandler;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import static java.util.jar.Attributes.Name.MANIFEST_VERSION;

public class JarFileHandler implements GeneratedClassesHandler {
  private final Path jarFilePath;

  private record Entry(String name, byte[] bytes) {}

  private List<Entry> entries = new ArrayList<>();

  public JarFileHandler(Path jarFilePath) {
    this.jarFilePath = jarFilePath;
  }

  @Override
  public void beforeCompile() {
    if (Files.exists(jarFilePath)) {
      throw new CompilerException(
          "Cannot overwrite existing Jar file! -> %s".formatted(jarFilePath.toString()));
    }
  }

  @Override
  public void afterCompile() {
    var manifest = new Manifest();
    manifest.getMainAttributes().put(MANIFEST_VERSION, "1.0");
    try (var os =
        new JarOutputStream(
            new BufferedOutputStream(Files.newOutputStream(jarFilePath)), manifest)) {
      for (Entry entry : entries) {
        os.putNextEntry(new JarEntry(entry.name()));
        os.write(entry.bytes());
        os.closeEntry();
      }
    } catch (IOException e) {
      throw new CompilerException(e);
    }
  }

  @Override
  public void handleGeneratedClass(String className, byte[] bytes) {
    var name = "%s.class".formatted(className.replace('.', '/'));
    entries.add(new Entry(name, bytes));
  }
}

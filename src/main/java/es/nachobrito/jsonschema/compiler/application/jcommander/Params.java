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

package es.nachobrito.jsonschema.compiler.application.jcommander;

import com.beust.jcommander.Parameter;
import es.nachobrito.jsonschema.compiler.domain.InputParameters;

import java.nio.file.Path;
import java.util.Optional;

public class Params implements InputParameters{

  @Parameter(description="The path of the json schema file to compile")
  private String schemaFile;

  @Parameter(names = {"-p", "--package-name"}, description = "The package name for the generated classes")
  private String packageName = "";

  @Parameter(names = {"-o", "--output"}, description = "The destination folder")
  private String destinationFolder = ".";

  @Override
  public Optional<String> getPackageName() {
    return Optional.ofNullable(packageName);
  }

  @Override
  public Path getOutputFolder() {
    return Path.of(destinationFolder);
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getDestinationFolder() {
    return destinationFolder;
  }

  public void setDestinationFolder(String destinationFolder) {
    this.destinationFolder = destinationFolder;
  }

  public String getSchemaFile() {
    return schemaFile;
  }

  public void setSchemaFile(String schemaFile) {
    this.schemaFile = schemaFile;
  }

  @Override
  public String toString() {
    return "Params{" +
            "schemaFile='" + schemaFile + '\'' +
            ", packageName='" + packageName + '\'' +
            ", destinationFolder='" + destinationFolder + '\'' +
            '}';
  }
}

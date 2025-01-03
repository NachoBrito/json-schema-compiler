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

package es.nachobrito.jsonschema.compiler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompileToJarTest extends CompilerTest{

    @DisplayName("When the output path is a jar file, the generated classes have to be packed in it.")
    @Test
    void expectJarFilesGenerated() throws IOException, ClassNotFoundException {
        var jar = compileSampleSchemaFromFileToJar("classpath:test-schemas/Nested.json", "com.example");
        assertTrue(Files.exists(jar));
    }

}

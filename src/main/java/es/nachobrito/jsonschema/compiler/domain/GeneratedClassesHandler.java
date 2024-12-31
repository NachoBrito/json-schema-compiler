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

package es.nachobrito.jsonschema.compiler.domain;

public interface GeneratedClassesHandler {


    default void beforeCompile(){
        //Implement this method for preparatory tasks before any class is generated.
    };

    void handleGeneratedClass(String className, byte[] bytes);

    default void afterCompile(){
        //Implement this method for resource cleanup after all classes have been compiled.
    }
}

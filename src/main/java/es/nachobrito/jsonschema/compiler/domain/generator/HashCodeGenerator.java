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

package es.nachobrito.jsonschema.compiler.domain.generator;

import static java.lang.classfile.ClassFile.ACC_FINAL;
import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.constant.ConstantDescs.*;

import es.nachobrito.jsonschema.compiler.domain.Property;
import es.nachobrito.jsonschema.compiler.domain.runtimeconfiguration.RuntimeConfiguration;
import java.lang.classfile.ClassBuilder;
import java.lang.classfile.CodeBuilder;
import java.lang.constant.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.SortedMap;

record HashCodeGenerator(
    RuntimeConfiguration runtimeConfiguration,
    ClassDesc classDesc,
    ClassBuilder classBuilder,
    SortedMap<String, Property> properties)
    implements ModelGenerator {

  /**
   * This is inspired on <a href="https://projectlombok.org/features/EqualsAndHashCode">Lombok's
   * hashCode implementation</a>:
   *
   * <pre>{@code
   * final int PRIME = 59;
   * int result = 1;
   * final long temp1 = Double.doubleToLongBits(this.score);
   * result = (result*PRIME) + (this.name == null ? 43 : this.name.hashCode());
   * result = (result*PRIME) + (int)(temp1 ^ (temp1 >>> 32));
   * result = (result*PRIME) + Arrays.deepHashCode(this.tags);
   * return result;
   *
   * }</pre>
   */
  @Override
  public void generatePart() {
    classBuilder.withMethodBody(
        "hashCode",
        MethodTypeDesc.of(CD_int),
        ACC_PUBLIC | ACC_FINAL,
        cob -> {
          cob.ldc(1);
          properties
              .entrySet()
              .forEach(
                  entry -> {
                    var propertyName = entry.getValue().formattedName();
                    var propertyDesc = entry.getValue().type();
                    cob.ldc(59).imul();
                    loadFieldValue(propertyName, propertyDesc, cob);
                    cob.iadd();
                  });
          cob.ireturn();
        });
  }

  private void loadFieldValue(String propertyName, ClassDesc propertyDesc, CodeBuilder cob) {
    if (propertyDesc.isArray()) {
      loadArrayValue(propertyName, propertyDesc, cob);
      return;
    }
    if (propertyDesc.isPrimitive()) {
      loadPrimitiveValue(propertyName, propertyDesc, cob);
      return;
    }
    cob.aload(0)
        .getfield(classDesc, propertyName, propertyDesc)
        .invokestatic(
            ClassDesc.of(Objects.class.getName()),
            "hashCode",
            MethodTypeDesc.of(CD_int, CD_Object));
  }

  private void loadPrimitiveValue(String propertyName, ClassDesc propertyDesc, CodeBuilder cob) {
    cob.aload(0).getfield(classDesc, propertyName, propertyDesc);
  }

  private void loadArrayValue(String propertyName, ClassDesc propertyDesc, CodeBuilder cob) {

    cob.aload(0)
        .getfield(classDesc, propertyName, propertyDesc)
        .invokestatic(
            ClassDesc.of(Arrays.class.getName()),
            "deepHashcode",
            MethodTypeDesc.of(CD_Object.arrayType()));
  }
}

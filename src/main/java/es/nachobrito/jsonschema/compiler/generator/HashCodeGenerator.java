package es.nachobrito.jsonschema.compiler.generator;

import static java.lang.classfile.ClassFile.ACC_FINAL;
import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.constant.ConstantDescs.*;

import java.lang.classfile.ClassBuilder;
import java.lang.classfile.CodeBuilder;
import java.lang.constant.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.SortedMap;

record HashCodeGenerator(
    ClassDesc classDesc, ClassBuilder classBuilder, SortedMap<String, ClassDesc> properties)
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
                    var propertyName = entry.getKey();
                    var propertyDesc = entry.getValue();
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

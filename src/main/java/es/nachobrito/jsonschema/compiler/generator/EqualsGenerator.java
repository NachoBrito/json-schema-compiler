package es.nachobrito.jsonschema.compiler.generator;

import static java.lang.classfile.ClassFile.ACC_FINAL;
import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.constant.ConstantDescs.CD_Object;
import static java.lang.constant.ConstantDescs.CD_boolean;

import java.lang.classfile.ClassBuilder;
import java.lang.classfile.CodeBuilder;
import java.lang.classfile.Label;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.Objects;
import java.util.SortedMap;

record EqualsGenerator(
    ClassDesc classDesc, ClassBuilder classBuilder, SortedMap<String, ClassDesc> properties)
    implements ModelGenerator {
  @Override
  public void generatePart() {
    classBuilder.withMethodBody(
        "equals",
        MethodTypeDesc.of(CD_boolean, CD_Object),
        ACC_PUBLIC | ACC_FINAL,
        cob -> {
          var returnTrue = cob.newLabel();
          var returnFalse = cob.newLabel();
          var returnValue = cob.newLabel();
          cob
              // if (o == null) return false;
              .aload(1)
              .if_null(returnFalse)
              // if (!(o instanceof *ThisClass*)) return false;
              .aload(1)
              .instanceof_(classDesc)
              .ifeq(returnFalse)
              // if (o == this) return true;
              .aload(0)
              .aload(1)
              .if_acmpeq(returnTrue);

          properties
              .entrySet()
              .forEach(
                  entry -> {
                    var propertyName = entry.getKey();
                    var propertyDesc = entry.getValue();
                    compareProperty(propertyName, propertyDesc, cob, returnFalse);
                  });

          cob.labelBinding(returnTrue)
              .iconst_1()
              .goto_(returnValue)
              .labelBinding(returnFalse)
              .iconst_0()
              .labelBinding(returnValue)
              .ireturn();
        });
  }

  /**
   * Compare the property value in both objects, goto [returnFalse] if they are not equal.
   *
   * @param propertyName the property name
   * @param propertyDesc the ClassDesk of the property
   * @param cob the code builder
   * @param returnFalse the label to go in case the property value is not equal in both objects
   */
  private void compareProperty(
      String propertyName, ClassDesc propertyDesc, CodeBuilder cob, Label returnFalse) {
    if (propertyDesc.isPrimitive()) {
      comparePrimitive(propertyName, propertyDesc, cob, returnFalse);
      return;
    }
    if (propertyDesc.isArray()) {
      compareArray(propertyName, propertyDesc, cob, returnFalse);
      return;
    }
    cob.aload(0)
        .getfield(classDesc, propertyName, propertyDesc)
        .aload(1)
        .checkcast(classDesc)
        .getfield(classDesc, propertyName, propertyDesc)
        .invokestatic(
            ClassDesc.of(Objects.class.getName()),
            "equals",
            MethodTypeDesc.of(CD_boolean, CD_Object, CD_Object))
        .ifeq(returnFalse);
  }

  private void compareArray(
      String propertyName, ClassDesc propertyDesc, CodeBuilder cob, Label returnFalse) {
    cob.aload(0)
        .getfield(classDesc, propertyName, propertyDesc)
        .aload(1)
        .checkcast(classDesc)
        .getfield(classDesc, propertyName, propertyDesc)
        .invokestatic(
            ClassDesc.of(Objects.class.getName()),
            "deepEquals",
            MethodTypeDesc.of(CD_boolean, CD_Object, CD_Object))
        .ifeq(returnFalse);
  }

  private void comparePrimitive(
      String propertyName, ClassDesc propertyDesc, CodeBuilder cob, Label returnFalse) {
    cob.aload(0)
        .getfield(classDesc, propertyName, propertyDesc)
        .aload(1)
        .checkcast(classDesc)
        .getfield(classDesc, propertyName, propertyDesc)
        .if_icmpne(returnFalse);
  }
}

package es.nachobrito.jsonschema.compiler.generator;

import java.lang.classfile.ClassBuilder;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.SortedMap;

import static java.lang.classfile.ClassFile.ACC_FINAL;
import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.constant.ClassDesc.of;
import static java.lang.constant.ConstantDescs.CD_String;

public class ToStringGenerator implements ModelGenerator{
    @Override
    public void generatePart(ClassDesc classDesc, ClassBuilder classBuilder, SortedMap<String, ClassDesc> properties) {
//        classBuilder.withMethodBody("toString", MethodTypeDesc.of(CD_String), ACC_PUBLIC|ACC_FINAL,
//                builder -> builder
//                        .aload(0)
//                        .invokedynamic(
//                                builder.invokestatic(
//                                        of("java.lang.runtime.ObjectMethods"), "bootstrap", MethodTypeDesc.of(
//                                                of("Ljava/lang/invoke/MethodHandles$Lookup;"),
//                                                of("Ljava/lang/String;"),
//                                                of("Ljava/lang/invoke/TypeDescriptor;"),
//                                                of("Ljava/lang/Class;"),
//                                                of("Ljava/lang/String;"),
//                                                of("[Ljava/lang/invoke/MethodHandle;"),
//                                                of("Ljava/lang/Object;")
//                                        )
//                                )
//                        )
//                        .return_());
//      java/lang/runtime/ObjectMethods.bootstrap(
//          Ljava/lang/invoke/MethodHandles$Lookup;
//          Ljava/lang/String;
//          Ljava/lang/invoke/TypeDescriptor;
//          Ljava/lang/Class;
//          Ljava/lang/String;
//          [Ljava/lang/invoke/MethodHandle;)
//          Ljava/lang/Object;
    }
    /*
      public final toString()Ljava/lang/String;
   L0
    LINENUMBER 3 L0
    ALOAD 0
    INVOKEDYNAMIC toString(Les/nachobrito/jsonschema/compiler/Person;)Ljava/lang/String; [
      // handle kind 0x6 : INVOKESTATIC
      java/lang/runtime/ObjectMethods.bootstrap(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/TypeDescriptor;Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/invoke/MethodHandle;)Ljava/lang/Object;
      // arguments:
      es.nachobrito.jsonschema.compiler.Person.class,
      "age;firstName;lastName",
      // handle kind 0x1 : GETFIELD
      es/nachobrito/jsonschema/compiler/Person.age(Ljava/lang/Integer;),
      // handle kind 0x1 : GETFIELD
      es/nachobrito/jsonschema/compiler/Person.firstName(Ljava/lang/String;),
      // handle kind 0x1 : GETFIELD
      es/nachobrito/jsonschema/compiler/Person.lastName(Ljava/lang/String;)
    ]
    ARETURN
    MAXSTACK = 1
    MAXLOCALS = 1
     */
}

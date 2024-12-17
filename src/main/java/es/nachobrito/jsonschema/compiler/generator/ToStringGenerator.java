package es.nachobrito.jsonschema.compiler.generator;

import java.lang.classfile.ClassBuilder;
import java.lang.constant.*;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.Collectors;

import static java.lang.classfile.ClassFile.ACC_FINAL;
import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.constant.ClassDesc.of;
import static java.lang.constant.ConstantDescs.*;

record ToStringGenerator(ClassDesc classDesc, ClassBuilder classBuilder,
                         SortedMap<String, ClassDesc> properties) implements ModelGenerator {
    @Override
    public void generatePart() {
        //loosely based on:
        //https://github.com/openjdk/babylon/blob/490332b12e479d8a0c164cb32dab1def982d8fce/hat/hat/src/main/java/hat/ifacemapper/ByteCodeGenerator.java#L36
        var propertyNames = properties.keySet().stream().collect(Collectors.joining(";"));
        var getters = properties.entrySet().stream()
                .map(entry -> MethodHandleDesc.ofField(DirectMethodHandleDesc.Kind.GETTER, classDesc, entry.getKey(), entry.getValue()))
                .toList();
        var nonArrayGetters = properties.entrySet().stream()
                .filter(entry -> !entry.getValue().isArray())
                .map(entry -> MethodHandleDesc.ofField(DirectMethodHandleDesc.Kind.GETTER, classDesc, entry.getKey(), entry.getValue()))
                .toList();

        var recipe = properties.entrySet().stream()
                .map(entry -> entry.getValue().isArray() ?
                        String.format("%s=%s%s", entry.getKey(), entry.getValue().arrayType().displayName(), "[]") :
                        String.format("%s=\u0001", entry.getKey())

                )
                .collect(Collectors.joining(", ", classDesc.displayName() + "[", "]"));

        DirectMethodHandleDesc bootstrap = ofCallsiteBootstrap(
                of("java.lang.invoke.StringConcatFactory"),
                "makeConcatWithConstants",
                CD_CallSite,
                CD_String, CD_Object.arrayType()
        );

        List<ClassDesc> getDescriptions = properties.values().stream().filter(it -> !it.isArray()).toList();

        DynamicCallSiteDesc desc = DynamicCallSiteDesc.of(
                bootstrap,
                "toString",
                MethodTypeDesc.of(CD_String, getDescriptions), // String, g0, g1, ...
                recipe
        );

        classBuilder.withMethodBody("toString",
                MethodTypeDesc.of(CD_String),
                ACC_PUBLIC | ACC_FINAL,
                cob -> {
                    for (int i = 0; i < nonArrayGetters.size(); i++) {
                        var name = nonArrayGetters.get(i).methodName();
                        cob.aload(0);
                        // Method gi:()?
                        cob.invokevirtual(classDesc, name, MethodTypeDesc.of(getDescriptions.get(i)));
                    }
                    cob.invokedynamic(desc);
                    cob.areturn();
                });
    }
}

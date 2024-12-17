package es.nachobrito.jsonschema.compiler.generator;

import java.lang.classfile.ClassBuilder;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.SortedMap;

import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.constant.ClassDesc.of;
import static java.lang.constant.ConstantDescs.CD_void;
import static java.lang.constant.ConstantDescs.INIT_NAME;

record ConstructorGenerator(ClassDesc classDesc, ClassBuilder classBuilder, SortedMap<String, ClassDesc> properties) implements ModelGenerator{
    @Override
    public void generatePart() {
        var propertyTypes = properties.values().toArray(ClassDesc[]::new);

        classBuilder.withMethodBody(INIT_NAME, MethodTypeDesc.of(CD_void, propertyTypes), ACC_PUBLIC,
                builder -> {
                    //Invoke parent constructor
                    builder
                            .aload(0)
                            .invokespecial(of("java.lang.Record"), INIT_NAME, MethodTypeDesc.of(CD_void));
                    //Set properties:
                    int index = 1;
                    for (var entry : properties.entrySet()) {
                        builder
                                .aload(0)
                                .aload(index++)
                                .putfield(classDesc, entry.getKey(), entry.getValue());
                    }
                    builder.return_();
                }
        );
    }
}

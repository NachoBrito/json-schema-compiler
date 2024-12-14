package es.nachobrito.jsonschema.compiler.generator;

import java.lang.classfile.ClassBuilder;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.SortedMap;

import static java.lang.classfile.ClassFile.*;

public class PropertiesGenerator implements ModelGenerator{
    @Override
    public void generatePart(ClassDesc classDesc, ClassBuilder classBuilder, SortedMap<String, ClassDesc> properties) {
        properties.entrySet().forEach(entry -> buildProperty(classDesc, entry.getKey(), entry.getValue(), classBuilder));
    }


    private void buildProperty(ClassDesc className, String name, ClassDesc type, ClassBuilder classBuilder) {
        buildField(name, type, classBuilder);
        buildAccessor(className, name, type, classBuilder);
    }

    private void buildAccessor(ClassDesc classDesc, String name, ClassDesc type, ClassBuilder classBuilder) {
        classBuilder.withMethodBody(name, MethodTypeDesc.of(type), ACC_PUBLIC,
                builder -> builder
                        .aload(0)
                        .getfield(classDesc, name, type)
                        .areturn()
        );
    }

    private void buildField(String name, ClassDesc type, ClassBuilder classBuilder) {
        classBuilder.withField(name, type, ACC_PRIVATE | ACC_FINAL);
    }
}

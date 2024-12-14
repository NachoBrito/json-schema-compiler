package es.nachobrito.jsonschema.compiler.generator;

import java.lang.classfile.ClassBuilder;
import java.lang.constant.ClassDesc;
import java.util.SortedMap;

public interface ModelGenerator {

    void generatePart(ClassDesc classDesc, ClassBuilder classBuilder, SortedMap<String, ClassDesc> properties);
}

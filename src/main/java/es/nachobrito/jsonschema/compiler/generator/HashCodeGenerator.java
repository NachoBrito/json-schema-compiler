package es.nachobrito.jsonschema.compiler.generator;

import java.lang.classfile.ClassBuilder;
import java.lang.constant.ClassDesc;
import java.util.SortedMap;

record HashCodeGenerator(ClassDesc classDesc, ClassBuilder classBuilder, SortedMap<String, ClassDesc> properties)  implements ModelGenerator{
    @Override
    public void generatePart() {

    }
}

package es.nachobrito.jsonschema.compiler;

import static java.lang.constant.ConstantDescs.*;
import static java.util.stream.Collectors.toMap;

import io.zenwave360.jsonrefparser.$RefParser;
import io.zenwave360.jsonrefparser.$Refs;
import java.io.IOException;
import java.lang.constant.ClassDesc;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class JsonSchemaReader implements SchemaReader {
    final Map<String, Object> models;

    public JsonSchemaReader(URI uri) {
        this.models = loadModels(uri);
    }

    @Override
    public String getClassName() {
        return String.valueOf(models.getOrDefault("title", "UnknownClassName"));
    }

    @Override
    public SortedMap<String, ClassDesc> getProperties() {
        var definitions = ((Map<String, Map<String, ?>>) models.getOrDefault("properties", Collections.emptyMap()));
        return definitions
                .entrySet()
                .stream()
                .collect(toMap(
                        entry -> entry.getKey(),
                        entry -> getJavaType((String) entry.getValue().get("type")),
                        (v1, v2) -> {
                            throw new CompilerException("Duplicate property found!");
                        },
                        TreeMap::new
                ))
                ;
    }

    private ClassDesc getJavaType(String jsonSchemaType) {
        return switch (jsonSchemaType) {
            case "string" -> CD_String;
            case "integer" -> CD_Integer;
            default -> CD_Object;
        };
    }

    private static Map<String, Object> loadModels(URI uri) {
        try {
            $RefParser parser = new $RefParser(uri);
            $Refs refs = null;
            refs = parser.parse().dereference().mergeAllOf().getRefs();
            return refs.schema();

        } catch (IOException e) {
            throw new CompilerException(e);
        }
    }
}

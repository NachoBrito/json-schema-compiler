package es.nachobrito.jsonschema.compiler;

import java.lang.constant.ClassDesc;
import java.net.URI;
import java.util.SortedMap;

public interface SchemaReader {

    String getClassName();

    SortedMap<String, ClassDesc> getProperties();

     static SchemaReader of(URI uri)
    {
        return new JsonSchemaReader(uri);
    }
}

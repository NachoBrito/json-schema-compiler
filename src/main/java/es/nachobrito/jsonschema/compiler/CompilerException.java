package es.nachobrito.jsonschema.compiler;

import java.io.IOException;

public class CompilerException extends RuntimeException{
    public CompilerException(IOException e) {
        super(e);
    }

    public CompilerException(String message) {
        super(message);
    }
}

# JSON Schema Compiler

> ⚠️ WARNING! WORK IN PROGRESS: This project is in an early stage and should not be considered production-ready. Use it at your
> own risk.

When you need to integrate JSON-Schema models into JVM-based applications, you can use several tools to translate it
Java, or other supported language, and then compile it along with your application.

This approach works for many use cases, but requires a complete development kit and takes an indirect path as you need
translate one language to another, and then use a compiler to create your executable.

`json-schema-compiler`, as it's name implies, **compiles JSON-Schema code directly to bytecode**. This removes the need
to have a development kit available, and reduces the complexity to integrate such schemas into JVM-based applications.

## How to use

`json-schema-compiler` is a command line utility. The general syntax is:

```bash
json-schema-compiler -p [package] -o [output]
```

Supported parameters:

| parameter               | default value | description                                    |
|-------------------------|---------------|------------------------------------------------|
| `-p` , `--package-name` | empty         | The package where generated classes should be. |
| `-o` , `--output`       | "."           | The output folder for the generated classes.   |

### 1. Download your specific image

Everytime the code is updated a new native image is generated for Windows, Linux and MacOS
using [GraalVM's Native Image](https://www.graalvm.org/latest/reference-manual/native-image/). You can find the image
for your operating system under the `[OS]-current` tag:

- [Download Ubuntu image](https://github.com/NachoBrito/json-schema-compiler/raw/refs/tags/ubuntu-latest-current/bin/ubuntu-latest/json-schema-compiler).
- [Download MacOS image](https://github.com/NachoBrito/json-schema-compiler/raw/refs/tags/macos-latest-current/bin/macos-latest/json-schema-compiler).
- [Download Windows image](https://github.com/NachoBrito/json-schema-compiler/raw/refs/tags/windows-latest-current/bin/windows-latest/json-schema-compiler).

Once downloaded, you can run it directly from your current folder, or add it to your systems `$PATH` so that it can be
launched from anywhere.

### 2. Compile json schema files

If you are using `json-schema-compiler` in a UNIX-like environment, you can pass the JSON code trough standard input:

```bash
cat /my/json-schema/file.json | json-schema-compiler -p com.example -o ./output
```

You can also pass the json schema's path as an argument:

```bash
json-schema-compiler -p com.example -o ./output /my/json-schema/file.json
```


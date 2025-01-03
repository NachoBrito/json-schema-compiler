# JSON Schema Compiler

> ⚠️ WARNING! WORK IN PROGRESS: This project is in an early stage and should not be considered production-ready. Use it
> at your
> own risk.

`json-schema-compiler`, **compiles JSON-Schema code directly to bytecode**, in both in `.class` files and `.jar`
libraries, eliminating the need to translate the schema to other programming language and then compiling it.

This makes the process faster, removes the need of a development kit available, and reduces the complexity to
integrate such schemas into JVM-based applications.

## How to compile JSON Schema files to bytecode

`json-schema-compiler` is a command line utility. The general syntax is:

```bash
json-schema-compiler -p [package] -o [output]
```

Supported parameters:

| parameter               | default value | description                                                                           |
|-------------------------|---------------|---------------------------------------------------------------------------------------|
| `-p` , `--package-name` | empty         | The package where generated classes should be.                                        |
| `-o` , `--output`       | "."           | The output folder for the generated classes, or the path of the jar file to generate. |

### 1. Download your specific image, or generate one

Everytime the code is updated a new native image is generated for Windows, Linux and MacOS
using [GraalVM's Native Image](https://www.graalvm.org/latest/reference-manual/native-image/). You can find the image
for your operating system under the `[OS]-current` tag:

- [Download Ubuntu image](https://github.com/NachoBrito/json-schema-compiler/raw/refs/tags/ubuntu-latest-current/bin/ubuntu-latest/json-schema-compiler).
- [Download MacOS image](https://github.com/NachoBrito/json-schema-compiler/raw/refs/tags/macos-latest-current/bin/macos-latest/json-schema-compiler).
- [Download Windows image](https://github.com/NachoBrito/json-schema-compiler/raw/refs/tags/windows-latest-current/bin/windows-latest/json-schema-compiler).

Once downloaded, you can run it directly from your current folder, or add it to your systems `$PATH` so that it can be
launched from anywhere.

If your system is not listed here, you can generate your own image by:

1. Installing the [GraalVM JDK](https://www.graalvm.org/downloads/)
2. Cloning this repository a
3. Running the [create_image.sh](./create-image.sh) script

The script will build and image for your current operating system and architecture as  `target/json-schema-compiler`.

### 2. Compile json schema files

If you are using `json-schema-compiler` in a UNIX-like environment, you can pass the JSON code trough standard input:

```bash
# compile as .class files in the ./output folder:
cat /my/json-schema/file.json | json-schema-compiler -p com.example -o ./output
```

```bash
# compile as ./library.jar:
cat /my/json-schema/file.json | json-schema-compiler -p com.example -o ./library.jar
```

You can also pass the json schema's path as an argument:

```bash
json-schema-compiler -p com.example -o ./library.jar /my/json-schema/file.json
```

## How it works

This compiler makes heavy use of the [Java Class-File API](https://openjdk.org/jeps/484) to generate bytecode directly
at runtime. You have more details about this project
in [this article I wrote](https://www.nachobrito.es/sideprojects/json-schema-compiler/). 
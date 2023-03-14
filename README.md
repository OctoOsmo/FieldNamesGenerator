## FieldNamesGenerator

This project provides a Java annotation processor that generates static list of field names for annotated classes.

### Usage

To use this annotation processor in your project, follow these steps:

* Add the org.octoosmo:FieldNamesGenerator dependency to your build file. If you use gradle include it both as an implementation and as annotationProcessor.
* Create a new class and annotate it with @GenerateFieldNames.
* Build your project.

After building your project, a new file named {ClassName}FieldNames will be generated for each class annotated with @FieldName. 

This file will contain a static set named FIELD_NAMES that lists all of the field names for the annotated class.

### Example

Here's an example of how to use this annotation processor:
```Java
import com.example.FieldName;

@GenerateFieldNames
public class ExampleClass {
    private final String field1 = "value1";
    private final int field2 = 42;
}
```

```Java
package com.example;

import java.util.List;

public class ExampleClassFieldNames {
    public static final List<String> FIELD_NAMES = List.of(
        "field1",
        "field2"
    );
}
```

### Authors
90% of this code was written by ChatGPT.

90% of this README.md was also written by ChatGPT.

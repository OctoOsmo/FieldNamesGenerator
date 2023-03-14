package org.octoosmo;

import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class FieldNamesProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(GenerateFieldNames.class.getName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (element.getKind() == ElementKind.CLASS) {
                    String className = element.getSimpleName().toString();
                    String packageName = processingEnv.getElementUtils().getPackageOf(element).toString();
                    List<String> fieldNames = element.getEnclosedElements().stream()
                            .filter(e -> e.getKind() == ElementKind.FIELD)
                            .map(e -> "\"" + e.getSimpleName() + "\"")
                            .collect(Collectors.toList());
                    String fileContent = String.format("""
                                    package %s;
                                    import java.util.List;

                                    public class %sFieldNames {
                                        public static final List<String> FIELD_NAMES = List.of(
                                            %s
                                        );
                                    }""",
                            packageName, className, String.join(",\n        ", fieldNames));
                    String fileName = className + "FieldNames";
                    try {
                        JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(packageName + "." + fileName);
                        try (Writer writer = fileObject.openWriter()) {
                            writer.write(fileContent);
                        }
                    } catch (IOException e) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to write file: " + e.getMessage());
                    }
                }
            }
        }
        return true;
    }
}


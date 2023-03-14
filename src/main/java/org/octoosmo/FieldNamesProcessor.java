package org.octoosmo;

import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Collections;
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
        for (var annotation : annotations) {
            for (var element : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (element.getKind() == ElementKind.CLASS) {
                    var className = element.getSimpleName().toString();
                    var packageName = processingEnv.getElementUtils().getPackageOf(element).toString();
                    var fieldNames = element.getEnclosedElements().stream()
                            .filter(e -> e.getKind() == ElementKind.FIELD)
                            .map(e -> "\"" + e.getSimpleName() + "\"")
                            .collect(Collectors.toList());
                    var fileContent = String.format("""
                                package %s;
                                import java.util.List;

                                public class %sFieldNames {
                                    public static final List<String> FIELD_NAMES = List.of(
                                        %s
                                    );
                                }""",
                            packageName, className, String.join(",\n        ", fieldNames));
                    var fileName = className + "FieldNames";
                    try {
                        var fileObject = processingEnv.getFiler().createSourceFile(packageName + "." + fileName);
                        try (var writer = fileObject.openWriter()) {
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


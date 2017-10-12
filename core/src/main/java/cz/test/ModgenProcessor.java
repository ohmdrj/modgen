package cz.test;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

@SupportedAnnotationTypes({"cz.test.Modgen"})
public class ModgenProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> ans, RoundEnvironment env) {
        Messager messager = processingEnv.getMessager();
        messager.printMessage(Diagnostic.Kind.NOTE, "*** Modgen ***");
        for (TypeElement type : ans) {
            for (Element elem : env.getElementsAnnotatedWith(type)) {
                if (elem.getKind().isClass()) {
                    ModgenTemplate temp = new ModgenTemplate();
                    String name = ((TypeElement) elem).getQualifiedName().toString();
                    String suff = "G";
                    temp.pkg = ModgenUtils.simplePackageOf(name);
                    temp.base = ModgenUtils.simpleNameOf(name);
                    temp.synth = temp.base + suff;
                    for (ExecutableElement ee : ElementFilter.methodsIn(elem.getEnclosedElements())) {
                        temp.props.add(new ModgenProperty(ee));
                    }
                    System.out.println("* " + elem.toString());
                    writeSourceFile(name + suff, temp.toString(), type);

                } else {
                    System.out.println("! Zatim nepodporujeme " + elem.getSimpleName());
                }
            }
        }
        return true;
    }

    private void writeSourceFile(String className, String text, TypeElement originatingType) {
        try {
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(className, originatingType);
            Writer writer = sourceFile.openWriter();
            try {
                writer.write(text);
            } finally {
                writer.close();
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "Could not write generated class " + className + ": " + e);
        }
    }
}

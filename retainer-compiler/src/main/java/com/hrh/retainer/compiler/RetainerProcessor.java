package com.hrh.retainer.compiler;

import com.google.auto.service.AutoService;
import com.hrh.retainer.Retain;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static com.hrh.retainer.compiler.Utils.*;

@AutoService(Processor.class)
public class RetainerProcessor extends AbstractProcessor {

    private static final String GENERATED_CLASS_SUFFIX = "Retainer";

    //Methods
    private static final String METHOD_RESTORE = "restore";
    private static final String METHOD_RETAIN = "retain";

    //Types
    private static final ClassName TYPE_FRAGMENT_MANAGER = ClassName.get("android.support.v4.app", "FragmentManager");
    private static final ClassName TYPE_RETAINED_FIELDS_MAP_HOLDER = ClassName.get("com.hrh.retainer", "RetainedFieldsMapHolder");

    //Parameters
    private static final String PAR_TARGET = "target";
    private static final String PAR_FRAGMENT_MANAGER = "manager";

    //Vars
    private static final String VAR_HOLDER = "holder";

    private Logger mLogger;

    @SuppressWarnings("unchecked")
    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        return toAnnotationSet(Retain.class);
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment)
    {
        super.init(processingEnvironment);
        mLogger = new Logger(processingEnvironment);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        Element[] elements = extractAnnotatedElements(roundEnv, Retain.class);
        if (elements.length == 0)
        {
            return true;
        }
        Map<String, List<Element>> classNameToElements = mapElementsToClasses(elements);
        for (String className : classNameToElements.keySet())
        {
            //Generate retainer class for each class with retained elements
            List<Element> enclosedElements = classNameToElements.get(className);
            Element superClassElement = getSuperClassElement(enclosedElements.get(0));
            boolean hasRetainerSuperClass = classNameToElements.containsKey(superClassElement.getSimpleName().toString());
            createRetainerForClass(hasRetainerSuperClass ? superClassElement : null, enclosedElements);
        }
        return true;
    }

    private void createRetainerForClass(Element superClassElement, List<Element> enclosedElements)
    {
        Element classElement = enclosedElements.get(0).getEnclosingElement();
        ClassName className = elementToClassName(processingEnv, classElement);

        //Add methods
        List<MethodSpec> methodSpecList = new ArrayList<>();
        methodSpecList.add(restoreMethod(className, superClassElement != null, enclosedElements));
        methodSpecList.add(retainMethod(className, superClassElement != null, enclosedElements));

        //Generate class
        generateRetainerClass(className, classElement, superClassElement, methodSpecList);
    }


    private MethodSpec retainMethod(ClassName className, boolean hasSuper, List<Element> elements)
    {
        MethodSpec.Builder retainBuilder = retainerMethodBuilder(METHOD_RETAIN, hasSuper, className)
                .addStatement(String.format("RetainedFieldsMapHolder %s = " +
                                "(RetainedFieldsMapHolder) %s.findFragmentByTag(%s.getClass().getName())",
                        VAR_HOLDER, PAR_FRAGMENT_MANAGER, PAR_TARGET))
                .beginControlFlow(String.format("if (%s!=null)", VAR_HOLDER));
        for (Element element : elements)
        {
            retainBuilder.addStatement(String.format("%s.getMap().put(\"%s\", target.%s)",
                    VAR_HOLDER, element.getSimpleName(), element.getSimpleName()));
        }
        retainBuilder.endControlFlow();
        return retainBuilder.build();
    }

    private MethodSpec restoreMethod(ClassName className, boolean hasSuper, List<Element> elements)
    {
        MethodSpec.Builder retainBuilder = retainerMethodBuilder(METHOD_RESTORE, hasSuper, className)
                .addStatement(String.format("RetainedFieldsMapHolder %s = " +
                        "($T) %s.findFragmentByTag(%s.getClass().getName())", VAR_HOLDER, PAR_FRAGMENT_MANAGER, PAR_TARGET), TYPE_RETAINED_FIELDS_MAP_HOLDER)
                .beginControlFlow(String.format("if (%s == null)", VAR_HOLDER))
                .addCode("//Nothing to restore, just add holder for next time\n")
                .addStatement(String.format("%s = new RetainedFieldsMapHolder()", VAR_HOLDER))
                .addStatement(String.format("%s.beginTransaction().add(%s, target.getClass().getName()).commitNow()", PAR_FRAGMENT_MANAGER, VAR_HOLDER))
                .endControlFlow()
                .beginControlFlow(String.format("else if (!%s.getMap().isEmpty())", VAR_HOLDER))
                .addCode("//Restore all fields from mapping\n");
        for (Element element : elements)
        {
            retainBuilder.addStatement(String.format("%s.%s = ($T) %s.getMap().get(\"%s\")",
                    PAR_TARGET, element.getSimpleName(), VAR_HOLDER, element.getSimpleName()), element.asType());
        }
        retainBuilder.endControlFlow();
        return retainBuilder.build();
    }

    private MethodSpec.Builder retainerMethodBuilder(String name, boolean hasSuper, ClassName className)
    {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(Override.class)
                .returns(TypeName.VOID)
                .addParameter(className, PAR_TARGET)
                .addParameter(TYPE_FRAGMENT_MANAGER, PAR_FRAGMENT_MANAGER);
        if (hasSuper)
        {
            builder.addStatement(String.format("super.%s(%s,%s)", name, PAR_TARGET, PAR_FRAGMENT_MANAGER));
        }
        return builder;
    }

    private void generateRetainerClass(ClassName className, Element classElement,
                                       Element superClassElement, List<MethodSpec> methodSpecs)
    {
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(getGeneratedClassName(
                className.simpleName()))
                .addModifiers(Modifier.PUBLIC);
        //Add generic type
        String genericType = "T";
        typeSpecBuilder.addTypeVariable(TypeVariableName.get(genericType, className));

        //Define inheritance, if target don't have a retained super class just implement
        //interface, otherwise extend superclass retainer
        if (superClassElement != null)
        {
            typeSpecBuilder.superclass(ParameterizedTypeName.get(
                    elementToGenClassName(processingEnv, superClassElement, GENERATED_CLASS_SUFFIX),
                    TypeVariableName.get(genericType)));
        }
        else
        {
            typeSpecBuilder.addSuperinterface(ParameterizedTypeName.get(
                    ClassName.get("com.hrh.retainer", "IClassRetainer"), TypeVariableName.get(genericType)));
        }

        //Add methods
        typeSpecBuilder.addMethods(methodSpecs).build();

        //Write to java generated source file
        if (!writeClassToFile(processingEnv, className.packageName(), typeSpecBuilder.build()))
        {
            mLogger.e(classElement, "Unable to write retainer for class: %s", classElement.getSimpleName().toString());
        }
    }

    private String getGeneratedClassName(String forClass)
    {
        return Utils.getGeneratedClassName(forClass, GENERATED_CLASS_SUFFIX);
    }


}

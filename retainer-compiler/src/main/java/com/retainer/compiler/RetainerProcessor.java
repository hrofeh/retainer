package com.retainer.compiler;

import com.google.auto.service.AutoService;
import com.retainer.Retain;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
public class RetainerProcessor extends AbstractProcessor {

    private static final String GEN_CLASS_SUFFIX = "Retainer";

    //Methods
    private static final String METHOD_RESTORE = "restore";
    private static final String METHOD_RETAIN = "retain";

    //Types
    private static final ClassName TYPE_FRAGMENT_MANAGER = ClassName.get("android.support.v4.app", "FragmentManager");
    private static final ClassName TYPE_RETAINED_FIELDS_MAP_HOLDER = ClassName.get("com.retainer", "RetainedFieldsMapHolder");

    //Parameters
    private static final String PAR_TARGET = "target";
    private static final String PAR_FRAGMENT_MANAGER = "manager";

    //Vars
    private static final String VAR_HOLDER = "holder";

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        Set<String> types = new LinkedHashSet<>();
        types.add(Retain.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        Element[] elements = roundEnv.getElementsAnnotatedWith(Retain.class)
                .toArray(new Element[0]);
        if (elements.length == 0)
        {
            return true;
        }
        String packageName = processingEnv.getElementUtils().getPackageOf(elements[0]).getQualifiedName().toString();
        Map<String, List<Element>> classNameToElements = mapElementsToClasses(elements);
        for (String className : classNameToElements.keySet())
        {
            createRetainerForClass(packageName, className, classNameToElements.get(className));
        }
        return true;
    }

    private Map<String, List<Element>> mapElementsToClasses(Element[] elements)
    {
        Map<String, List<Element>> classNameToElements = new HashMap<>();
        for (Element element : elements)
        {
            String className = element.getEnclosingElement().getSimpleName().toString();
            List<Element> classElements = classNameToElements.get(className);
            if (classElements == null)
            {
                classElements = new ArrayList<>();
                classNameToElements.put(className, classElements);
            }
            classElements.add(element);
        }
        return classNameToElements;
    }


    private void createRetainerForClass(String packageName, String className, List<Element> elements)
    {
        String generatedClassName = className + GEN_CLASS_SUFFIX;
        List<MethodSpec> methodSpecList = new ArrayList<>();
        addRestoreMethod(packageName, className, methodSpecList, elements);
        addRetainMethod(packageName, className, methodSpecList, elements);
        addHolderGetterMethod(methodSpecList);
        generateClass(packageName, generatedClassName, methodSpecList);
    }


    private void addRetainMethod(String packageName, String className, List<MethodSpec> methodSpecList, List<Element> elements)
    {
        MethodSpec.Builder retainBuilder = retainerMethod(METHOD_RETAIN, packageName, className)
                .addStatement(String.format("RetainedFieldsMapHolder %s = getHolder(%s,%s)",
                        VAR_HOLDER, PAR_TARGET, PAR_FRAGMENT_MANAGER))
                .beginControlFlow(String.format("if (%s!=null)", VAR_HOLDER));
        for (Element element : elements)
        {
            retainBuilder.addStatement(String.format("%s.getMap().put(\"%s\", target.%s)",
                    VAR_HOLDER, element.getSimpleName(), element.getSimpleName()));
        }
        retainBuilder.endControlFlow();
        methodSpecList.add(retainBuilder.build());
    }

    private void addHolderGetterMethod(List<MethodSpec> methodSpecList)
    {
        methodSpecList.add(MethodSpec.methodBuilder("getHolder")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .returns(TYPE_RETAINED_FIELDS_MAP_HOLDER)
                .addParameter(TypeName.OBJECT, PAR_TARGET)
                .addParameter(TYPE_FRAGMENT_MANAGER, PAR_FRAGMENT_MANAGER)
                .addStatement(String.format("return (RetainedFieldsMapHolder) %s.findFragmentByTag(%s.getClass().getName())", PAR_FRAGMENT_MANAGER, PAR_TARGET))
                .build());
    }

    private void addRestoreMethod(String packageName, String className, List<MethodSpec> methodSpecList, List<Element> elements)
    {
        MethodSpec.Builder retainBuilder = retainerMethod(METHOD_RESTORE, packageName, className)
                .addStatement(String.format("RetainedFieldsMapHolder %s = getHolder(%s,%s)", VAR_HOLDER, PAR_TARGET, PAR_FRAGMENT_MANAGER))
                .beginControlFlow(String.format("if (%s == null)", VAR_HOLDER))
                .addComment("Nothing to restore, just add holder for next time")
                .addStatement(String.format("%s = new RetainedFieldsMapHolder()", VAR_HOLDER))
                .addStatement(String.format("%s.beginTransaction().add(%s, target.getClass().getName()).commitNow()", PAR_FRAGMENT_MANAGER, VAR_HOLDER))
                .endControlFlow()
                .beginControlFlow("else")
                .addComment("Restore all fields from mapping");
        for (Element element : elements)
        {
            retainBuilder.addStatement(String.format("%s.%s = (%s) %s.getMap().get(\"%s\")",
                    PAR_TARGET, element.getSimpleName(), element.asType(), VAR_HOLDER, element.getSimpleName()));
        }
        retainBuilder.endControlFlow();
        methodSpecList.add(retainBuilder.build());
    }

    private MethodSpec.Builder retainerMethod(String name, String packageName, String className)
    {
        return MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.VOID)
                .addParameter(ClassName.get(packageName, className), PAR_TARGET)
                .addParameter(TYPE_FRAGMENT_MANAGER, PAR_FRAGMENT_MANAGER);
    }

    private void generateClass(String packageName, String className, List<MethodSpec> methodSpecs)
    {
        TypeSpec typeSpec = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethods(methodSpecs).build();
        try
        {
            JavaFile.builder(packageName, typeSpec).build().writeTo(processingEnv.getFiler());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

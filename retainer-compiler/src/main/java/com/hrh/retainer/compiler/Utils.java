package com.hrh.retainer.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

/**
 * Created by Hanan on 2/13/2017.
 */

public class Utils {


    static <T extends Annotation> Set<String> toAnnotationSet(Class<T>... vars)
    {
        Set<String> set = new LinkedHashSet<>();
        for (Class<T> var : vars)
        {
            set.add(var.getCanonicalName());
        }
        return set;
    }

    static Map<String, List<Element>> mapElementsToClasses(Element[] elements)
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

    static Element[] extractAnnotatedElements(RoundEnvironment roundEnv, Class<? extends Annotation> annotationClass)
    {
        Set<? extends Element> elementSet = roundEnv.getElementsAnnotatedWith(annotationClass);
        return elementSet.toArray(new Element[elementSet.size()]);
    }

    static String getPackage(ProcessingEnvironment pe, Element element)
    {
        return pe.getElementUtils().getPackageOf(element).getQualifiedName().toString();
    }

    static String getGeneratedClassName(String className, String suffix)
    {
        return className + suffix;
    }

    static Element getSuperClassElement(Element classElement)
    {
        return ((DeclaredType) ((TypeElement) classElement.getEnclosingElement()).getSuperclass()).asElement();
    }

    static ClassName elementToClassName(ProcessingEnvironment pe, Element element)
    {
        return ClassName.get(getPackage(pe, element), element.getSimpleName().toString());
    }

    static ClassName elementToGenClassName(ProcessingEnvironment pe, Element element, String suffix)
    {
        return ClassName.get(getPackage(pe, element), getGeneratedClassName(element.getSimpleName().toString(), suffix));
    }

    static boolean writeClassToFile(ProcessingEnvironment env, String packageName, TypeSpec typeSpec)
    {
        try
        {
            JavaFile.builder(packageName, typeSpec).build().writeTo(env.getFiler());
            return true;
        }
        catch (IOException e)
        {
            return false;
        }
    }
}

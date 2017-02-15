package com.hananrh.retainer.compiler.enforcers;

import com.hananrh.retainer.compiler.Logger;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;

/**
 * Created by Hanan on 2/13/2017.
 */

public class EnclosingClassEnforcer extends AbsEnforcer {

    public EnclosingClassEnforcer(Logger logger)
    {
        super(logger);
    }

    @Override
    public boolean enforce(Class<? extends Annotation> annotation, Element element)
    {
        Element classElement = element.getEnclosingElement();
        if (classElement.getKind() != ElementKind.CLASS ||
                classElement.getModifiers().contains(Modifier.PRIVATE))
        {
            mLogger.e(element, "@%s field %s.%s must be contained in a non-private class",
                    annotation.getSimpleName(), classElement.getSimpleName(), element.getSimpleName());
            return false;
        }
        return true;
    }
}

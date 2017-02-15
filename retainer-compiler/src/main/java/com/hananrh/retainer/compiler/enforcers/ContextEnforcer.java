package com.hananrh.retainer.compiler.enforcers;

import com.hananrh.retainer.compiler.Logger;
import com.hananrh.retainer.compiler.Utils;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;

/**
 * Created by Hanan on 2/13/2017.
 */

public class ContextEnforcer extends AbsEnforcer {

    public ContextEnforcer(Logger logger)
    {
        super(logger);
    }

    @Override
    public boolean enforce(Class<? extends Annotation> annotation, Element element)
    {
        return enforce(annotation, element, element);
    }

    private boolean enforce(Class<? extends Annotation> annotation, Element orgElement, Element element)
    {
        String className = element.getSimpleName().toString();
        if (element.asType().getKind().isPrimitive() || className.equals(Object.class.getSimpleName()))
        {
            return true;
        }
        if (className.equals("Context"))
        {
            mLogger.e(orgElement, "@%s field %s.%s cannot be an android context",
                    annotation.getSimpleName(), orgElement.getEnclosingElement().getSimpleName(), orgElement.getSimpleName());
            return false;
        }
        return enforce(annotation, orgElement, Utils.getSuperClassElement(
                ((DeclaredType) element.asType()).asElement()));
    }
}

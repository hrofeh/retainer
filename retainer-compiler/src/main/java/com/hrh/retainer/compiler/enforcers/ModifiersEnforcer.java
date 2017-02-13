package com.hrh.retainer.compiler.enforcers;

import com.hrh.retainer.compiler.Logger;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

/**
 * Created by Hanan on 2/13/2017.
 */

public class ModifiersEnforcer extends AbsEnforcer {

    public ModifiersEnforcer(Logger logger)
    {
        super(logger);
    }

    @Override
    public boolean enforce(Class<? extends Annotation> annotation, Element element)
    {
        Set<Modifier> modifierList = element.getModifiers();
        for (Modifier modifier : modifierList)
        {
            if (modifier == Modifier.PRIVATE || modifier == Modifier.STATIC)
            {
                mLogger.e(element, "@%s field %s.%s cannot be defined private or static",
                        annotation.getSimpleName(), element.getEnclosingElement().getSimpleName(), element.getSimpleName());
                return false;
            }
        }
        return true;
    }
}

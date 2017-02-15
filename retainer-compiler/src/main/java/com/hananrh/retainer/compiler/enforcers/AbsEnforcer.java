package com.hananrh.retainer.compiler.enforcers;

import com.hananrh.retainer.compiler.Logger;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;

/**
 * Created by Hanan on 2/13/2017.
 */

public abstract class AbsEnforcer {

    protected Logger mLogger;

    public AbsEnforcer(Logger logger)
    {
        mLogger = logger;
    }

    public abstract boolean enforce(Class<? extends Annotation> annotation, Element element);
}

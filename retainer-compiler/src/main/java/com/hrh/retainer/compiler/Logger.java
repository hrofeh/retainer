package com.hrh.retainer.compiler;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * Created by Hanan on 2/13/2017.
 */

public class Logger {

    private ProcessingEnvironment mEnv;

    Logger(ProcessingEnvironment processingEnvironment)
    {
        mEnv = processingEnvironment;
    }

    void e(Element element, String message, Object... args)
    {
        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }

    void d(Element element, String message, Object... args)
    {
        printMessage(Diagnostic.Kind.NOTE, element, message, args);
    }

    void printMessage(Diagnostic.Kind kind, Element element, String message, Object[] args)
    {
        if (args.length > 0)
        {
            message = String.format(message, args);
        }

        mEnv.getMessager().printMessage(kind, message, element);
    }
}

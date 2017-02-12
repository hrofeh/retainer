package com.hrh.retainer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hanan on 2/7/2017.
 */

public class Retainer {

    private static Map<String, IClassRetainer<?>> mClassNameToRetainer = new HashMap<>();

    public static void restore(FragmentActivity target)
    {
        restore(target, target.getSupportFragmentManager());
    }

    public static void restore(Fragment target)
    {
        restore(target, target.getFragmentManager());
    }

    public static <T> void restore(T target, FragmentManager manager)
    {
        IClassRetainer<T> retainerForClass = findOrCreateRetainerForClass(target);
        if (retainerForClass != null)
        {
            retainerForClass.restore(target, manager);
        }
    }

    public static void retain(FragmentActivity target)
    {
        retain(target, target.getSupportFragmentManager());
    }

    public static void retain(Fragment target)
    {
        retain(target, target.getFragmentManager());
    }

    public static <T> void retain(T target, FragmentManager manager)
    {
        IClassRetainer<T> retainerForClass = findOrCreateRetainerForClass(target);
        if (retainerForClass != null)
        {
            retainerForClass.retain(target, manager);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> IClassRetainer<T> findOrCreateRetainerForClass(T target)
    {
        String retainerClassName = target.getClass().getSimpleName() + "Retainer";
        try
        {
            IClassRetainer<T> retainer = (IClassRetainer<T>) mClassNameToRetainer.get(retainerClassName);
            if (retainer == null)
            {
                retainer = (IClassRetainer<T>) Class.forName(String.format("%s.%s",
                        target.getClass().getPackage().getName(), retainerClassName)).newInstance();
                mClassNameToRetainer.put(retainerClassName, retainer);
            }
            return retainer;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}

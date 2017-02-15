package com.hananrh.retainer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hanan on 2/7/2017.
 */

public final class Retainer {

    private static final String LOG_TAG = "Retainer";

    private static final Map<Class<?>, IClassRetainer<?>> RETAINERS = new HashMap<>();

    private static boolean msLogsEnabled = false;

    private Retainer()
    {
    }

    public static void setLogsEnabled(boolean logsEnabled)
    {
        msLogsEnabled = logsEnabled;
    }

    public static void restore(@NonNull FragmentActivity target)
    {
        restore(target, target.getSupportFragmentManager());
    }

    public static void restore(@NonNull Fragment target)
    {
        restore(target, target.getFragmentManager());
    }

    public static <T> void restore(@NonNull T target, @NonNull FragmentManager manager)
    {
        IClassRetainer<T> retainerForClass = findOrCreateRetainerForClass(target);
        if (retainerForClass != null)
        {
            retainerForClass.restore(target, manager);
        }
    }

    public static void retain(@NonNull FragmentActivity target)
    {
        retain(target, target.getSupportFragmentManager());
    }

    public static void retain(@NonNull Fragment target)
    {
        retain(target, target.getFragmentManager());
    }

    public static <T> void retain(@NonNull T target, @NonNull FragmentManager manager)
    {
        IClassRetainer<T> retainerForClass = findOrCreateRetainerForClass(target);
        if (retainerForClass != null)
        {
            retainerForClass.retain(target, manager);
        }
    }

    @Nullable
    private static <T> IClassRetainer<T> findOrCreateRetainerForClass(@NonNull T target)
    {
        Class<?> targetClass = target.getClass();
        log("Looking for class constructor for: " + targetClass.getSimpleName());
        IClassRetainer<T> retainer = findOrCreateRetainerForClass(targetClass, targetClass);
        if (retainer == null)
        {
            throw new RuntimeException("Unable to find retainer for " + targetClass.getSimpleName());
        }
        return retainer;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static <T> IClassRetainer<T> findOrCreateRetainerForClass(Class<?> originClass, Class<?> cls)
    {
        IClassRetainer retainer = RETAINERS.get(cls);
        if (retainer != null)
        {
            log("Found cached retainer for: " + cls.getSimpleName());
            return retainer;
        }
        String clsName = cls.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java."))
        {
            log("Reached non-user class, stopping retainer search for: " + originClass.getSimpleName());
            return null;
        }
        try
        {
            Class<?> retainerClass = Class.forName(clsName + "Retainer");
            retainer = constructRetainer(retainerClass.getConstructor());
            log("Found and constructed retainer for: " + cls.getSimpleName());
        }
        catch (ClassNotFoundException e)
        {
            log("Retainer not found for: " + clsName + ", proceeding to superclass " + cls.getSuperclass().getName());
            retainer = findOrCreateRetainerForClass(originClass, cls.getSuperclass());
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException("Unable to find retainer constructor for " + clsName, e);
        }
        RETAINERS.put(cls, retainer);
        return retainer;
    }

    @SuppressWarnings("TryWithIdenticalCatches")
    private static <T> IClassRetainer<T> constructRetainer(Constructor<?> constructor)
    {
        try
        {
            return (IClassRetainer<T>) constructor.newInstance();
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        }
        catch (InvocationTargetException e)
        {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException)
            {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error)
            {
                throw (Error) cause;
            }
            throw new RuntimeException("Unable to create retainer", cause);
        }
    }

    private static void log(String msg)
    {
        if (msLogsEnabled)
        {
            Log.d(LOG_TAG, msg);
        }
    }
}

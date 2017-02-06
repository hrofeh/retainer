package com.example.hanan.retainhack.retainer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hanan on 2/6/2017.
 */

public class Retainer {

    public static void restore(FragmentActivity activity)
    {
        restore(activity, activity.getSupportFragmentManager());
    }

    public static void restore(Fragment fragment)
    {
        restore(fragment, fragment.getChildFragmentManager());
    }

    public static void restore(Object target, FragmentManager manager)
    {
        try
        {
            RetainedObjectHolder<Map<String, Object>> retainedFieldsHolder = (RetainedObjectHolder) manager.findFragmentByTag(target.getClass().getName());
            if (retainedFieldsHolder != null)
            {
                for (Field field : target.getClass().getDeclaredFields())
                {
                    if (field.getAnnotation(Retain.class) != null)
                    {
                        field.setAccessible(true);
                        field.set(target, retainedFieldsHolder.getRetainedObject().get(field.getName()));
                    }
                }
            }
        }
        catch (IllegalAccessException e)
        {
            throw new RetainerException(e);
        }
    }

    public static void retain(FragmentActivity activity)
    {
        retain(activity, activity.getSupportFragmentManager());
    }

    public static void retain(Fragment fragment)
    {
        retain(fragment, fragment.getChildFragmentManager());
    }

    public static void retain(Object target, FragmentManager manager)
    {
        try
        {
            Map<String, Object> fieldsValues = new HashMap<>();
            for (Field field : target.getClass().getDeclaredFields())
            {
                if (field.getAnnotation(Retain.class) != null)
                {
                    field.setAccessible(true);
                    fieldsValues.put(field.getName(), field.get(target));
                }
                manager.beginTransaction()
                        .add(new RetainedObjectHolder<>(fieldsValues), target.getClass().getName())
                        .commitNow();
            }
        }
        catch (IllegalAccessException e)
        {
            throw new RetainerException(e);
        }
    }
}

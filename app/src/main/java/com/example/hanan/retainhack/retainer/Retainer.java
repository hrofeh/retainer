package com.example.hanan.retainhack.retainer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.lang.reflect.Field;

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
            RetainedFieldsMapHolder retainedFieldsHolder = getHolder(target, manager);
            if (retainedFieldsHolder == null)
            {
                //Nothing to restore, just add holder for next time
                retainedFieldsHolder = new RetainedFieldsMapHolder();
                manager.beginTransaction().add(retainedFieldsHolder, target.getClass().getName()).commitNow();
            }
            else
            {
                //Restore all fields from mapping
                for (Field field : target.getClass().getDeclaredFields())
                {
                    if (field.getAnnotation(Retain.class) != null)
                    {
                        field.setAccessible(true);
                        field.set(target, retainedFieldsHolder.getMap().get(field.getName()));
                    }
                }
            }
        }
        catch (IllegalAccessException e)
        {
            throw new RetainerException(e);
        }
    }

    private static RetainedFieldsMapHolder getHolder(Object target, FragmentManager manager)
    {
        return (RetainedFieldsMapHolder) manager.findFragmentByTag(target.getClass().getName());
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
            RetainedFieldsMapHolder holder = getHolder(target, manager);
            for (Field field : target.getClass().getDeclaredFields())
            {
                if (field.getAnnotation(Retain.class) != null)
                {
                    field.setAccessible(true);
                    holder.getMap().put(field.getName(), field.get(target));
                }
            }
        }
        catch (IllegalAccessException e)
        {
            throw new RetainerException(e);
        }
    }
}

package com.retainer;

import android.support.v4.app.FragmentManager;

/**
 * Created by Hanan on 2/7/2017.
 */

public interface IClassRetainer<T> {

    void restore(T target, FragmentManager manager);

    void retain(T target, FragmentManager manager);
}

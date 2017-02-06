package com.example.hanan.retainhack.retainer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by Hanan on 2/6/2017.
 */

@SuppressLint("ValidFragment")
class RetainedObjectHolder<T> extends Fragment {

    private T mRetainedObject;

    RetainedObjectHolder(T retainedObject)
    {
        mRetainedObject = retainedObject;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    T getRetainedObject()
    {
        return mRetainedObject;
    }

    void setRetainedObject(T retainedObject)
    {
        mRetainedObject = retainedObject;
    }

}

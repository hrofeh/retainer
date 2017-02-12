package com.hrh.retainer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hanan on 2/6/2017.
 */

@SuppressLint("ValidFragment")
public class RetainedFieldsMapHolder extends Fragment {

    private Map<String, Object> mMap;

    public RetainedFieldsMapHolder()
    {
        mMap = new HashMap<>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public Map<String, Object> getMap()
    {
        return mMap;
    }

}

package com.example.hanan.retainhack.retainer;

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
class RetainedFieldsMapHolder extends Fragment {

    private Map<String, Object> mMap;

    RetainedFieldsMapHolder()
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

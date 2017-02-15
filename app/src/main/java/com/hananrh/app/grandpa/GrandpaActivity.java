package com.hananrh.app.grandpa;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hananrh.retainer.Retain;

/**
 * Created by Hanan on 2/13/2017.
 */

public abstract class GrandpaActivity extends AppCompatActivity {

    @Retain
    protected boolean mBooleanToRetain;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null)
        {
            mBooleanToRetain = true;
        }
    }
}

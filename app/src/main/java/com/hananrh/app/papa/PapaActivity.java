package com.hananrh.app.papa;

import android.os.Bundle;

import com.hananrh.app.grandpa.GrandpaActivity;
import com.hananrh.retainer.Retain;

/**
 * Created by Hanan on 2/13/2017.
 */

public abstract class PapaActivity extends GrandpaActivity {

    @Retain
    protected String mStringToRetain;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null)
        {
            mStringToRetain = "This is a retained string";
        }
    }
}

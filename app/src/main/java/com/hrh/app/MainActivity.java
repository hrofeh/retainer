package com.hrh.app;

import android.os.Bundle;
import android.widget.TextView;

import com.hrh.app.papa.PapaActivity;
import com.hrh.retainer.Retain;
import com.hrh.retainer.Retainer;

public class MainActivity extends PapaActivity {

    @Retain
    int mNumToTest;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null)
        {
            mNumToTest = 2;
        }
        Retainer.setLogsEnabled(true);
        Retainer.restore(this);
        ((TextView) findViewById(R.id.text)).setText("Retained number is: " + mNumToTest);
        ((TextView) findViewById(R.id.text2)).setText("Retained string is: " + mStringToRetain);
        ((TextView) findViewById(R.id.text3)).setText("Retained boolean is: " + mBooleanToRetain);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Retainer.retain(this);
    }


}

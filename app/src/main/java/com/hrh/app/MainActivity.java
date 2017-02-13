package com.hrh.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.hrh.retainer.Retain;
import com.hrh.retainer.Retainer;

public class MainActivity extends AppCompatActivity {

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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Retainer.retain(this);
    }
}

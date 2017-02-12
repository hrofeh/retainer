package com.example.hanan.retainhack;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.retainer.Retain;
import com.retainer.Retainer;

public class MainActivity extends AppCompatActivity {

    @Retain
    int mNum;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Retainer.restore(this);
        ((TextView) findViewById(R.id.text)).setText(mNum+"");
    }

    public void setNum(int num)
    {
        mNum = num;
    }



    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Retainer.retain(this);
    }
}

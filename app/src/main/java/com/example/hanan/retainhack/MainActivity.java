package com.example.hanan.retainhack;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.retainer.Retain;
import com.retainer.Retainer;

public class MainActivity extends AppCompatActivity {

    @Retain
    User mRetainedUser;

    @Retain
    int mNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Retainer.restore(this, getSupportFragmentManager());
        Toast.makeText(this, mRetainedUser != null ? mRetainedUser.getFirstName() + mNumber : "Empty", Toast.LENGTH_SHORT).show();
    }

    public void setVar(View view)
    {
        mRetainedUser = new User("Hanan");
        mNumber = 6;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Retainer.retain(this, getSupportFragmentManager());
    }
}

package com.example.hanan.retainhack;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class RetainerTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private Solo mSolo;

    @Before
    public void setUp() throws Exception
    {
        mSolo = new Solo(getInstrumentation(), mActivityTestRule.getActivity());
    }

    @Before
    public void setup()
    {
        mSolo = new Solo(getInstrumentation(), mActivityTestRule.getActivity());
    }

    @Test
    public void checkRetain() throws Exception
    {
        int num = 1;
        mActivityTestRule.getActivity().setNum(num);
        mSolo.setActivityOrientation(Solo.LANDSCAPE);
        assertThat(mSolo.getText(num + "").getText().toString(), is(num + ""));
    }


}

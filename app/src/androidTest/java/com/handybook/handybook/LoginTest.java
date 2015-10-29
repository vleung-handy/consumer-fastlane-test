package com.handybook.handybook;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import com.handybook.handybook.ui.activity.LoginActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

//proof of concept espresso test
public class LoginTest extends ActivityInstrumentationTestCase2<LoginActivity>{

    private LoginActivity mActivity;

    public LoginTest() {
        super(LoginActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity();
    }

    @Test
    public void LoginVisibleTest() {
        onView(withId(R.id.nav_text)).check(matches(withText("Log In")));
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
package com.handybook.handybook.core.ui.widget;

import com.handybook.handybook.RobolectricGradleTestWrapper;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.ShadowApplication;

import static junit.framework.Assert.assertEquals;

public class PhoneInputTextViewTest extends RobolectricGradleTestWrapper {

    private PhoneInputTextView mView;

    @Before
    public void setUp() throws Exception {
        mView = new PhoneInputTextView(
                ShadowApplication.getInstance().getApplicationContext());
    }

    @Test
    public void testPhoneNumberInputFormat() throws Exception {
        mView.setText("5555555555");
        assertEquals("(555) 555-5555", mView.getText().toString());
        assertEquals(mView.getText().length(), mView.getSelectionStart());
    }


}

package com.handybook.handybook.core.ui.widget;

import com.handybook.handybook.RobolectricGradleTestWrapper;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.ShadowApplication;

import static junit.framework.Assert.assertEquals;

public class CreditCardExpDateInputTextViewTest extends RobolectricGradleTestWrapper {

    private CreditCardExpDateInputTextView mView;

    @Before
    public void setUp() throws Exception {
        mView = new CreditCardExpDateInputTextView(
                ShadowApplication.getInstance().getApplicationContext());
    }

    @Test
    public void testCreditCardDateInputFormat() throws Exception {
        mView.setText("0112");
        assertEquals("01/12", mView.getText().toString());
        assertEquals(mView.getText().length(), mView.getSelectionStart());

        mView.setText("011");
        assertEquals("01/1", mView.getText().toString());
        assertEquals(mView.getText().length(), mView.getSelectionStart());

    }
}

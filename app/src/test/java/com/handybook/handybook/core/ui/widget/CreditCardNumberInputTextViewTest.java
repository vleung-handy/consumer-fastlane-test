package com.handybook.handybook.core.ui.widget;

import com.handybook.handybook.RobolectricGradleTestWrapper;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.ShadowApplication;

import static junit.framework.Assert.assertEquals;

public class CreditCardNumberInputTextViewTest extends RobolectricGradleTestWrapper {

    private CreditCardNumberInputTextView mView;

    @Before
    public void setUp() throws Exception {
        mView = new CreditCardNumberInputTextView(
                ShadowApplication.getInstance().getApplicationContext());
    }

    @Test
    public void testCreditCardNumberInputFormat() throws Exception {
        mView.setText("4242424242424242");
        assertEquals("4242 4242 4242 4242", mView.getText().toString());
        assertEquals(mView.getText().length(), mView.getSelectionStart());

        mView.setText("345555555555555");
        assertEquals("3455 555555 55555", mView.getText().toString());
        assertEquals(mView.getText().length(), mView.getSelectionStart());

        mView.setText("375555555555555");
        assertEquals("3755 555555 55555", mView.getText().toString());
        assertEquals(mView.getText().length(), mView.getSelectionStart());
    }
}

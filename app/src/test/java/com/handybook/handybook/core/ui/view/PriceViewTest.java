package com.handybook.handybook.core.ui.view;

import com.handybook.handybook.RobolectricGradleTestWrapper;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.ShadowApplication;

import static org.junit.Assert.assertEquals;

public class PriceViewTest extends RobolectricGradleTestWrapper
{
    private PriceView mPriceView;

    @Before
    public void setUp() throws Exception
    {
        mPriceView = new PriceView(ShadowApplication.getInstance().getApplicationContext());
        mPriceView.setCurrencySymbol("$");
    }

    @Test
    public void testSetPriceFromFloat()
    {
        mPriceView.setPriceDollars(156.4f);//using this specific float because this used to cause the decimal component to be 39 instead of 40
        assertEquals(mPriceView.mCardinal.getText(), "$156");
        assertEquals(mPriceView.mDecimal.getText(), "40");
    }

    @Test
    public void testSetPriceFromInteger()
    {
        mPriceView.setPriceCents(15640);
        assertEquals(mPriceView.mCardinal.getText(), "$156");
        assertEquals(mPriceView.mDecimal.getText(), "40");
    }
}

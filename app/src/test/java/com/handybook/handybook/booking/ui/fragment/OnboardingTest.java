package com.handybook.handybook.booking.ui.fragment;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ViewSwitcher;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.onboarding.OnboardV2Fragment;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.mockito.MockitoAnnotations.initMocks;

public class OnboardingTest extends RobolectricGradleTestWrapper
{
    private OnboardV2Fragment mFragment;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext())
                .inject(this);
        mFragment = OnboardV2Fragment.newInstance();
        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);
    }

    /**
     * At launch the zip page should be visible.
     */
    @Test
    public void testShowZip()
    {
        ViewSwitcher viewSwitcher = (ViewSwitcher) mFragment.getView()
                                                            .findViewById(R.id.view_switcher);
        View zipView = mFragment.getView().findViewById(R.id.view_zip);

        Assert.assertEquals(
                "The current view should be the zip view",
                zipView.getId(),
                viewSwitcher.getCurrentView().getId()
        );
    }

    /**
     * The button should be disabled, until a valid zip is set, then enabled.
     * Upon clicking on the button, the email view should be visible.
     */
    @Test
    public void testZipNextButton()
    {
        ViewSwitcher viewSwitcher = (ViewSwitcher) mFragment.getView()
                                                            .findViewById(R.id.view_switcher);
        View emailView = mFragment.getView().findViewById(R.id.view_email);
        View nextButton = mFragment.getView().findViewById(R.id.button_next);
        TextInputEditText editText = (TextInputEditText) mFragment.getView()
                                                                  .findViewById(R.id.edit_zip);

        Assert.assertEquals("The next button should be disabled", false, nextButton.isEnabled());

        editText.setText("10011");

        Assert.assertEquals("The next button should be enabled", true, nextButton.isEnabled());

        mFragment.nextClicked();
        Assert.assertEquals(
                "The current view should be the email view",
                emailView.getId(),
                viewSwitcher.getCurrentView().getId()
        );
    }

    /**
     * The email button should be initially disabled, and enabled upon a valid email address
     * (something that fits the regex)
     */
    @Test
    public void testEmailSubmitButton()
    {
        TextInputEditText editText = (TextInputEditText) mFragment.getView()
                                                                  .findViewById(R.id.edit_email);
        View submitButton = mFragment.getView().findViewById(R.id.button_submit);

        Assert.assertEquals(
                "The submit button should be disabled",
                false,
                submitButton.isEnabled()
        );
        editText.setText(";sakjflaskdjfksajf");

        //should still be disabled because of the failed regex.
        Assert.assertEquals(
                "The submit button should be disabled",
                false,
                submitButton.isEnabled()
        );

        editText.setText("j@h.com");
        Assert.assertEquals("The submit button should be enabled", true, submitButton.isEnabled());
    }
}

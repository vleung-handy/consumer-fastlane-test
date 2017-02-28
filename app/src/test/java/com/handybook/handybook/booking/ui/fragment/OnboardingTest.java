package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ViewSwitcher;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.model.response.UserExistsResponse;
import com.handybook.handybook.core.ui.activity.LoginActivity;
import com.handybook.handybook.onboarding.OnboardV2Fragment;
import com.handybook.handybook.onboarding.ServiceNotSupportedActivity;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

public class OnboardingTest extends RobolectricGradleTestWrapper {

    private OnboardV2Fragment mFragment;

    @Mock
    UserExistsResponse mUserExistsResponse;

    @Captor
    private ArgumentCaptor<DataManager.Callback> mCallbackCaptor;

    @Before
    public void setUp() throws Exception {
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
    public void testShowZip() {
        ViewSwitcher viewSwitcher = (ViewSwitcher) mFragment.getView()
                                                            .findViewById(R.id.onboard_view_switcher);
        View zipView = mFragment.getView().findViewById(R.id.onboard_zip);

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
    public void testZipNextButton() {
        ViewSwitcher viewSwitcher = (ViewSwitcher) mFragment.getView()
                                                            .findViewById(R.id.onboard_view_switcher);
        View emailView = mFragment.getView().findViewById(R.id.onboard_email);
        View nextButton = mFragment.getView().findViewById(R.id.onboard_button_next);
        TextInputEditText editText = (TextInputEditText) mFragment.getView()
                                                                  .findViewById(R.id.onboard_edit_zip);

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
     * If the email is showing, then clicking back will show zip view again.
     */
    @Test
    public void testBackPressed() {
        ViewSwitcher viewSwitcher = (ViewSwitcher) mFragment.getView()
                                                            .findViewById(R.id.onboard_view_switcher);
        View zipView = mFragment.getView().findViewById(R.id.onboard_zip);

        Assert.assertEquals(
                "The current view should be the zip view",
                zipView.getId(),
                viewSwitcher.getCurrentView().getId()
        );

        viewSwitcher.showNext();
        mFragment.onBackPressed();

        Assert.assertEquals(
                "The current view should be the zip view",
                zipView.getId(),
                viewSwitcher.getCurrentView().getId()
        );
    }

    /**
     * The email button should be initially disabled, and enabled upon a valid email address
     * (something that fits the regex)
     */
    @Test
    public void testEmailSubmitButton() {
        TextInputEditText editText = (TextInputEditText) mFragment.getView()
                                                                  .findViewById(R.id.onboard_edit_email);
        View submitButton = mFragment.getView().findViewById(R.id.onboard_button_submit);

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

    /**
     * Tests that the sign in button launches the login activity
     */
    @Test
    public void testSignin() {
        mFragment.signinClicked();

        Intent nextStartedActivity = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(
                nextStartedActivity.getComponent().getClassName(),
                equalTo(LoginActivity.class.getName())
        );
    }

    /**
     * If the user is an existing user, we want them to go to the sign in page
     */
    @Test
    public void testExistingUserResponse() {
        //mocking an existing user
        when(mUserExistsResponse.getFirstName()).thenReturn("John Doe");
        when(mUserExistsResponse.exists()).thenReturn(true);

        mFragment.handleEmailResponse(mUserExistsResponse);

        Intent nextStartedActivity = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(
                nextStartedActivity.getComponent().getClassName(),
                equalTo(LoginActivity.class.getName())
        );
    }

    /**
     * In the situation where no services are returned for a given zip, then we direct the user
     * to the not-supported page
     */
    @Test
    public void testZipNotSupported() {
        //mocking a new user
        when(mUserExistsResponse.getFirstName()).thenReturn("John Doe");
        when(mUserExistsResponse.exists()).thenReturn(false);

        mFragment.handleEmailResponse(mUserExistsResponse);

        Intent nextStartedActivity = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(
                nextStartedActivity.getComponent().getClassName(),
                equalTo(ServiceNotSupportedActivity.class.getName())
        );
    }

    /**
     * If this is a new user submitting an email, and there are services supported in the zip
     * then we go to home page
     */
    @Test
    public void testNewUserToHomePage() {
        //mocking a new user
        when(mUserExistsResponse.getFirstName()).thenReturn("John Doe");
        when(mUserExistsResponse.exists()).thenReturn(false);

        List<Service> services = new ArrayList<>();
        services.add(new Service());

        mFragment.onServicesReceived(services, "10011");
        mFragment.handleEmailResponse(mUserExistsResponse);

        //since now there are services, it will navigate the user to the home page
        Intent nextStartedActivity = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(
                nextStartedActivity.getComponent().getClassName(),
                equalTo(ServiceCategoriesActivity.class.getName())
        );
    }
}

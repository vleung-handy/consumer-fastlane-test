package com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.LocalizedMonetaryAmount;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.booking.ui.fragment.TipDialogFragment;
import com.handybook.handybook.configuration.model.Configuration;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.proteam.ui.activity.ProTeamEditActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.ArrayList;

import javax.inject.Inject;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

public class BookingDetailSectionFragmentProInformationTest extends RobolectricGradleTestWrapper {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Booking mBooking;
    @Mock
    private Configuration mConfiguration;
    @Mock
    private Provider mProvider;
    @Mock
    private User mUser;
    @Mock
    private LocalizedMonetaryAmount mTipAmount;

    @Inject
    UserManager mUserManager;

    private BookingDetailSectionFragmentProInformation mFragment;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ((TestBaseApplication) ShadowApplication.getInstance()
                                                .getApplicationContext()).inject(this);
        when(mBooking.getProvider()).thenReturn(mProvider);
        when(mUserManager.getCurrentUser()).thenReturn(mUser);

        mFragment =
                BookingDetailSectionFragmentProInformation.newInstance();

        Bundle bundle = new Bundle();
        bundle.putParcelable(BundleKeys.BOOKING, mBooking);
        mFragment.setArguments(bundle);
    }

    @Test
    public void shouldShowManageProTeamButtonWhenPendingProviderAssignment() throws Exception {
        when(mBooking.hasAssignedProvider()).thenReturn(false);
        when(mBooking.isPast()).thenReturn(false);

        SupportFragmentTestUtil.startVisibleFragment(mFragment);

        TextView actionTextView = (TextView) mFragment.getSectionView()
                                                      .findViewById(R.id.entry_action_text);
        assertEquals(actionTextView.getText(), mFragment.getString(R.string.manage_pro_team));

        actionTextView.performClick();
        Intent nextStartedActivity = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(
                nextStartedActivity.getComponent().getClassName(),
                equalTo(ProTeamEditActivity.class.getName())
        );
    }

    @Test
    public void shouldShowProviderNameWhenProviderAssigned() throws Exception {
        when(mBooking.hasAssignedProvider()).thenReturn(true);
        when(mBooking.isPast()).thenReturn(false);
        String providerName = "Testy M.";
        when(mProvider.getFirstNameAndLastInitial()).thenReturn(providerName);

        SupportFragmentTestUtil.startVisibleFragment(mFragment);

        TextView providerNameText = (TextView) mFragment.getSectionView()
                                                        .findViewById(R.id.mini_pro_profile_title);
        assertEquals(providerName, providerNameText.getText());
    }

    @Test
    public void shouldShowTipButtonWhenBookingPastAndCanShowTip() throws Exception {
        when(mBooking.hasAssignedProvider()).thenReturn(true);
        when(mBooking.isPast()).thenReturn(true);
        when(mBooking.canLeaveTip()).thenReturn(true);
        when(mBooking.getId()).thenReturn("123");

        ArrayList<LocalizedMonetaryAmount> tipAmounts = new ArrayList<>();
        tipAmounts.add(mTipAmount);
        when(mUser.getDefaultTipAmounts()).thenReturn(tipAmounts);

        SupportFragmentTestUtil.startVisibleFragment(mFragment);

        TextView actionTextView = (TextView) mFragment.getSectionView()
                                                      .findViewById(R.id.entry_action_text);
        assertEquals(actionTextView.getText(), mFragment.getString(R.string.leave_a_tip));
        actionTextView.performClick();

        TipDialogFragment dialog = (TipDialogFragment) mFragment
                .getFragmentManager().findFragmentByTag(TipDialogFragment.TAG);
        Assert.assertNotNull(dialog);
    }

    @Test
    public void shouldShowProTeamMatchIndicatorWhenProIsOnProTeam() throws Exception {
        when(mBooking.hasAssignedProvider()).thenReturn(true);
        when(mBooking.isPast()).thenReturn(false);
        Booking.ProviderAssignmentInfo providerAssignmentInfo
                = mock(Booking.ProviderAssignmentInfo.class);
        when(providerAssignmentInfo.isProTeamMatch()).thenReturn(true);

        when(mBooking.getProviderAssignmentInfo()).thenReturn(providerAssignmentInfo);

        SupportFragmentTestUtil.startVisibleFragment(mFragment);

        View proTeamMatchIndicator = mFragment.getSectionView()
                                              .findViewById(R.id.mini_pro_profile_title);
        assertTrue(proTeamMatchIndicator.getVisibility() == View.VISIBLE);
    }

    @Test
    public void shouldNotShowActionTextViewWhenBookingPastAndCannotLeaveTip() throws Exception {
        when(mBooking.hasAssignedProvider()).thenReturn(true);
        when(mBooking.isPast()).thenReturn(true);
        when(mBooking.canLeaveTip()).thenReturn(false);

        SupportFragmentTestUtil.startVisibleFragment(mFragment);

        TextView actionTextView = (TextView) mFragment.getSectionView()
                                                      .findViewById(R.id.entry_action_text);
        assertTrue(actionTextView.getVisibility() == View.GONE);
    }
}

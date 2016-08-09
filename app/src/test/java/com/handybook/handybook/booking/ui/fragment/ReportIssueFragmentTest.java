package com.handybook.handybook.booking.ui.fragment;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.ProviderJobStatus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReportIssueFragmentTest extends RobolectricGradleTestWrapper
{
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    Booking mBooking;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    ProviderJobStatus mProviderJobStatus;

    @Before
    public void setUp()
    {
        initMocks(this);

        when(mBooking.getBookingTimezone()).thenReturn("EST");
        when(mBooking.getStartDate()).thenReturn(new Date());
        when(mBooking.getEndDate()).thenReturn(new Date());
        when(mBooking.getProvider().getFullName()).thenReturn("Xi Wei");
    }

    @Test
    public void shouldHaveCorrectTitleAndHeader()
    {
        ReportIssueFragment fragment = ReportIssueFragment.newInstance(mBooking, mProviderJobStatus);
        SupportFragmentTestUtil.startFragment(fragment, AppCompatActivity.class);

        assertEquals(fragment.getString(R.string.help),
                ((AppCompatActivity) fragment.getActivity()).getSupportActionBar().getTitle());
        assertNotNull(fragment.getView().findViewById(R.id.report_issue_date));
        assertNotNull(fragment.getView().findViewById(R.id.report_issue_time));
        TextView providerText = (TextView) fragment.getView().findViewById(R.id.report_issue_provider);
        assertEquals("Xi Wei", providerText.getText());
    }

    @Test
    public void shouldShowMilestones()
    {
        ProviderJobStatus.Milestone[] milestones = new ProviderJobStatus.Milestone[6];
        ProviderJobStatus.Action[] actions = new ProviderJobStatus.Action[1];
        actions[0] = new ProviderJobStatus.Action("call_or_text");
        milestones[0] = new ProviderJobStatus.Milestone("On their way", "Your pro, John, is on his way", ProviderJobStatus.Milestone.COMPLETE, null, new Date());
        milestones[1] = new ProviderJobStatus.Milestone("incomplete", "incomplete", ProviderJobStatus.Milestone.INCOMPLETE, actions, new Date());
        milestones[2] = new ProviderJobStatus.Milestone("warning", "warning", ProviderJobStatus.Milestone.WARNING, null, new Date());
        milestones[3] = new ProviderJobStatus.Milestone("error", "error", ProviderJobStatus.Milestone.ERROR, null, new Date());
        milestones[4] = new ProviderJobStatus.Milestone("invalid", "invalid", ProviderJobStatus.Milestone.INVALID, null, new Date());
        milestones[5] = new ProviderJobStatus.Milestone("", "", "", null, new Date());
        when(mProviderJobStatus.getMilestones()).thenReturn(milestones);

        ReportIssueFragment fragment = ReportIssueFragment.newInstance(mBooking, mProviderJobStatus);
        SupportFragmentTestUtil.startFragment(fragment, AppCompatActivity.class);

        // Should have at least 3 milestones.
        assertThat(fragment.mMilestonesLayout.getChildCount(), greaterThan(3));

        // Only the 2nd milestone should have contact buttons
        View milestoneView = fragment.mMilestonesLayout.getChildAt(0);
        assertEquals(View.GONE, milestoneView.findViewById(R.id.pro_milestone_call).getVisibility());
        assertEquals(View.GONE, milestoneView.findViewById(R.id.pro_milestone_text).getVisibility());
        milestoneView = fragment.mMilestonesLayout.getChildAt(1);
        assertEquals(View.VISIBLE, milestoneView.findViewById(R.id.pro_milestone_call).getVisibility());
        assertEquals(View.VISIBLE, milestoneView.findViewById(R.id.pro_milestone_text).getVisibility());


    }
}

package com.handybook.handybook.booking.ui.fragment;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.JobStatus;
import com.handybook.handybook.booking.model.Provider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReportIssueFragmentTest extends RobolectricGradleTestWrapper
{
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    Booking mBooking;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    JobStatus mJobStatus;

    @Before
    public void setUp()
    {
        initMocks(this);

        when(mBooking.getBookingTimezone()).thenReturn("EST");
        when(mBooking.getStartDate()).thenReturn(new Date());
        when(mBooking.getEndDate()).thenReturn(new Date());
        Provider provider = new Provider(1, "Xi", "Wei", "123456789");
        when(mBooking.getProvider()).thenReturn(provider);
//        when(mBooking.getProvider().getFirstNameAndLastInitial()).thenReturn("Xi W.");
    }

    @Test
    public void shouldHaveCorrectTitleAndHeader()
    {
        ReportIssueFragment fragment = ReportIssueFragment.newInstance(mBooking, mJobStatus);
        SupportFragmentTestUtil.startFragment(fragment, AppCompatActivity.class);

        assertEquals(fragment.getString(R.string.help),
                ((AppCompatActivity) fragment.getActivity()).getSupportActionBar().getTitle());
        assertNotNull(fragment.getView().findViewById(R.id.report_issue_date));
        assertNotNull(fragment.getView().findViewById(R.id.report_issue_time));
        TextView providerText = (TextView) fragment.getView().findViewById(R.id.report_issue_provider);
        assertEquals("Xi W.", providerText.getText());
    }

    @Test
    public void shouldShowMilestones()
    {
        JobStatus.Milestone[] milestones = new JobStatus.Milestone[4];
        JobStatus.Action actions = new JobStatus.Action("call_sms_contact");
        milestones[0] = new JobStatus.Milestone("On their way", "Your pro, John, is on his way", JobStatus.Milestone.NORMAL, actions, new Date());
        milestones[1] = new JobStatus.Milestone("warning", "warning", JobStatus.Milestone.WARNING, null, new Date());
        milestones[2] = new JobStatus.Milestone("error", "error", JobStatus.Milestone.ERROR, null, new Date());
        milestones[3] = new JobStatus.Milestone("invalid", "invalid", JobStatus.Milestone.INVALID, null, new Date());
        when(mJobStatus.getMilestones()).thenReturn(milestones);

        ReportIssueFragment fragment = ReportIssueFragment.newInstance(mBooking, mJobStatus);
        SupportFragmentTestUtil.startFragment(fragment, AppCompatActivity.class);

        // Should have at n + 1 milestones displayed
        assertEquals(5, fragment.mMilestonesLayout.getChildCount());

        // Only the 2nd milestone should have contact buttons
        View milestoneView = fragment.mMilestonesLayout.getChildAt(0);
        assertEquals(View.VISIBLE, milestoneView.findViewById(R.id.pro_milestone_call).getVisibility());
        assertEquals(View.VISIBLE, milestoneView.findViewById(R.id.pro_milestone_text).getVisibility());
        milestoneView = fragment.mMilestonesLayout.getChildAt(1);
        assertEquals(View.GONE, milestoneView.findViewById(R.id.pro_milestone_call).getVisibility());
        assertEquals(View.GONE, milestoneView.findViewById(R.id.pro_milestone_text).getVisibility());
    }

    @Test
    public void shouldShowDeepLinks()
    {
        JobStatus.DeepLinkWrapper[] deepLinkWrappers = new JobStatus.DeepLinkWrapper[2];
        deepLinkWrappers[0] = new JobStatus.DeepLinkWrapper("help", "cancel",
                "handybook://deep_link/booking_cancel?fallback_url=https://help.handy.com");
        deepLinkWrappers[1] = new JobStatus.DeepLinkWrapper("help", "cancel",
                "handybook://deep_link/booking_cancel?fallback_url=https://help.handy.com");
        when(mJobStatus.getDeepLinkWrappers()).thenReturn(deepLinkWrappers);

        ReportIssueFragment fragment = ReportIssueFragment.newInstance(mBooking, mJobStatus);
        SupportFragmentTestUtil.startFragment(fragment, AppCompatActivity.class);

        assertEquals(2, fragment.mDeepLinksLayout.getChildCount());
    }
}

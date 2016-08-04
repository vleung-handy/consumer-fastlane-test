package com.handybook.handybook.booking.ui.fragment;

import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.Provider;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReportIssueFragmentTest extends RobolectricGradleTestWrapper
{
    private ReportIssueFragment mFragment;


    @Before
    public void setUp()
    {
        Booking booking = mock(Booking.class);
        Provider provider = mock(Provider.class);

        when(booking.getBookingTimezone()).thenReturn("EST");
        when(booking.getStartDate()).thenReturn(new Date());
        when(booking.getEndDate()).thenReturn(new Date());
        when(booking.getProvider()).thenReturn(provider);
        when(provider.getFullName()).thenReturn("Xi Wei");

        mFragment = ReportIssueFragment.newInstance(booking);
        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);
    }

    @Test
    public void shouldHaveCorrectTitleAndHeader()
    {
        assertEquals(mFragment.getString(R.string.help),
                ((AppCompatActivity) mFragment.getActivity()).getSupportActionBar().getTitle());
        assertNotNull(mFragment.getView().findViewById(R.id.report_issue_date));
        assertNotNull(mFragment.getView().findViewById(R.id.report_issue_time));
        TextView providerText = (TextView) mFragment.getView().findViewById(R.id.report_issue_provider);
        assertEquals("Xi Wei", providerText.getText());
    }
}

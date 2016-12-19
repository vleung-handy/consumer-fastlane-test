package com.handybook.handybook.referral.util;

import android.content.Intent;
import android.net.Uri;

import com.handybook.handybook.RobolectricGradleTestWrapper;

import org.junit.Test;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ReferralIntentUtilTest extends RobolectricGradleTestWrapper
{
    @Test
    public void returnsReferralGuidFromIntentData() throws Exception
    {
        final Intent intent = new Intent();
        intent.setData(Uri.parse("http://a.b.c/abc123/some_guid"));

        final String referralGuid = ReferralIntentUtil.getReferralGuidFromIntent(intent);

        assertThat(referralGuid, equalTo("some_guid"));
    }

    @Test
    public void returnsReferralGuidFromIntentExtra() throws Exception
    {
        final Intent intent = new Intent();
        intent.putExtra("h", "some_guid");

        final String referralGuid = ReferralIntentUtil.getReferralGuidFromIntent(intent);

        assertThat(referralGuid, equalTo("some_guid"));
    }

    @Test
    public void returnsNullIfIntentDataDoesNotContainReferralGuid() throws Exception
    {
        final Intent intent = new Intent();
        intent.setData(Uri.parse("http://a.b.c?other=123"));

        final String referralGuid = ReferralIntentUtil.getReferralGuidFromIntent(intent);

        assertNull(referralGuid);
    }

    @Test
    public void returnsNullIfIntentDoesNotContainReferralGuid() throws Exception
    {
        final Intent intent = new Intent();

        final String referralGuid = ReferralIntentUtil.getReferralGuidFromIntent(intent);

        assertNull(referralGuid);
    }
}

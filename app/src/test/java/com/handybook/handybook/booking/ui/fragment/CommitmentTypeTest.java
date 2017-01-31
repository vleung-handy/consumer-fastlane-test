package com.handybook.handybook.booking.ui.fragment;

import com.google.gson.GsonBuilder;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.subscription.Price;
import com.handybook.handybook.booking.model.subscription.SubscriptionFrequency;
import com.handybook.handybook.booking.model.subscription.SubscriptionLength;
import com.handybook.handybook.library.util.DateTimeUtils;
import com.handybook.handybook.library.util.IOUtils;

import junit.framework.Assert;

import org.junit.Test;

import java.util.List;

/**
 * This a unit test to verify the correctness of the new commitment type pricing conversions
 */
public class CommitmentTypeTest
{
    BookingQuote mQuote;

    private void setup() throws Exception
    {
        String json = IOUtils.getJsonStringForTest("commitment_quote.json");

        mQuote = new GsonBuilder()
                .setDateFormat(DateTimeUtils.UNIVERSAL_DATE_FORMAT)
                .create()
                .fromJson(json, BookingQuote.class);

        if (mQuote != null && mQuote.getCommitmentPrices() != null)
        {
            //if there is a specified active commitment to use
            if (mQuote.isCommitmentMonthsActive())
            {
                mQuote.setupCommitmentPricingStructure();
            }
        }

    }

    @Test
    public void testUniqueLengths() throws Exception
    {
        setup();

        List<SubscriptionLength> uniqueLengths = mQuote.getCommitmentType().getUniqueLengths();

        Assert.assertEquals("Should have lengths of 3, 6, 12", 3, uniqueLengths.size());
        Assert.assertEquals("Should have 3", true, containsLength(uniqueLengths, "3 Months"));
        Assert.assertEquals("Should have 6", true, containsLength(uniqueLengths, "6 Months"));
        Assert.assertEquals("Should have 12", true, containsLength(uniqueLengths, "12 Months"));

    }

    private boolean containsLength(List<SubscriptionLength> uniqueLengths, String title)
    {
        for (final SubscriptionLength uniqueLength : uniqueLengths)
        {
            if (uniqueLength.getTitle().equals(title))
            {
                return true;
            }
        }

        return false;
    }

    private boolean containsFrequency(List<SubscriptionFrequency> uniqueFrequencies, String title)
    {
        for (final SubscriptionFrequency freq : uniqueFrequencies)
        {
            if (freq.getTitle().equals(title))
            {
                return true;
            }
        }

        return false;
    }

    @Test
    public void testUniqueFrequencies() throws Exception
    {
        setup();

        List<SubscriptionFrequency> uniqueFrequencies = mQuote.getCommitmentType()
                                                              .getUniqueFrequencies();
        //since one time is disabled, we should only have 3 valid frequencies
        Assert.assertEquals(
                "Since one time is disabled, we should only have 3 valid frequencies",
                3,
                uniqueFrequencies.size()
        );
        Assert.assertEquals(
                "Should have Weekly",
                true,
                containsFrequency(uniqueFrequencies, "Weekly")
        );
        Assert.assertEquals(
                "Should have Biweekly",
                true,
                containsFrequency(uniqueFrequencies, "Bi-weekly")
        );
        Assert.assertEquals(
                "Should have Monthly",
                true,
                containsFrequency(uniqueFrequencies, "Monthly")
        );
    }

    @Test
    public void testSubscriptionPrices() throws Exception
    {
        setup();
        //6 months, bimonthly, 11.5 hours should have price: 24462, 12387
        Price price = mQuote.getCommitmentType()
                            .getSubscriptionPrices()
                            .get("6")
                            .get("2")
                            .getPrices()
                            .get("11.5");
        Assert.assertEquals("Full price should be 24462", 24462, price.getFullPrice());
        Assert.assertEquals("amount due should be 12387", 12387, price.getAmountDue());

        //3 months, weekly, 10 hours should have price: 23312, 11812
        price = mQuote.getCommitmentType()
                      .getSubscriptionPrices()
                      .get("3")
                      .get("1")
                      .getPrices()
                      .get("10.0");
        Assert.assertEquals("Full price should be 23312", 23312, price.getFullPrice());
        Assert.assertEquals("amount due should be 11812", 11812, price.getAmountDue());

    }
}

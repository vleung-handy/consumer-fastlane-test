package com.handybook.handybook.testutil;

import com.squareup.otto.Bus;

import org.hamcrest.Matcher;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * utility class containing app-specific assertion methods
 */
public class AppAssertionUtils
{
    public static void assertBusPost(Bus bus, ArgumentCaptor captor, Matcher matcher)
    {
        verify(bus, atLeastOnce()).post(captor.capture());
        assertThat(captor.getAllValues(), hasItem(matcher));
    }
}

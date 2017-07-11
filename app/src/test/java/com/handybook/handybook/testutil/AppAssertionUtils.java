package com.handybook.handybook.testutil;

import org.greenrobot.eventbus.EventBus;
import org.hamcrest.Matcher;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * utility class containing app-specific assertion methods
 */
public class AppAssertionUtils {

    public static void assertBusPost(EventBus bus, ArgumentCaptor captor, Matcher matcher) {
        verify(bus, atLeastOnce()).post(captor.capture());
        assertThat(captor.getAllValues(), hasItem(matcher));
    }

    public static <T> T getFirstMatchingBusEvent(EventBus bus, Class klass) {
        ArgumentCaptor<T> captor = ArgumentCaptor.forClass(klass);
        verify(bus, atLeastOnce()).post(captor.capture());
        for (Object event : captor.getAllValues()) {
            if (klass.isInstance(event)) {
                return (T) event;
            }
        }
        return null;
    }
}

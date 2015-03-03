package com.handybook.handybook.ui.activity;

import android.os.Bundle;
import android.test.AndroidTestCase;
import android.widget.ListView;

import com.handybook.handybook.HandyRobolectricTestRunner;
import com.handybook.handybook.core.TestModule;
import com.handybook.handybook.ui.fragment.BookingsFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import dagger.ObjectGraph;

/**
 * Created by jwilliams on 2/25/15.
 */
@RunWith(HandyRobolectricTestRunner.class)
public class BookingsActivityTest {

    @Before public void setUp() {
        ObjectGraph.create(new TestModule(Robolectric.application)).inject(this);
    }

    @Test
    public void testLoadUserBookings() {
        ActivityController controller = Robolectric.buildActivity(BookingsActivity.class);
        BookingsActivity activity = (BookingsActivity)controller.get();
        BookingsFragment fragment = (BookingsFragment)activity.createFragment();
        controller.create();
        assertNotNull(fragment);
    }
}

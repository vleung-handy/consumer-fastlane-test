package com.handybook.handybook.ui.activity;

import android.widget.ListView;

import com.handybook.handybook.HandyRobolectricTestRunner;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.ui.fragment.BookingsFragment;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.robolectric.Robolectric;

import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;
import org.robolectric.util.FragmentTestUtil;

import java.io.IOException;

import javax.inject.Inject;

/**
 * Created by jwilliams on 2/25/15.
 */
@RunWith(HandyRobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class BookingsActivityTest {

    @Inject MockWebServer server;

    @Before
    public void setUp() {
        ((BaseApplication)Robolectric.application).inject(this);
    }

    public void testLoadUserBookings() {
        ActivityController controller = Robolectric.buildActivity(BookingsActivity.class);
        BookingsActivity activity = (BookingsActivity)controller.get();
        BookingsFragment fragment = (BookingsFragment)activity.createFragment();
        controller.create().start().resume();
        assertNotNull(fragment.getView());
        ListView listView = (ListView)fragment.getView().findViewById(android.R.id.list);
        assertEquals(3, listView.getAdapter().getCount());
    }

    @Test
    public void testLoadUpcomingBookings() throws IOException {
        // Schedule some responses.
        server.enqueue(new MockResponse().setBody(getUpcomingBookingsJsonResponse()));

        BookingsFragment fragment = BookingsFragment.newInstance();
        FragmentTestUtil.startFragment(fragment);
        assertNotNull(fragment.getView());
        ListView listView = (ListView)fragment.getView().findViewById(android.R.id.list);

        assertEquals(3, listView.getAdapter().getCount());

        server.shutdown();
    }

    @Test
    public void testLoadPastBookings() throws IOException {
        // Schedule some responses.
        server.enqueue(new MockResponse().setBody(getPastBookingsJsonResponse()));

        BookingsFragment fragment = BookingsFragment.newInstance();
        FragmentTestUtil.startFragment(fragment);
        assertNotNull(fragment.getView());
        ListView listView = (ListView)fragment.getView().findViewById(android.R.id.list);

        assertEquals(5, listView.getAdapter().getCount());

        server.shutdown();
    }

    @Test
    public void testLoadUpcomingAndPastBookings() throws IOException {
        // Schedule some responses.
        server.enqueue(new MockResponse().setBody(getUpcomingAndPastBookingsJsonResponse()));

        BookingsFragment fragment = BookingsFragment.newInstance();
        FragmentTestUtil.startFragment(fragment);
        assertNotNull(fragment.getView());
        ListView listView = (ListView)fragment.getView().findViewById(android.R.id.list);

        assertEquals(4, listView.getAdapter().getCount());

        server.shutdown();
    }

    // N.B. Had to change time zone string from format -04:00 -> 0400 because of differences in
    // SimpleDateFormat in Android SDK vs JDK
    public String getUpcomingBookingsJsonResponse() {
        return "{\"user_bookings\":" +
                "[{\"booking_status\":0,\"service_id\":3,\"service_name\":\"Home Cleaning\"," +
                "\"service_machine\":\"home_cleaning\",\"id\":102445,\"address\":{\"zipcode\":\"10001\"," +
                "\"address1\":\"Handy St\",\"address2\":\"7\",\"city\":\"New York\",\"state\":\"NY\"}," +
                "\"price\":81,\"date_start\":\"2015-03-10T19:00:00-0400\",\"hours\":3,\"getin\":0," +
                "\"getin_string\":\"I’ll be home.\",\"msg_to_pro\":\"Focus on the floor\"," +
                "\"provider\":{\"phone\":\"8888476036\",\"status\":0},\"num_providers\":1," +
                "\"description\":\"\\n(Customer will be at home)\\n(Customer message to provider: Focus on the floor)\"," +
                "\"comments\":null,\"user_id\":5024,\"recurring\":1,\"available_pros\":null," +
                "\"recurring_string\":\"Repeats every 2 weeks\",\"payment_hash\":null," +
                "\"billed_status\":\"Billed on Mon, Mar 9\"},{\"booking_status\":0,\"service_id\":3," +
                "\"service_name\":\"Home Cleaning\",\"service_machine\":\"home_cleaning\",\"id\":102446," +
                "\"address\":{\"zipcode\":\"10001\",\"address1\":\"Handy St\",\"address2\":\"7\"," +
                "\"city\":\"New York\",\"state\":\"NY\"},\"price\":59,\"date_start\":\"2015-03-24T19:00:00-0400\"," +
                "\"hours\":3,\"provider\":{\"phone\":\"8888476036\",\"status\":0},\"num_providers\":1," +
                "\"description\":\"\",\"comments\":null,\"user_id\":5024,\"recurring\":0," +
                "\"available_pros\":null,\"recurring_string\":\"Repeats every 2 weeks\"," +
                "\"payment_hash\":[{\"order\":0,\"label\":\"Subtotal\",\"amount\":\"$81\"},{\"order\":1," +
                "\"label\":\"Coupon\",\"amount\":\"(-$22)\"}],\"billed_status\":\"Billed on Mon, Mar 23\"}]," +
                "\"user_has_recurring_bookings\":true,\"currency_char\":\"$\",\"currency_suffix\":\"\"}";
    }

    public String getPastBookingsJsonResponse() {
        return "{\"user_bookings\":" +
                "[{\"booking_status\":1,\"service_id\":3,\"service_name\":\"Home Cleaning\"," +
                "\"service_machine\":\"home_cleaning\",\"id\":102445,\"address\":{\"zipcode\":\"10001\"," +
                "\"address1\":\"Handy St\",\"address2\":\"7\",\"city\":\"New York\",\"state\":\"NY\"}," +
                "\"price\":81,\"date_start\":\"2015-03-10T19:00:00-0400\",\"hours\":3,\"getin\":0," +
                "\"getin_string\":\"I’ll be home.\",\"msg_to_pro\":\"Focus on the floor\"," +
                "\"provider\":{\"phone\":\"8888476036\",\"status\":0},\"num_providers\":1," +
                "\"description\":\"\\n(Customer will be at home)\\n(Customer message to provider: Focus on the floor)\"," +
                "\"comments\":null,\"user_id\":5024,\"recurring\":1,\"available_pros\":null," +
                "\"recurring_string\":\"Repeats every 2 weeks\",\"payment_hash\":null," +
                "\"billed_status\":\"Billed on Mon, Mar 9\"},{\"booking_status\":1,\"service_id\":3," +
                "\"service_name\":\"Home Cleaning\",\"service_machine\":\"home_cleaning\",\"id\":102446," +
                "\"address\":{\"zipcode\":\"10001\",\"address1\":\"Handy St\",\"address2\":\"7\"," +
                "\"city\":\"New York\",\"state\":\"NY\"},\"price\":59,\"date_start\":\"2015-03-24T19:00:00-0400\"," +
                "\"hours\":3,\"provider\":{\"phone\":\"8888476036\",\"status\":0},\"num_providers\":1," +
                "\"description\":\"\",\"comments\":null,\"user_id\":5024,\"recurring\":0," +
                "\"available_pros\":null,\"recurring_string\":\"Repeats every 2 weeks\"," +
                "\"payment_hash\":[{\"order\":0,\"label\":\"Subtotal\",\"amount\":\"$81\"},{\"order\":1," +
                "\"label\":\"Coupon\",\"amount\":\"(-$22)\"}],\"billed_status\":\"Billed on Mon, Mar 23\"}]," +
                "\"user_has_recurring_bookings\":true,\"currency_char\":\"$\",\"currency_suffix\":\"\"}";
    }

    public String getUpcomingAndPastBookingsJsonResponse() {
        return "{\"user_bookings\":" +
                "[{\"booking_status\":0,\"service_id\":3,\"service_name\":\"Home Cleaning\"," +
                "\"service_machine\":\"home_cleaning\",\"id\":102445,\"address\":{\"zipcode\":\"10001\"," +
                "\"address1\":\"Handy St\",\"address2\":\"7\",\"city\":\"New York\",\"state\":\"NY\"}," +
                "\"price\":81,\"date_start\":\"2015-03-10T19:00:00-0400\",\"hours\":3,\"getin\":0," +
                "\"getin_string\":\"I’ll be home.\",\"msg_to_pro\":\"Focus on the floor\"," +
                "\"provider\":{\"phone\":\"8888476036\",\"status\":0},\"num_providers\":1," +
                "\"description\":\"\\n(Customer will be at home)\\n(Customer message to provider: Focus on the floor)\"," +
                "\"comments\":null,\"user_id\":5024,\"recurring\":1,\"available_pros\":null," +
                "\"recurring_string\":\"Repeats every 2 weeks\",\"payment_hash\":null," +
                "\"billed_status\":\"Billed on Mon, Mar 9\"},{\"booking_status\":1,\"service_id\":3," +
                "\"service_name\":\"Home Cleaning\",\"service_machine\":\"home_cleaning\",\"id\":102446," +
                "\"address\":{\"zipcode\":\"10001\",\"address1\":\"Handy St\",\"address2\":\"7\"," +
                "\"city\":\"New York\",\"state\":\"NY\"},\"price\":59,\"date_start\":\"2015-03-24T19:00:00-0400\"," +
                "\"hours\":3,\"provider\":{\"phone\":\"8888476036\",\"status\":0},\"num_providers\":1," +
                "\"description\":\"\",\"comments\":null,\"user_id\":5024,\"recurring\":0," +
                "\"available_pros\":null,\"recurring_string\":\"Repeats every 2 weeks\"," +
                "\"payment_hash\":[{\"order\":0,\"label\":\"Subtotal\",\"amount\":\"$81\"},{\"order\":1," +
                "\"label\":\"Coupon\",\"amount\":\"(-$22)\"}],\"billed_status\":\"Billed on Mon, Mar 23\"}]," +
                "\"user_has_recurring_bookings\":true,\"currency_char\":\"$\",\"currency_suffix\":\"\"}";
    }
}

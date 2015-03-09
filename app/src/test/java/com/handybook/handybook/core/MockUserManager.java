package com.handybook.handybook.core;

import com.handybook.handybook.data.SecurePreferences;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Created by jwilliams on 3/4/15.
 */
public class MockUserManager extends UserManager {

    @Inject
    public MockUserManager(final Bus bus, final SecurePreferences prefs) {
        super(bus, prefs);
        setCurrentUser(User.fromJson("{\"auth_token\":\"Or4P7gfnl3QCKShEUob\",\"id\":\"5024\"," +
                "\"credits\":0.0,\"first_name\":\"Handy\",\"last_name\":\"Tester\"," +
                "\"email\":\"handytester@handy.com\",\"phone_country_prefix\":\"+1\"," +
                "\"phone\":\"3015555555\",\"currency_char\":\"$\",\"currency_suffix\":\"\"," +
                "\"first_address\":{\"address1\":\"Handy St\",\"address2\":\"7\",\"zipcode\":\"10001\"}," +
                "\"card_info\":{\"brand\":\"Visa\",\"last4\":\"4242\"}," +
                "\"analytics\":{\"last_booking_end\":\"2015-03-24T22:00:00-0400\",\"bookings\":2," +
                "\"facebook_login\":false,\"provider\":false,\"vip\":false,\"past_bookings_count\":0," +
                "\"recurring_bookings_count\":1,\"total_bookings_count\":2," +
                "\"upcoming_bookings_count\":2}}"));
    }

    public User getCurrentUser() {
        return user;
    }

    public void setCurrentUser(final User newUser) {
        user = newUser;
    }
}

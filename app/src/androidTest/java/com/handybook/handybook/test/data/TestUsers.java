package com.handybook.handybook.test.data;

import com.handybook.handybook.test.model.Address;
import com.handybook.handybook.test.model.TestUser;
import com.stripe.android.model.Card;

public class TestUsers
{
    private static Card mValidVisaCard = new Card(
            "4242424242424242", 12, 50, "424");

    public static TestUser FIRST_TIME_USER_BOOKING_CREATION =
            new TestUser("Testy McTesty",
                    "test+new-user-booking-creation@handybook.com",
                    "password",
                    new Address("1 Test Drive", "Apt. 13D", "99499"),
                    "9876543210",
                    mValidVisaCard);

    public static TestUser EXISTING_USER_BOOKING_CREATION =
            new TestUser("test+user@handybook.com",
                    "supersecretpassword");

    public static TestUser UPDATE_PROFILE_USER =
            new TestUser(
                    "test+user2@handybook.com",
                    "supersecretpassword");

    public static TestUser LOGIN =
            new TestUser(
                    "test+user@handybook.com",
                    "supersecretpassword");


}

package com.handybook.handybook.test.data;

import com.handybook.handybook.test.model.Address;
import com.handybook.handybook.test.model.TestUser;
import com.stripe.android.model.Card;

import java.util.UUID;

public class TestUsers {

    private static Card mValidVisaCard = new Card(
            "4242424242424242", 12, 50, "424");

    public static TestUser getUserRandomEmail() {
        return new TestUser(
                "Testy McTesty",
                UUID.randomUUID().toString() + "test+new-user-booking-creation@handybook.com",
                "password",
                new Address("1 Test Drive", "Apt. 13D", "99499"),
                "9876543210",
                mValidVisaCard
        );
    }

    public static TestUser EXISTING_USER_BOOKING_CREATION =
            new TestUser(
                    "test+user@handybook.com",
                    "supersecretpassword"
            );

    public static TestUser CANCEL_SINGLE_BOOKING_USER =
            new TestUser(
                    "test+cancel-single-booking@handybook.com",
                    "supersecretpassword"
            );

    public static TestUser UPDATE_PROFILE_USER =
            new TestUser(
                    "test+user2@handybook.com",
                    "supersecretpassword"
            );

    public static TestUser UPDATE_PASSWORD_USER =
            new TestUser(
                    "test+android@handybook.com",
                    "supersecretpassword"
            );

    public static TestUser LOGIN =
            new TestUser(
                    "test+user@handybook.com",
                    "supersecretpassword"
            );

    public static TestUser CANCEL =
            new TestUser(
                    "test+cancel@handybook.com",
                    "supersecretpassword"
            );

    public static TestUser CANCEL_RECURRING_USER = new TestUser(
            "test+cancel-plan@handybook.com",
            "supersecretpassword"
    );
}

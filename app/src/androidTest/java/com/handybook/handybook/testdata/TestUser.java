package com.handybook.handybook.testdata;

public class TestUser
{
    public static TestUser TEST_USER_NY = new TestUser(
            "test+userny@handybook.com",
            "password"
    );

    private final String mEmail;
    private final String mPassword;

    public TestUser(final String email, final String password)
    {
        mEmail = email;
        mPassword = password;
    }

    public String getEmail()
    {
        return mEmail;
    }

    public String getPassword()
    {
        return mPassword;
    }
}

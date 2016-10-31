package com.handybook.handybook.handylayer;

/**
 * Created by jtse on 10/31/16.
 */
public class HandyUser
{
    private String mId;
    private String mUserName;

    public HandyUser(final String id, final String userName)
    {
        mId = id;
        mUserName = userName;
    }

    public String getId()
    {
        return mId;
    }

    public void setId(final String id)
    {
        mId = id;
    }

    public String getUserName()
    {
        return mUserName;
    }

    public void setUserName(final String userName)
    {
        mUserName = userName;
    }
}

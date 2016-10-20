package com.handybook.handybook.booking.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

public class BookingCompleteTransaction
{
    @SerializedName("id")
    private int mId;
    @SerializedName("charge")
    private int mCharge;
    @SerializedName("first_ever_booking")
    private boolean firstEverBooking;
    @SerializedName("user_info")
    private User mUser;
    @SerializedName("instructions")
    private Instructions mInstructions;
    @SerializedName("entry_methods_info")
    private EntryMethodsInfo mEntryMethodsInfo;

    public EntryMethodsInfo getEntryMethodsInfo()
    {
        return mEntryMethodsInfo;
    }

    public int getId()
    {
        return mId;
    }

    public void setId(final int id)
    {
        mId = id;
    }

    public int getCharge()
    {
        return mCharge;
    }

    public boolean isFirstEverBooking()
    {
        return firstEverBooking;
    }

    public User getUser()
    {
        return mUser;
    }

    void setUser(final User user)
    {
        mUser = user;
    }

    public Instructions getInstructions()
    {
        return mInstructions;
    }

    String toJson()
    {
        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .registerTypeAdapter(BookingCompleteTransaction.class,
                        new BookingCompleteTransaction()).create();

        return gson.toJson(this);
    }

    public static BookingCompleteTransaction fromJson(final String json)
    {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                .fromJson(json, BookingCompleteTransaction.class);
    }

    static class BookingCompleteTransactionSerializer
            implements JsonSerializer<BookingCompleteTransaction>
    {
        @Override
        public JsonElement serialize(final BookingCompleteTransaction value, final Type type,
                                     final JsonSerializationContext context)
        {
            final JsonObject jsonObj = new JsonObject();
            jsonObj.add("id", context.serialize(value.getId()));
            jsonObj.add("user_info", context.serialize(value.getUser()));
            jsonObj.add("entry_methods_info", context.serialize(value.getEntryMethodsInfo()));
            jsonObj.add("charge", context.serialize(value.getCharge()));
            jsonObj.add("first_ever_booking", context.serialize(value.isFirstEverBooking()));
            return jsonObj;
        }
    }


    public static class User
    {
        @SerializedName("auth_token")
        private String mAuthToken;
        @SerializedName("id")
        private String mId;

        public String getAuthToken()
        {
            return mAuthToken;
        }

        public String getId()
        {
            return mId;
        }
    }
}

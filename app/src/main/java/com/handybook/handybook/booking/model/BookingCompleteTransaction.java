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
    public static final String ENTRY_METHODS_INFO = "entry_methods_info";
    public static final String CHARGE = "charge";
    public static final String ID = "id";
    public static final String USER_INFO = "user_info";
    public static final String FIRST_EVER_BOOKING = "first_ever_booking";
    @SerializedName(ID)
    private int mId;
    @SerializedName(CHARGE)
    private int mCharge;
    @SerializedName(FIRST_EVER_BOOKING)
    private boolean firstEverBooking;
    @SerializedName(USER_INFO)
    private User mUser;
    @SerializedName("instructions")
    private Instructions mInstructions;
    @SerializedName(ENTRY_METHODS_INFO)
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
        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
                                           .registerTypeAdapter(BookingCompleteTransaction.class,
                        new BookingCompleteTransaction()).create();

        return gson.toJson(this);
    }

    public static BookingCompleteTransaction fromJson(final String json)
    {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").create()
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
            jsonObj.add(ID, context.serialize(value.getId()));
            jsonObj.add(USER_INFO, context.serialize(value.getUser()));
            jsonObj.add(ENTRY_METHODS_INFO, context.serialize(value.getEntryMethodsInfo()));
            jsonObj.add(CHARGE, context.serialize(value.getCharge()));
            jsonObj.add(FIRST_EVER_BOOKING, context.serialize(value.isFirstEverBooking()));
            return jsonObj;
        }
    }


    public static class User
    {
        @SerializedName("auth_token")
        private String mAuthToken;
        @SerializedName(ID)
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

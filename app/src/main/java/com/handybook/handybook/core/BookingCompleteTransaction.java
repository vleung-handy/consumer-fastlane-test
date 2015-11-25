package com.handybook.handybook.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

public class BookingCompleteTransaction {
    @SerializedName("id") private int id;
    @SerializedName("user_info") private User user;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    void setUser(final User user) {
        this.user = user;
    }

    String toJson() {
        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .registerTypeAdapter(BookingCompleteTransaction.class,
                        new BookingCompleteTransaction()).create();

        return gson.toJson(this);
    }

    public static BookingCompleteTransaction fromJson(final String json) {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                .fromJson(json, BookingCompleteTransaction.class);
    }

    static class BookingCompleteTransactionSerializer
            implements JsonSerializer<BookingCompleteTransaction> {
        @Override
        public JsonElement serialize(final BookingCompleteTransaction value, final Type type,
                                           final JsonSerializationContext context) {
            final JsonObject jsonObj = new JsonObject();
            jsonObj.add("id", context.serialize(value.getId()));
            jsonObj.add("user_info", context.serialize(value.getUser()));
            return jsonObj;
        }
    }

    public static class User {
        @SerializedName("auth_token") private String authToken;
        @SerializedName("id") private String id;

        public String getAuthToken() {
            return authToken;
        }

        public String getId() {
            return id;
        }
    }
}

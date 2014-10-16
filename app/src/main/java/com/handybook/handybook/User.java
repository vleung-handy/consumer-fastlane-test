package com.handybook.handybook;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.Observable;

public final class User extends Observable {
    @SerializedName("auth_token") private String authToken;
    @SerializedName("id") private String id;

    String getAuthToken() {
        return authToken;
    }

    void setAuthToken(String authToken) {
        this.authToken = authToken;
        triggerObservers();
    }

    String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
        triggerObservers();
    }

    private void triggerObservers() {
        setChanged();
        notifyObservers();
    }

    static final class UserSerializer implements JsonSerializer<User> {
        @Override
        public final JsonElement serialize(final User value, final Type type,
                                     final JsonSerializationContext context) {
            final JsonObject jsonObj = new JsonObject();
            jsonObj.add("auth_token", context.serialize(value.getAuthToken()));
            jsonObj.add("id", context.serialize(value.getId()));
            return jsonObj;
        }
    }
}

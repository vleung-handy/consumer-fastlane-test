package com.handybook.handybook;

import com.google.gson.annotations.SerializedName;

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
}

package com.handybook.handybook.core;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.Observable;
import java.util.Observer;

public final class BookingUpdateDescriptionTransaction extends Observable {
    @SerializedName("getin") private String getInId;            //Get-In identifier, numeric, corresponds to value on server  . TODO: Sync values to consts here
    @SerializedName("getin_text") private String getInText;     //User entered text that explains additional Get-In information
    @SerializedName("msg_to_pro") private String messageToPro;  //Separate message to pro containing misc information

    public final String getGetInId() {
        return getInId;
    }

    public final void setGetInId(final String getInId) {
        this.getInId = getInId;
        triggerObservers();
    }

    final String getGetInText() {
        return getInText;
    }

    public final void setGetInText(final String getInText) {
        this.getInText = getInText;
        triggerObservers();
    }

    private void triggerObservers() {
        setChanged();
        notifyObservers();
    }

    final String toJson() {
        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setExclusionStrategies(getExclusionStrategy())
                .registerTypeAdapter(BookingUpdateDescriptionTransaction.class,
                        new BookingPostInfoSerializer()).create();

        return gson.toJson(this);
    }

    static BookingUpdateDescriptionTransaction fromJson(final String json) {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                .fromJson(json, BookingUpdateDescriptionTransaction.class);
    }

    static ExclusionStrategy getExclusionStrategy() {
        return new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(final FieldAttributes f) {
                return false;
            }

            @Override
            public boolean shouldSkipClass(final Class<?> clazz) {
                return clazz.equals(Observer.class);
            }
        };
    }

    public String getMessageToPro()
    {
        return messageToPro;
    }

    public void setMessageToPro(String messageToPro)
    {
        this.messageToPro = messageToPro;
    }

    static final class BookingPostInfoSerializer implements JsonSerializer<BookingUpdateDescriptionTransaction> {
        @Override
        public final JsonElement serialize(final BookingUpdateDescriptionTransaction value, final Type type,
                                           final JsonSerializationContext context) {
            final JsonObject jsonObj = new JsonObject();
            jsonObj.add("getin", context.serialize(value.getGetInId()));
            jsonObj.add("getintxt", context.serialize(value.getGetInText()));
            jsonObj.add("msg_to_pro", context.serialize(value.getMessageToPro()));
            return jsonObj;
        }
    }
}

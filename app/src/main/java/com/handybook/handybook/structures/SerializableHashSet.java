package com.handybook.handybook.structures;

import android.support.annotation.NonNull;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashSet;

/**
 * currently used for remembering splash promos in preferences
 *
 * a set that has convenience methods for converting to and from string using Gson
 */
public class SerializableHashSet extends HashSet<String> implements Serializable
{
    //TODO: give this a better name?
    //TODO: how to make this generic type instead of String?

    private static final Type TYPE =
            (new TypeToken<SerializableHashSet>() {}).getType();

    private static final Gson GSON = new Gson();

    public @NonNull String toJson()
    {
        return GSON.toJson(this, TYPE);
    }

    public static @NonNull SerializableHashSet fromJson(String jsonString)
    {
        SerializableHashSet prefsHashSet = null;
        try
        {
            prefsHashSet = GSON.fromJson(jsonString, TYPE);
        }
        catch (Exception e)
        {
            //do nothing
        }
        if(prefsHashSet == null)
        {
            prefsHashSet = new SerializableHashSet();
        }
        return prefsHashSet;
    }

    @Override
    public @NonNull String[] toArray()
    {
        return this.toArray(new String[this.size()]);
    }
}

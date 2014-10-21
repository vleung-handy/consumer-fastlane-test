package com.handybook.handybook;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

public final class Typefaces {
    private static final Hashtable<String, Typeface> cache = new Hashtable<>();

    public static Typeface get(final Context c, final String name){
        synchronized(cache) {
            if (!cache.containsKey(name)) {
                    final Typeface t = Typeface.createFromAsset(c.getAssets(),
                            String.format("fonts/%s", name));
                cache.put(name, t);
            }
            return cache.get(name);
        }
    }
}

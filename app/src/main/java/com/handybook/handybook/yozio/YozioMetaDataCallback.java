package com.handybook.handybook.yozio;

import android.content.Context;

import com.handybook.handybook.core.ApplicationModule;
import com.yozio.android.Yozio;
import com.yozio.android.interfaces.YozioMetaDataCallbackable;

import java.util.HashMap;

import dagger.ObjectGraph;

public class YozioMetaDataCallback implements YozioMetaDataCallbackable {

	public final void onCallback(final Context context, final String targetActivityClassName,
                           final HashMap<String, Object> metaData) {
        final ObjectGraph graph = ObjectGraph.create(new ApplicationModule(context));
        graph.inject(this);

        // launching the activity with meta data using Yozio helper
        // You have to use Yozio.startActivityWithMetaData here, so the analytics will work properly.
        if (targetActivityClassName != null) {
            Yozio.startActivityWithMetaData(context, targetActivityClassName, metaData);
        }
    }
}

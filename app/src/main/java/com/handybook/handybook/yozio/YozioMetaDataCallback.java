package com.handybook.handybook.yozio;

import android.content.Context;

import com.handybook.handybook.core.ApplicationModule;
import com.handybook.handybook.data.Mixpanel;
import com.yozio.android.Yozio;
import com.yozio.android.interfaces.YozioMetaDataCallbackable;

import java.util.HashMap;

import javax.inject.Inject;

import dagger.ObjectGraph;

public class YozioMetaDataCallback implements YozioMetaDataCallbackable {

    @Inject Mixpanel mixpanel;

	public final void onCallback(final Context context, final String targetActivityClassName,
                           final HashMap<String, Object> metaData) {
        final ObjectGraph graph = ObjectGraph.create(new ApplicationModule(context));
        graph.inject(this);

        mixpanel.trackEventYozioInstall(metaData);

        // launching the activity with meta data using Yozio helper
        // You have to use Yozio.startActivityWithMetaData here, so the analytics will work properly.
        if (targetActivityClassName != null) {
            Yozio.startActivityWithMetaData(context, targetActivityClassName, metaData);
        }

        // you can later retrieve the meta data from your started activity via Yozio.getMetaData(intent);
        // see SecondActivity class for example.
    }
}

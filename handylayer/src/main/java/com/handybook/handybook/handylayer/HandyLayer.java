package com.handybook.handybook.handylayer;

import android.app.Application;
import android.content.Context;

import com.layer.sdk.LayerClient;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import dagger.ObjectGraph;
import retrofit.RestAdapter;

/**
 * Created by jtse on 10/31/16.
 */
public class HandyLayer
{
    private final RestAdapter mRestAdapter;
    private final Bus mBus;
    private final Context mContext;
    protected ObjectGraph graph;

    private static HandyLayer mHandyLayer;

    @Inject
    LayerHelper mLayerHelper;

    public static HandyLayer init(
            final RestAdapter restAdapter,
            final Bus bus,
            final Application application
    )
    {
        if (mHandyLayer == null)
        {
            LayerClient.applicationCreated(application);
            mHandyLayer = new HandyLayer(restAdapter, bus, application);
            mHandyLayer.initGraph();
        }

        return mHandyLayer;
    }

    public static HandyLayer getInstance()
    {
        return mHandyLayer;
    }

    private HandyLayer(
            final RestAdapter restAdapter,
            final Bus bus,
            final Context context
    )
    {

        mRestAdapter = restAdapter;
        mBus = bus;
        mContext = context;
    }

    public LayerHelper getLayerHelper()
    {
        return mLayerHelper;
    }

    private void initGraph()
    {
        graph = ObjectGraph.create(new ChatModule(mRestAdapter, mBus, mContext));
        inject(this);
    }

    public final void inject(final Object object)
    {
        graph.inject(object);
    }
}

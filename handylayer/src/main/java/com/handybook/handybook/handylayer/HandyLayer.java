package com.handybook.handybook.handylayer;

import android.app.Application;
import android.content.Context;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import dagger.ObjectGraph;
import retrofit.RestAdapter;

/**
 * Created by jtse on 10/31/16.
 */
public class HandyLayer extends Application
{
    private final RestAdapter mRestAdapter;
    private final HandyUser mUser;
    private final Bus mBus;
    private final Context mContext;
    protected ObjectGraph graph;

    private static HandyLayer mHandyLayer;

    @Inject
    static LayerHelper mLayerHelper;

    public static LayerHelper init(
            RestAdapter restAdapter,
            HandyUser user,
            Bus bus,
            Context context
    )
    {

        if (mHandyLayer == null)
        {
            mHandyLayer = new HandyLayer(restAdapter, user, bus, context);
            mHandyLayer.initGraph();
        }

        return mLayerHelper;
    }

    public static HandyLayer getInstance()
    {
        return mHandyLayer;
    }

    private HandyLayer(
            final RestAdapter restAdapter,
            final HandyUser user,
            final Bus bus,
            final Context context
    )
    {

        mRestAdapter = restAdapter;
        mUser = user;
        mBus = bus;
        mContext = context;
    }

    private void initGraph()
    {
        graph = ObjectGraph.create(new ChatModule(mRestAdapter, mUser, mBus, mContext));
        inject(this);
    }

    public final void inject(final Object object)
    {
        graph.inject(object);
    }
}

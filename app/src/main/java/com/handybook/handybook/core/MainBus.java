package com.handybook.handybook.core;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

final class MainBus extends Bus
{
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public MainBus()
    {
    }

    @Override
    public final void register(final Object object)
    {
        if (Looper.myLooper() == Looper.getMainLooper()) super.register(object);
        else
        {
            mHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    MainBus.super.register(object);
                }
            });
        }
    }

    @Override
    public void post(final Object event)
    {
        if (Looper.myLooper() == Looper.getMainLooper())
        {
            super.post(event);
        }
        else
        {
            mHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    MainBus.super.post(event);
                }
            });
        }
    }
}

package com.handybook.handybook.core;

import com.handybook.handybook.RobolectricGradleTestWrapper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class MainBusTest extends RobolectricGradleTestWrapper {

    private EventBus mBus = new MainBus();

    private boolean mBusCalled;

    @Before
    public void setUp() throws Exception {
        mBus.register(this);
    }

    @After
    public void tearDown() throws Exception {
        mBus.unregister(this);
    }

    @Subscribe
    public void subscribeMethod(Trigger event) {
        mBusCalled = true;
    }

    @Test
    public void testSubscribeMethod() {
        mBus.post(new Trigger());
        assertTrue(mBusCalled);
    }

    @Test
    public void testForceRegistrationOnMainLooper() throws Exception {

        // register on bus outside of the main looper
        Thread newThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mBus.register(MainBusTest.this); // assumes the same bus is shared between threads
            }
        });

        newThread.start();
        newThread.join();

        mBus.post(new Trigger());

        assertTrue(mBusCalled);
    }

    static class Trigger {}
}

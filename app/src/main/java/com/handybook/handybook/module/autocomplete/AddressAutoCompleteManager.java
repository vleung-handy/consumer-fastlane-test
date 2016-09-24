package com.handybook.handybook.module.autocomplete;

import com.handybook.handybook.data.HandyRetrofitService;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Created by jtse on 9/24/16.
 */
public class AddressAutoCompleteManager
{
    private final HandyRetrofitService mService;
    private final Bus mBus;

    @Inject
    public AddressAutoCompleteManager(final Bus bus, final HandyRetrofitService service)
    {
        this.mBus = bus;
        this.mBus.register(this);
        this.mService = service;
    }

    AddressPredictionResponse getAddressPrediction(String word) {
        return mService.getAddressPrediction(word);
    }
}

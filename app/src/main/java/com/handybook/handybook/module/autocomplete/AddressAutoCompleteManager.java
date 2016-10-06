package com.handybook.handybook.module.autocomplete;

import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Created by jtse on 9/24/16.
 */
public class AddressAutoCompleteManager
{
    private final PlacesService mService;
    private final Bus mBus;

    @Inject
    public AddressAutoCompleteManager(final Bus bus, final PlacesService service)
    {
        this.mBus = bus;
        this.mBus.register(this);
        this.mService = service;
    }

    /**
     * Note: This is a synchronous call, and should not be made from the UI thread.
     *
     * @param word
     * @return
     */
    PlacePredictionResponse getAddressPrediction(String word)
    {
        return mService.getAddressPrediction(word);
    }
}

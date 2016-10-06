package com.handybook.handybook.module.autocomplete;

import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.AddressAutocompleteLog;
import com.squareup.otto.Bus;

import javax.inject.Inject;

public class AddressAutoCompleteManager
{
    private final PlacesService mService;
    private final Bus mBus;

    @Inject
    public AddressAutoCompleteManager(final Bus bus, final PlacesService service)
    {
        this.mBus = bus;
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
        mBus.post(new LogEvent.AddLogEvent(new AddressAutocompleteLog.AddressAutocompleteRequestLog(word)));
        PlacePredictionResponse response = mService.getAddressPrediction(word);

        int count = (response == null || response.predictions == null) ? 0 : response.predictions.size();
        mBus.post(new LogEvent.AddLogEvent(new AddressAutocompleteLog.AddressAutocompleteResponseLog(count)));

        return response;
    }
}

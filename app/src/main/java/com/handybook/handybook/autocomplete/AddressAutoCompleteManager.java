package com.handybook.handybook.autocomplete;

import android.support.annotation.NonNull;

import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.AddressAutocompleteLog;
import com.squareup.otto.Bus;

import javax.inject.Inject;

public class AddressAutoCompleteManager {

    private final PlacesService mService;
    private final Bus mBus;

    @Inject
    public AddressAutoCompleteManager(final Bus bus, final PlacesService service) {
        this.mBus = bus;
        this.mService = service;
    }

    /**
     * Note: This is a synchronous call, and should not be made from the UI thread.
     *
     * @param word
     * @return
     */
    @NonNull
    public PlacePredictionResponse getAddressPrediction(String word) {
        mBus.post(new LogEvent.AddLogEvent(
                new AddressAutocompleteLog.AddressAutocompleteRequestLog(word)
        ));
        PlacePredictionResponse response = mService.getAddressPrediction(word);

        if (response == null) {
            response = new PlacePredictionResponse();
        }
        mBus.post(new LogEvent.AddLogEvent(
                new AddressAutocompleteLog.AddressAutocompleteResponseLog(response.predictions.size())
        ));
        return response;
    }
}

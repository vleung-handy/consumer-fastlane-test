package com.handybook.handybook.module.autocomplete;

import android.support.annotation.NonNull;
import android.util.Log;

import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.AddressAutocompleteLog;
import com.squareup.otto.Bus;

import javax.inject.Inject;

public class AddressAutoCompleteManager
{
    private static final String TAG = "AddressAutoCompleteMana";
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
    @NonNull
    PlacePredictionResponse getAddressPrediction(String word)
    {
        Log.d(TAG, "getAddressPrediction() called with: word = [" + word + "]");

        mBus.post(new LogEvent.AddLogEvent(new AddressAutocompleteLog.AddressAutocompleteRequestLog(word)));

        if (mService == null)
        {
            Log.d(TAG, "getAddressPrediction: mService is null");
        }
        PlacePredictionResponse response = mService.getAddressPrediction(word);

        if (response == null)
        {
            Log.d(TAG, "getAddressPrediction: response is null");
            response = new PlacePredictionResponse();
        }
        Log.d(TAG, "getAddressPrediction: got response");
        mBus.post(new LogEvent.AddLogEvent(new AddressAutocompleteLog.AddressAutocompleteResponseLog(response.predictions.size())));
        return response;
    }
}

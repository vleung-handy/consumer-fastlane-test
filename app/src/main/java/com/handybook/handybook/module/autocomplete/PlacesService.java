package com.handybook.handybook.module.autocomplete;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by jtse on 10/6/16.
 */
public interface PlacesService
{
    /**
     * Synchronous API call (should not be called from the UI thread)
     *
     * @param input
     * @return
     */
    @GET("/maps/api/place/autocomplete/json?types=address")
    PlacePredictionResponse getAddressPrediction(@Query("input") String input);

}

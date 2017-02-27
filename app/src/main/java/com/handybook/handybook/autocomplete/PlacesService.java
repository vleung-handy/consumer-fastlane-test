package com.handybook.handybook.autocomplete;

import retrofit.http.GET;
import retrofit.http.Query;

public interface PlacesService {

    /**
     * Synchronous API call (should not be called from the UI thread)
     *
     * @param input
     * @return
     */
    @GET("/maps/api/place/autocomplete/json?types=address")
    PlacePredictionResponse getAddressPrediction(@Query("input") String input);

}

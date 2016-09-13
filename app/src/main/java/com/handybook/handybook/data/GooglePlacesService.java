package com.handybook.handybook.data;

import com.handybook.handybook.module.autocomplete.PlacePredictionResponse;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 */
public interface GooglePlacesService
{
    /**
     * This is a synchronous call to get address autocomplete
     *
     * @param input
     */
    @GET("/maps/api/place/autocomplete/json?types=address")
    PlacePredictionResponse getAddressPrediction(@Query("input") String input);
}

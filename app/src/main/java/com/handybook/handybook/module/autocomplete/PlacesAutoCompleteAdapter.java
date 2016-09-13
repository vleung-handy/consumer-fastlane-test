package com.handybook.handybook.module.autocomplete;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.handybook.handybook.data.GooglePlacesService;

import java.util.List;

/**
 */
public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable
{
    private static final String TAG = PlacesAutoCompleteAdapter.class.getName();

    List<String> mPredictionValues;
    List<PlacePrediction> mPredictions;

    Context mContext;
    int mResource;

    GooglePlacesService mPlaceService;

    public PlacesAutoCompleteAdapter(Context context, int resource, GooglePlacesService placesService)
    {
        super(context, resource);

        mContext = context;
        mResource = resource;
        mPlaceService = placesService;
    }

    @Override
    public int getCount()
    {
        // Last item will be the footer
        return mPredictionValues.size();
    }

    @Override
    public String getItem(int position)
    {
        return mPredictionValues.get(position);
    }

    public PlacePrediction getPrediction(int position)
    {
        return mPredictions.get(position);
    }

    @Override
    public Filter getFilter()
    {
        Filter filter = new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                Log.d(TAG, "performFiltering: start filtering");
                FilterResults filterResults = new FilterResults();
                if (constraint != null)
                {
                    PlacePredictionResponse response = mPlaceService.getAddressPrediction(constraint.toString());
                    response.filter();
                    mPredictions = response.predictions;
                    mPredictionValues = response.getDescriptions();
                    filterResults.values = mPredictionValues;
                    filterResults.count = mPredictionValues.size();
                }

                Log.d(TAG, "performFiltering: returning filters of size:" + filterResults.count);

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results)
            {
                if (results != null && results.count > 0)
                {
                    notifyDataSetChanged();
                }
                else
                {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }
}

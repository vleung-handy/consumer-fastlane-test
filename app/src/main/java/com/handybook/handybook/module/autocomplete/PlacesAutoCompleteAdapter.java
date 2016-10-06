package com.handybook.handybook.module.autocomplete;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.handybook.handybook.booking.model.ZipValidationResponse;

import java.util.List;

/**
 */
public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable
{
    List<String> mPredictionValues;
    List<PlacePrediction> mPredictions;

    Context mContext;
    int mResource;

    AddressAutoCompleteManager mDataManager;
    ZipValidationResponse.ZipArea mZipAreaFilter;

    public PlacesAutoCompleteAdapter(
            final Context context,
            final int resource,
            final AddressAutoCompleteManager dataManager,
            final ZipValidationResponse.ZipArea filter
    )
    {
        super(context, resource);

        mContext = context;
        mResource = resource;
        mDataManager = dataManager;
        mZipAreaFilter = filter;
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
            /**
             * Note that this is invoked in a worker thread.
             *
             * @param constraint
             * @return
             */
            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                FilterResults filterResults = new FilterResults();
                if (constraint != null)
                {
                    PlacePredictionResponse response = mDataManager.getAddressPrediction(constraint.toString());
                    response.filter(mZipAreaFilter);
                    mPredictions = response.predictions;
                    mPredictionValues = response.getFullAddresses();
                    filterResults.values = mPredictionValues;
                    filterResults.count = mPredictionValues.size();
                }

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

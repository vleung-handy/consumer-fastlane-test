package com.handybook.handybook.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This view shows the error message letting user know there is a location missing. Either booking
 * location, pro location or both
 */
public class MissingLocationView extends FrameLayout
{

    @Bind(R.id.text_error_message)
    TextView mTextErrorMessage;

    @Bind(R.id.image_error_icon)
    ImageView mImageError;

    public MissingLocationView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        inflate(getContext(), R.layout.missing_location_view, this);
        ButterKnife.bind(this);
    }

    public void missingBookingLocation()
    {
        mTextErrorMessage.setText(getResources().getString(R.string.missing_booking_location));
    }

    public void missingProviderLocation()
    {
        mTextErrorMessage.setText(getResources().getString(R.string.missing_provider_location));
    }

}

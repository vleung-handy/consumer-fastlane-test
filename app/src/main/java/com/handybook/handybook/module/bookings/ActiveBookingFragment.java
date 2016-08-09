package com.handybook.handybook.module.bookings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 */
public class ActiveBookingFragment extends Fragment implements OnMapReadyCallback
{
    private static final String TAG = ActiveBookingFragment.class.getName();
    private static final String KEY_BOOKING = "booking";

    @Bind(R.id.map_container)
    View mMapContainer;

    @Bind(R.id.text_start_soon_indicator)
    View mStartingSoonIndicator;

    @Bind(R.id.text_provider_name)
    TextView mTextProviderName;

    @Bind(R.id.text_booking_title)
    TextView mTextBookingTitle;

    @Bind(R.id.text_booking_subtitle)
    TextView mTextBookingSubtitle;

    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;
    private Booking mBooking;

    //    FIXME -- use API value for this.
    private boolean mShouldShowMap = true;

    public static ActiveBookingFragment newInstance(Booking booking)
    {
        Bundle args = new Bundle();
        args.putParcelable(KEY_BOOKING, booking);
        ActiveBookingFragment fragment = new ActiveBookingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_active_booking, container, false);
        ButterKnife.bind(this, view);

        if (getArguments() != null)
        {
            mBooking = getArguments().getParcelable(KEY_BOOKING);
        }

        if (mBooking != null && mShouldShowMap)
        {
            mMapFragment = SupportMapFragment.newInstance();
            mMapFragment.getMapAsync(this);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.map_container, mMapFragment);
            fragmentTransaction.commit();
            mMapContainer.setVisibility(View.VISIBLE);

            if (mBooking.getProvider() != null)
            {
                String name = mBooking.getProvider().getFirstName();

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(mBooking.getProvider().getLastName()))
                {
                    name += " " + mBooking.getProvider().getLastName().charAt(0) + ".";
                }

                mTextProviderName.setText(name);
            }

            mTextBookingTitle.setText(BookingHelper.getTitle(mBooking));
            mTextBookingSubtitle.setText(BookingHelper.getSubtitle(mBooking, getActivity()));
            mStartingSoonIndicator.setVisibility(View.VISIBLE);
        }
        else
        {
            mMapContainer.setVisibility(View.GONE);
            mStartingSoonIndicator.setVisibility(View.GONE);
        }

        return view;
    }

    private void updateMap()
    {
        if (mBooking != null)
        {
            double lat = mBooking.getAddress().getLatitude();
            double lng = mBooking.getAddress().getLongitude();

            LatLng latLng = new LatLng(lat, lng);
            Log.d(TAG, "updateMap: plotting: " + lat + ", " + lng);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            mGoogleMap.animateCamera(cameraUpdate);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap)
    {
        Log.d(TAG, "onMapReady: ");
        if (mGoogleMap == null)
        {
            mGoogleMap = googleMap;
            updateMap();
        }
    }
}

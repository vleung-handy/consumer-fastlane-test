package com.handybook.handybook.module.bookings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.util.BookingUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @Bind(R.id.start_soon_indicator_divider)
    View mStartingSoonIndicatorDivider;

    @Bind(R.id.text_provider_name)
    TextView mTextProviderName;

    @Bind(R.id.text_booking_title)
    TextView mTextBookingTitle;

    @Bind(R.id.text_booking_subtitle)
    TextView mTextBookingSubtitle;

    @Bind(R.id.profile_container)
    View mProfileContainer;

    @Bind(R.id.profile_container_divider)
    View mProfileContainerDivider;

    @Bind(R.id.image_call)
    ImageView mImageCall;

    @Bind(R.id.image_text)
    ImageView mImageText;

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
        Log.d(TAG, "onCreateView: ");
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

            Log.d(TAG, "onCreateView: inserting map fragment into map_container");

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.map_container, mMapFragment);
            fragmentTransaction.commit();
            mMapContainer.setVisibility(View.VISIBLE);

            if (mBooking.getProvider() != null)
            {
                mProfileContainer.setVisibility(View.VISIBLE);
                mProfileContainerDivider.setVisibility(View.VISIBLE);
                String name = mBooking.getProvider().getFirstName();

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(mBooking.getProvider().getLastName()))
                {
                    name += " " + mBooking.getProvider().getLastName().charAt(0) + ".";
                }

                mTextProviderName.setText(name);

                if (!TextUtils.isEmpty(mBooking.getProvider().getPhone()))
                {
                    mImageCall.setVisibility(View.VISIBLE);
                    mImageText.setVisibility(View.VISIBLE);
                }
                else
                {
                    mImageCall.setVisibility(View.GONE);
                    mImageText.setVisibility(View.GONE);
                }
            }
            else
            {
                //no provider is here, shouldn't happen, but if it doesn, hide the provider card.
                mProfileContainer.setVisibility(View.GONE);
                mProfileContainerDivider.setVisibility(View.GONE);
            }

            mTextBookingTitle.setText(BookingUtil.getTitle(mBooking));
            mTextBookingSubtitle.setText(BookingUtil.getSubtitle(mBooking, getActivity()));
            mStartingSoonIndicator.setVisibility(View.VISIBLE);
            mStartingSoonIndicatorDivider.setVisibility(View.VISIBLE);
        }
        else
        {
            mMapContainer.setVisibility(View.GONE);
            mStartingSoonIndicator.setVisibility(View.GONE);
            mStartingSoonIndicatorDivider.setVisibility(View.GONE);
        }

        return view;
    }

    private void updateMap()
    {
        if (mBooking != null)
        {
            double lat = mBooking.getAddress().getLatitude();
            double lng = mBooking.getAddress().getLongitude();

            LatLng addressLatLng = new LatLng(lat, lng);
            Log.d(TAG, "updateMap: plotting: " + lat + ", " + lng);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(addressLatLng, 15);
            mGoogleMap.moveCamera(cameraUpdate);
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(addressLatLng)
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin)));

            //FIXME: replace this with pro location information when it's there.
            LatLng providerLatLng = new LatLng(40.741899, -73.998602);
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(providerLatLng)
                    .title("Provider")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pro_location)));
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

    @OnClick(R.id.profile_container)
    public void profileClicked()
    {
        gotoBookingDetails();
    }

    @OnClick(R.id.booking_item_container)
    public void bookingClicked()
    {
        gotoBookingDetails();
    }

    @OnClick(R.id.image_call)
    public void callClicked()
    {
        BookingUtil.callPhoneNumber(mBooking.getProvider().getPhone(), this.getActivity());
    }

    @OnClick(R.id.image_text)
    public void textClicked()
    {
        BookingUtil.textPhoneNumber(mBooking.getProvider().getPhone(), this.getActivity());
    }

    private void gotoBookingDetails()
    {
        final Intent intent = new Intent(getActivity(), BookingDetailActivity.class);
        intent.putExtra(BundleKeys.BOOKING, mBooking);
        getActivity().startActivityForResult(intent, ActivityResult.BOOKING_UPDATED);
    }
}

package com.handybook.handybook.module.bookings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.util.BookingUtil;
import com.handybook.handybook.util.PlayServicesUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 */
public class ActiveBookingFragment extends Fragment implements OnMapReadyCallback
{
    private static final String TAG = ActiveBookingFragment.class.getName();
    private static final String KEY_BOOKING = "booking";
    private static final String KEY_TOUCH_LISTENER = "touch_listener";

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

    @Bind(R.id.map_view)
    MapView mMapView;

    @Bind(R.id.transparent_image)
    ImageView mTransparentImage;

    private GoogleMap mGoogleMap;
    private Booking mBooking;

    //    FIXME -- use API value for this.
    private boolean mShouldShowMap = true;
    private ScrollView mParentScrollView;

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

            if (PlayServicesUtils.hasPlayServices(getActivity()))
            {
                mMapView.onCreate(savedInstanceState);
                mMapView.setVisibility(View.VISIBLE);
                mMapView.getMapAsync(this);

                if (mParentScrollView != null)
                {
                    mTransparentImage.setOnTouchListener(new View.OnTouchListener()
                    {

                        @Override
                        public boolean onTouch(View v, MotionEvent event)
                        {
                            int action = event.getAction();
                            switch (action)
                            {
                                case MotionEvent.ACTION_DOWN:
                                    // Disallow ScrollView to intercept touch events.
                                    mParentScrollView.requestDisallowInterceptTouchEvent(true);
                                    // Disable touch on transparent view
                                    return false;

                                case MotionEvent.ACTION_UP:
                                    // Allow ScrollView to intercept touch events.
                                    mParentScrollView.requestDisallowInterceptTouchEvent(false);
                                    return true;

                                case MotionEvent.ACTION_MOVE:
                                    mParentScrollView.requestDisallowInterceptTouchEvent(true);
                                    return false;

                                default:
                                    return true;
                            }
                        }
                    });
                }

            }
            else
            {
                mMapView.setVisibility(View.GONE);
            }

            if (mBooking.getProvider() != null && !TextUtils.isEmpty(mBooking.getProvider().getFullName().trim()))
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
            mMapView.setVisibility(View.GONE);
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

            //FIXME: remove this.
            if (lat == 0 && lng == 0)
            {
                Toast.makeText(this.getActivity(), "No address location, faking it.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "updateMap: address not returning lat/lng, hard coding something");
                lat = 40.7399124;
                lng = -73.9953073;
            }

            LatLng addressLatLng = new LatLng(lat, lng);
            Log.d(TAG, "updateMap: plotting: " + lat + ", " + lng);

            mGoogleMap.addMarker(new MarkerOptions()
                    .position(addressLatLng)
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin)));

            //FIXME: replace this with pro location information when it's there.
            Booking.Location providerLocation = mBooking.getActiveBookingStatus().getProviderLocation();

            LatLng providerLatLng;
            if (!isBadLocation(providerLocation))
            {
                providerLatLng = new LatLng(
                        Double.valueOf(providerLocation.getLatitude()),
                        Double.valueOf(providerLocation.getLongitude())
                );
            }
            else
            {
                //FIXME: remove these fake data.
                Log.e(TAG, "updateMap: provider location not returning lat/lng, hard coding something");
                Toast.makeText(this.getActivity(), "No provider location, faking it.", Toast.LENGTH_SHORT).show();
                providerLatLng = new LatLng(40.741899, -73.998602);
            }

            mGoogleMap.addMarker(new MarkerOptions()
                    .position(providerLatLng)
                    .title("Provider")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pro_location)));

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(addressLatLng);
            builder.include(providerLatLng);
            LatLngBounds bounds = builder.build();

            //gives it some padding, so that the markers are not right at the edge of the screen.
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.30);

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mGoogleMap.moveCamera(cu);
        }
    }

    /**
     * A bad location is null, empty string, or 0
     *
     * @param str
     * @return
     */
    private boolean isBadLocation(Booking.Location location)
    {
        return location == null ||
                TextUtils.isEmpty(location.getLatitude()) ||
                TextUtils.isEmpty(location.getLongitude()) ||
                Double.valueOf(location.getLatitude()) == 0 ||
                Double.valueOf(location.getLongitude()) == 0;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap)
    {
        Log.d(TAG, "onMapReady: ");
        if (mGoogleMap == null)
        {
            mGoogleMap = googleMap;

            // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
            MapsInitializer.initialize(this.getActivity());

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

    @Override
    public void onResume()
    {
        if (mMapView.getVisibility() == View.VISIBLE)
        {
            mMapView.onResume();
        }
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        if (mMapView.getVisibility() == View.VISIBLE)
        {
            mMapView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();

        if (mMapView.getVisibility() == View.VISIBLE)
        {
            mMapView.onLowMemory();
        }

    }

    public void setParentScrollView(final ScrollView parentScrollView)
    {
        mParentScrollView = parentScrollView;
    }
}

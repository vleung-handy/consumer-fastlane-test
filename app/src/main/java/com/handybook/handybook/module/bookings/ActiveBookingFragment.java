package com.handybook.handybook.module.bookings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingGeoStatus;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.view.MapPlaceholderView;
import com.handybook.handybook.util.BookingUtil;
import com.handybook.handybook.util.DateTimeUtils;
import com.handybook.handybook.util.PlayServicesUtils;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This fragment contains only the information regarding one active booking.
 * It holds the map, plots the location of both the provider and the destination.
 * It pings the server for updated provider location information periodically.
 * Also shows the provider's name, and allows calling/texting to the provider.
 */
public class ActiveBookingFragment extends InjectedFragment implements OnMapReadyCallback
{
    private static final String KEY_BOOKING = "booking";
    private static final int GEO_STATUS_PING_INTERVAL_MS = 10000;  //ping for geo status every 10 seconds

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

    @Bind(R.id.text_call)
    TextView mTextCall;

    @Bind(R.id.text_text)
    TextView mTextText;

    @Bind(R.id.booking_item_container)
    View mBookingItemContainer;

    @Bind(R.id.map_view)
    MapView mMapView;

    @Bind(R.id.transparent_image)
    ImageView mTransparentImage;

    @Bind(R.id.text_pro_location_time)
    TextView mTextLocationTime;

    @Bind(R.id.map_place_holder)
    MapPlaceholderView mMapPlaceHolderView;

    private GoogleMap mGoogleMap;
    private Booking mBooking;
    private Marker mProviderLocationMarker;
    private String mProviderName;
    private LatLng mProviderLatLng;
    private LatLng mAddressLatLng;
    private boolean mFirstZoom = true;
    private Handler mHandler;
    private Runnable mRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            requestGeoStatus();
            periodicUpdate();
        }
    };

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
        View view = inflater.inflate(R.layout.fragment_active_booking, container, false);
        ButterKnife.bind(this, view);

        if (getArguments() != null)
        {
            mBooking = getArguments().getParcelable(KEY_BOOKING);
        }

        if (mBooking != null)
        {

            if (PlayServicesUtils.hasPlayServices(getActivity()))
            {
                mMapView.onCreate(savedInstanceState);
                mMapView.getMapAsync(this);
                mMapView.setVisibility(View.VISIBLE);
                mMapPlaceHolderView.setVisibility(View.GONE);
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
                mMapPlaceHolderView.setVisibility(View.VISIBLE);
            }

            if (mBooking.getProvider() != null && !TextUtils.isEmpty(mBooking.getProvider().getFullName().trim()))
            {
                mProfileContainer.setVisibility(View.VISIBLE);
                mProfileContainerDivider.setVisibility(View.VISIBLE);
                mProviderName = mBooking.getProvider().getFirstNameAndLastInitial();
                mTextProviderName.setText(mProviderName);

                if (!TextUtils.isEmpty(mBooking.getProvider().getPhone()))
                {
                    mTextCall.setVisibility(View.VISIBLE);
                    mTextText.setVisibility(View.VISIBLE);
                }
                else
                {
                    mTextCall.setVisibility(View.GONE);
                    mTextText.setVisibility(View.GONE);
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

            mAddressLatLng = new LatLng(lat, lng);

            mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener()
            {
                @Override
                public void onCameraChange(final CameraPosition cameraPosition)
                {
                    float maxZoom = 16.0f;
                    if (cameraPosition.zoom > maxZoom)
                    {
                        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(maxZoom));
                    }
                }
            });

            mGoogleMap.addMarker(new MarkerOptions()
                    .position(mAddressLatLng)
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin)));

            Booking.Location providerLocation = mBooking.getActiveBookingStatus().getProviderLocation();

            if (!isBadLocation(providerLocation))
            {
                mProviderLatLng = new LatLng(
                        Double.valueOf(providerLocation.getLatitude()),
                        Double.valueOf(providerLocation.getLongitude())
                );

                setTimeStamp(mBooking.getActiveBookingStatus().getProviderLocation().getTimeStamp());

                mProviderLocationMarker = mGoogleMap.addMarker(
                        new MarkerOptions()
                                .position(mProviderLatLng)
                                .title(mProviderName)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pro_location))
                );

                if (mHandler == null)
                {
                    //start the handler to ping for location change every time
                    mHandler = new Handler();
                    periodicUpdate();
                }

            }
            else
            {
                Crashlytics.logException(new RuntimeException("Active booking enabled, but no pro location"));
            }

            adjustMapPositioning();

        }
    }

    private void setTimeStamp(Date timeStamp)
    {
        if (timeStamp != null)
        {
            String time = DateTimeUtils.getTime(timeStamp);
            mTextLocationTime.setText(getResources().getString(R.string.pro_location_time_formatted, time));
            mTextLocationTime.setVisibility(View.VISIBLE);
        }
        else
        {
            mTextLocationTime.setVisibility(View.GONE);
        }
    }

    private void periodicUpdate()
    {
        if (mHandler != null)
        {
            mHandler.postDelayed(mRunnable, GEO_STATUS_PING_INTERVAL_MS);
        }
    }

    private String getThisName()
    {
        return String.valueOf(ActiveBookingFragment.this.hashCode());
    }

    private void requestGeoStatus()
    {
        dataManager.getBookingGeoStatus(mBooking.getId(), new DataManager.Callback<BookingGeoStatus>()
        {
            @Override
            public void onSuccess(final BookingGeoStatus response)
            {
                mProviderLatLng = new LatLng(response.getProLat(), response.getProLng());
                adjustMapPositioning();
                setTimeStamp(response.getTimeStamp());
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                //don't worry about it.
            }
        });
    }

    private void adjustMapPositioning()
    {
        if (mGoogleMap == null || !isAdded())
        {
            return;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(mAddressLatLng);

        if (mProviderLatLng != null)
        {
            builder.include(mProviderLatLng);
            mProviderLocationMarker.setPosition(mProviderLatLng);
        }

        if (mFirstZoom)
        {
            LatLngBounds bounds = builder.build();

            //gives it some padding, so that the markers are not right at the edge of the screen.
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDimensionPixelSize(R.dimen.active_booking_map_height);
            int padding = getResources().getDimensionPixelOffset(R.dimen.default_padding_4x);

            //first zoom to enclose all the markers.
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mGoogleMap.moveCamera(cu);
            mFirstZoom = false;
        }
    }

    /**
     * A bad location is null, empty string, or 0
     *
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
        if (mGoogleMap == null)
        {
            mGoogleMap = googleMap;

            if (this.getActivity() != null)
            {
                // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
                MapsInitializer.initialize(this.getActivity());
                updateMap();
            }
        }
    }

    @Override
    public void onAttach(final Context context)
    {
        super.onAttach(context);

        if (mGoogleMap != null)
        {
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

    @OnClick(R.id.text_call)
    public void callClicked()
    {
        BookingUtil.callPhoneNumber(mBooking.getProvider().getPhone(), this.getActivity());
    }

    @OnClick(R.id.text_text)
    public void textClicked()
    {
        BookingUtil.textPhoneNumber(mBooking.getProvider().getPhone(), this.getActivity());
    }

    @OnClick(R.id.button_report_issue)
    public void reportIssueClicked()
    {
        //FIXME: JIA: implement this to hook up with Report Issue before go live.
        Toast.makeText(getActivity(), "To be implemented", Toast.LENGTH_SHORT).show();
    }


    private void gotoBookingDetails()
    {
        final Intent intent = new Intent(getActivity(), BookingDetailActivity.class);
        intent.putExtra(BundleKeys.BOOKING, mBooking);
        getActivity().startActivityForResult(intent, ActivityResult.BOOKING_UPDATED);
    }

    private boolean isMapBeingShown()
    {
        return mMapView != null && mMapView.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onResume()
    {
        if (isMapBeingShown())
        {
            mMapView.onResume();
        }
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        //make sure this step is done, otherwise there will be a memory leak.
        if (mHandler != null)
        {
            mHandler.removeCallbacks(mRunnable);
            mHandler = null;
        }
    }

    @Override
    public void onDestroy()
    {
        if (isMapBeingShown())
        {
            mMapView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();

        if (isMapBeingShown())
        {
            mMapView.onLowMemory();
        }

    }

    public void setParentScrollView(final ScrollView parentScrollView)
    {
        mParentScrollView = parentScrollView;
    }
}

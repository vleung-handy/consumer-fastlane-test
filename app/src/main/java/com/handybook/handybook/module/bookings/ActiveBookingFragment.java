package com.handybook.handybook.module.bookings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

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
import com.handybook.handybook.util.BookingUtil;
import com.handybook.handybook.util.DateTimeUtils;
import com.handybook.handybook.util.PlayServicesUtils;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 */
public class ActiveBookingFragment extends InjectedFragment implements OnMapReadyCallback
{
    private static final String TAG = ActiveBookingFragment.class.getName();
    private static final String KEY_BOOKING = "booking";
    private static final int GEO_STATUS_PING_INTERVAL = 10000;  //ping for geo status every 10 seconds

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

    @Bind(R.id.map_container)
    FrameLayout mMapContainer;

    @Bind(R.id.transparent_image)
    ImageView mTransparentImage;

    @Bind(R.id.text_pro_location_time)
    TextView mTextLocationTime;

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
            Log.d(TAG, getThisName() + "ping for location data: ");
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
        Log.d(TAG, "onCreateView: ");
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
                mMapContainer.setVisibility(View.VISIBLE);

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
                mMapContainer.setVisibility(View.GONE);
            }

            if (mBooking.getProvider() != null && !TextUtils.isEmpty(mBooking.getProvider().getFullName().trim()))
            {
                mProfileContainer.setVisibility(View.VISIBLE);
                mProfileContainerDivider.setVisibility(View.VISIBLE);
                mProviderName = mBooking.getProvider().getFirstName();

                if (!TextUtils.isEmpty(mProviderName) && !TextUtils.isEmpty(mBooking.getProvider().getLastName()))
                {
                    mProviderName += " " + mBooking.getProvider().getLastName().charAt(0) + ".";
                }

                mTextProviderName.setText(mProviderName);

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

            mAddressLatLng = new LatLng(lat, lng);
            Log.d(TAG, "updateMap: plotting: " + lat + ", " + lng);

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

                //FIXME: JIA: remove this hard code
                String time = DateTimeUtils.getTime(new Date());
                mTextLocationTime.setText(getResources().getString(R.string.pro_location_time_formatted, time));

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

            adjustMap();

        }
    }

    private void periodicUpdate()
    {
        Log.d(TAG, "periodicUpdate: " + getThisName());
        if (mHandler != null)
        {
            mHandler.postDelayed(mRunnable, GEO_STATUS_PING_INTERVAL);
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
                adjustMap();

                //FIXME: JIA: remove this hard code
                String time = DateTimeUtils.getTime(new Date());
                mTextLocationTime.setText(getResources().getString(R.string.pro_location_time_formatted, time));
                Log.d(TAG, "receive geo status success: " + response.getProLat() + "," + response.getProLng());
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                //don't worry about it.
            }
        });
    }

    private void adjustMap()
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
        Log.d(TAG, "onMapReady: ");
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
        if (mMapView != null && mMapView.getVisibility() == View.VISIBLE)
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
        if (mMapView != null && mMapView.getVisibility() == View.VISIBLE)
        {
            mMapView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();

        if (mMapView != null && mMapView.getVisibility() == View.VISIBLE)
        {
            mMapView.onLowMemory();
        }

    }

    public void setParentScrollView(final ScrollView parentScrollView)
    {
        mParentScrollView = parentScrollView;
    }
}

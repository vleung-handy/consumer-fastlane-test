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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.booking.ui.activity.ReportIssueActivity;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.ActiveBookingLog;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.view.MapPlaceholderView;
import com.handybook.handybook.ui.view.MissingLocationView;
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
    private static final float MAP_CLOSEUP_ZOOM_LEVEL = 16;
    private static final float ANCHOR_MID_POINT = 0.5f;

    @Bind(R.id.map_divider)
    View mMapDivider;

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

    @Bind(R.id.report_an_issue_container)
    View mReportIssueContainer;

    @Bind(R.id.map_view)
    MapView mMapView;

    @Bind(R.id.transparent_image)
    ImageView mTransparentImage;

    @Bind(R.id.text_pro_location_time)
    TextView mTextLocationTime;

    @Bind(R.id.map_place_holder)
    MapPlaceholderView mMapPlaceHolderView;

    @Bind(R.id.missing_location_view)
    MissingLocationView mMissingLocationView;

    private GoogleMap mGoogleMap;
    private Booking mBooking;
    private Marker mProviderLocationMarker;
    private String mProviderName;
    private LatLng mProviderLatLng;
    private LatLng mAddressLatLng;
    private boolean mFirstZoom = true;
    private Booking.LocationStatus mLocationStatus;
    private Handler mHandler;
    private Runnable mRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            requestLocationStatus();
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
            mLocationStatus = mBooking.getActiveBookingLocationStatus();
            toggleMapServicesAvailability(savedInstanceState);
            toggleProviderSection();

            mTextBookingTitle.setText(BookingUtil.getTitle(mBooking));
            mTextBookingSubtitle.setText(BookingUtil.getSubtitle(mBooking, getActivity()));
            mMapDivider.setVisibility(View.VISIBLE);
        }
        else
        {
            mMapView.setVisibility(View.GONE);
            mMapDivider.setVisibility(View.GONE);
        }

        if (mBooking.isMilestonesEnabled())
        {
            mReportIssueContainer.setVisibility(View.VISIBLE);
        }
        else
        {
            mReportIssueContainer.setVisibility(View.GONE);
        }

        return view;
    }

    /**
     * If there is provider information, then fill it out, otherwise, don't show that section
     */
    private void toggleProviderSection()
    {
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
    }

    /**
     * Toggles the correct display for the map section. If device supports map, then show it,
     * otherwise show a way to install support for maps
     *
     * @param savedInstanceState
     */
    private void toggleMapServicesAvailability(Bundle savedInstanceState)
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
    }


    /**
     * This is tricky. We have to handle the display of both the pro location and the booking location
     *
     * If there are no booking location, show error view
     * If prolocation should be shown, but there isn't any, then show error view
     * if prolocation should not be shown, then don't attempt to show pro location
     *
     * if there are booking location and prolocation should be shown, and there is pro location,
     * then plot all parties on the map.
     *
     * Whenever we plot provider location, we also want to setup periodic update of the pro's location
     *
     */
    private void updateMap()
    {
        if (mLocationStatus != null)
        {
            mMissingLocationView.setVisibility(View.GONE);

            //if there is bad location, show the error message and do nothing else
            if (isBadLocation(mLocationStatus.getBookingLocation()))
            {
                mMissingLocationView.missingBookingLocation();
                mMissingLocationView.setVisibility(View.VISIBLE);
                return;
            }
            else
            {
                mAddressLatLng = new LatLng(
                        Double.valueOf(mLocationStatus.getBookingLocation().getLatitude()),
                        Double.valueOf(mLocationStatus.getBookingLocation().getLongitude())
                );

                mGoogleMap.addMarker(new MarkerOptions()
                        .position(mAddressLatLng)
                        .anchor(ANCHOR_MID_POINT, ANCHOR_MID_POINT)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.img_house_pin)));
            }

            //we first check whether the provider location should even be shown
            if (mLocationStatus.isProviderLocationVisible())
            {
                //if there is no provider location, then show error and exit
                if (isBadLocation(mLocationStatus.getProviderLocation()))
                {
                    mMissingLocationView.missingProviderLocation();
                    mMissingLocationView.setVisibility(View.VISIBLE);
                }
                else
                {
                    mProviderLatLng = new LatLng(
                            Double.valueOf(mLocationStatus.getProviderLocation().getLatitude()),
                            Double.valueOf(mLocationStatus.getProviderLocation().getLongitude())
                    );

                    mProviderLocationMarker = mGoogleMap.addMarker(
                            new MarkerOptions()
                                    .position(mProviderLatLng)
                                    .title(mProviderName)
                                    .anchor(ANCHOR_MID_POINT, ANCHOR_MID_POINT)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.img_pro_active_pin))
                    );

                    if (mHandler == null)
                    {
                        //start the handler to ping for location change every time
                        mHandler = new Handler();
                        periodicUpdate();
                    }
                    setTimeStamp(mBooking.getActiveBookingLocationStatus().getProviderLocation().getTimeStamp());
                }
            }

            //need to call this at the end because there could be a situation where there is
            //booking location but not provider location, so we want to zoom there at least.
            adjustMapPositioning();
        }
    }

    private void setTimeStamp(Date timeStamp)
    {
        if (!isAttached())
        {
            return;
        }
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

    private boolean isAttached()
    {
        return isAdded() && getActivity() != null;
    }

    private void periodicUpdate()
    {
        if (mHandler != null)
        {
            mHandler.postDelayed(mRunnable, GEO_STATUS_PING_INTERVAL_MS);
        }
    }

    private void requestLocationStatus()
    {
        dataManager.getLocationStatus(mBooking.getId(), new DataManager.Callback<Booking.LocationStatus>()
        {
            @Override
            public void onSuccess(final Booking.LocationStatus response)
            {

                if (response != null && !isBadLocation(response.getProviderLocation()) && isAttached())
                {
                    double lat = Double.parseDouble(response.getProviderLocation().getLatitude());
                    double lng = Double.parseDouble(response.getProviderLocation().getLongitude());
                    mProviderLatLng = new LatLng(lat, lng);
                    adjustMapPositioning();
                    setTimeStamp(response.getProviderLocation().getTimeStamp());
                }
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
        if (mGoogleMap == null || !isAttached())
        {
            return;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if (mAddressLatLng != null)
        {
            builder.include(mAddressLatLng);
        }

        if (mProviderLatLng != null)
        {
            builder.include(mProviderLatLng);
            mProviderLocationMarker.setPosition(mProviderLatLng);
        }

        if (mFirstZoom)
        {
            mFirstZoom = false;
            if (mAddressLatLng != null && mProviderLatLng != null)
            {
                //we bound the map view by the two locations if we actually have 2 locations
                LatLngBounds bounds = builder.build();
                //gives it some padding, so that the markers are not right at the edge of the screen.
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDimensionPixelSize(R.dimen.active_booking_map_height);
                int padding = getResources().getDimensionPixelOffset(R.dimen.default_padding_4x);

                //first zoom to enclose all the markers.
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                mGoogleMap.moveCamera(cu);
            }
            else if (mAddressLatLng != null)
            {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mAddressLatLng, MAP_CLOSEUP_ZOOM_LEVEL));
            }
            else if (mProviderLatLng != null)
            {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mProviderLatLng, MAP_CLOSEUP_ZOOM_LEVEL));
            }
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
        bus.post(new LogEvent.AddLogEvent(new ActiveBookingLog.BookingProContactedLog(
                mBooking.getId(), ActiveBookingLog.BookingProContactedLog.PHONE)));
        BookingUtil.callPhoneNumber(mBooking.getProvider().getPhone(), this.getActivity());
    }

    @OnClick(R.id.text_text)
    public void textClicked()
    {
        bus.post(new LogEvent.AddLogEvent(new ActiveBookingLog.BookingProContactedLog(
                mBooking.getId(), ActiveBookingLog.BookingProContactedLog.SMS)));
        BookingUtil.textPhoneNumber(mBooking.getProvider().getPhone(), this.getActivity());
    }

    @OnClick(R.id.button_report_issue)
    public void reportIssueClicked()
    {
        bus.post(new LogEvent.AddLogEvent(new ActiveBookingLog.ReportIssueTappedLog(mBooking.getId())));
        Intent intent = new Intent(getContext(), ReportIssueActivity.class);
        intent.putExtra(BundleKeys.BOOKING, mBooking);
        startActivity(intent);
    }


    private void gotoBookingDetails()
    {
        bus.post(new LogEvent.AddLogEvent(new ActiveBookingLog.BookingDetailsTappedLog(mBooking.getId())));
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

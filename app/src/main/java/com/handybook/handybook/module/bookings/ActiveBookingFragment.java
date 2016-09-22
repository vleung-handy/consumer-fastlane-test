package com.handybook.handybook.module.bookings;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.JobStatus;
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
 * This fragment contains only the information regarding one active booking. It holds the map, plots
 * the location of both the provider and the destination. It pings the server for updated provider
 * location information periodically. Also shows the provider's name, and allows calling/texting to
 * the provider.
 */
public class ActiveBookingFragment extends InjectedFragment implements OnMapReadyCallback
{
    private static final String KEY_BOOKING = "booking";
    private static final int GEO_STATUS_PING_INTERVAL_MS = 10000;  //ping for geo status every 10 seconds
    private static final int PRO_LOCATION_ACTIVE_THRESHOLD_MS = 300000;  //5 minutes. Past this means stale
    private static final float MAP_CLOSEUP_ZOOM_LEVEL = 16;
    private static final float ANCHOR_MID_POINT = 0.5f;

    @Bind(R.id.active_booking_map_divider)
    View mMapDivider;

    @Bind(R.id.active_booking_provider_name)
    TextView mTextProviderName;

    @Bind(R.id.text_booking_title)
    TextView mTextBookingTitle;

    @Bind(R.id.text_booking_subtitle)
    TextView mTextBookingSubtitle;

    @Bind(R.id.active_booking_profile_container)
    View mProfileContainer;

    @Bind(R.id.active_booking_profile_container_divider)
    View mProfileContainerDivider;

    @Bind(R.id.active_booking_call)
    TextView mTextCall;

    @Bind(R.id.active_booking_text)
    TextView mTextText;

    @Bind(R.id.booking_item_container)
    View mBookingItemContainer;

    @Bind(R.id.active_booking_report_an_issue_container)
    View mReportIssueContainer;

    @Bind(R.id.active_booking_map_view)
    MapView mMapView;

    @Bind(R.id.active_booking_transparent_image)
    ImageView mTransparentImage;

    @Bind(R.id.active_booking_pro_location_time)
    TextView mTextLocationTime;

    @Bind(R.id.active_booking_milestone_status)
    TextView mTextMilestoneStatus;

    @Bind(R.id.active_booking_map_place_holder)
    MapPlaceholderView mMapPlaceHolderView;

    @Bind(R.id.active_booking_missing_location_view)
    MissingLocationView mMissingLocationView;

    private GoogleMap mGoogleMap;
    private Booking mBooking;
    private Marker mBookingLocationMarker;
    private Marker mProviderLocationMarker;
    private String mProviderName;
    private boolean mFirstZoom = true;
    private Booking.LocationStatus mLocationStatus;
    private BitmapDescriptor mHouseIcon;
    private BitmapDescriptor mCompletedIcon;
    private BitmapDescriptor mCleanerArrivedIcon;
    private BitmapDescriptor mOtherServiceArrivedIcon;
    private BitmapDescriptor mActiveProIcon;
    private BitmapDescriptor mInactiveProIcon;

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
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    )
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
        if (mBooking.getProvider() != null && !TextUtils.isEmpty(mBooking.getProvider()
                                                                         .getFullName()
                                                                         .trim()))
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
     * Initialize the different map icons we are going to use for swapping when location status
     * changes
     */
    private void initializeIcons()
    {
        mHouseIcon = BitmapDescriptorFactory.fromResource(R.drawable.img_house_pin);
        mCompletedIcon = BitmapDescriptorFactory.fromResource(R.drawable.img_completed_pin);
        mActiveProIcon = BitmapDescriptorFactory.fromResource(R.drawable.img_pro_active_pin);
        mInactiveProIcon = BitmapDescriptorFactory.fromResource(R.drawable.img_pro_inactive_pin);
        mCleanerArrivedIcon = BitmapDescriptorFactory.fromResource(R.drawable.img_cleaner_pin);
        mOtherServiceArrivedIcon = BitmapDescriptorFactory.fromResource(R.drawable.img_handyman_pin);
    }

    /**
     * This is tricky. We have to handle the display of both the pro location and the booking
     * location
     * <p/>
     * If there are no booking location, show error view If prolocation should be shown, but there
     * isn't any, then show error view if prolocation should not be shown, then don't attempt to
     * show pro location
     * <p/>
     * if there are booking location and prolocation should be shown, and there is pro location,
     * then plot all parties on the map.
     * <p/>
     * Whenever we plot provider location, we also want to setup periodic update of the pro's
     * location
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
                if (mHouseIcon == null)
                {
                    initializeIcons();
                }
                LatLng addressLatLng = new LatLng(
                        Double.valueOf(mLocationStatus.getBookingLocation().getLatitude()),
                        Double.valueOf(mLocationStatus.getBookingLocation().getLongitude())
                );

                mBookingLocationMarker = mGoogleMap.addMarker(new MarkerOptions()
                                                                      .position(addressLatLng)
                                                                      .anchor(
                                                                              ANCHOR_MID_POINT,
                                                                              ANCHOR_MID_POINT
                                                                      )
                                                                      .icon(mHouseIcon));
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
                    LatLng providerLatLng = new LatLng(
                            Double.valueOf(mLocationStatus.getProviderLocation().getLatitude()),
                            Double.valueOf(mLocationStatus.getProviderLocation().getLongitude())
                    );

                    if (mActiveProIcon == null)
                    {
                        initializeIcons();
                    }

                    mProviderLocationMarker = mGoogleMap.addMarker(
                            new MarkerOptions()
                                    .position(providerLatLng)
                                    .title(mProviderName)
                                    .anchor(ANCHOR_MID_POINT, ANCHOR_MID_POINT)
                                    .icon(mActiveProIcon)
                    );

                    startPeriodicUpdate();
                    updateLocationStatus(mBooking.getActiveBookingLocationStatus());
                }
            }

            //need to call this at the end because there could be a situation where there is
            //booking location but not provider location, so we want to zoom there at least.
            adjustMapPositioning();
        }
    }

    private void startPeriodicUpdate()
    {
        //only do this if the handler is null. If it's not null, then there is probably a periodic update already running
        if (mHandler == null)
        {
            //start the handler to ping for location change every time
            mHandler = new Handler();
            periodicUpdate();
        }

    }

    /**
     * Updates the UI with the "milestone" information. It will show the booking status {starting
     * soon, pro's on their way, completed, etc.}
     *
     * @param locationStatus
     */
    protected void updateLocationStatus(Booking.LocationStatus locationStatus)
    {
        if (!isAttached())
        {
            return;
        }

        //if there is a timestamp, show it.
        String title = locationStatus.getMilestone() != null ? locationStatus.getMilestone()
                                                                             .getTitle() : null;

        if (locationStatus.getProviderLocation().getTimeStamp() != null)
        {
            String time = DateTimeUtils.getTime(locationStatus.getProviderLocation()
                                                              .getTimeStamp());
            mTextLocationTime.setText(getResources().getString(
                    R.string.pro_location_time_formatted,
                    time
            ));
            mTextLocationTime.setVisibility(View.VISIBLE);
        }
        else
        {
            mTextLocationTime.setVisibility(View.GONE);
        }

        //if there is a booking status, show it.

        if (!TextUtils.isEmpty(title))
        {
            mTextMilestoneStatus.setText(title);
            mTextMilestoneStatus.setVisibility(View.VISIBLE);

            Drawable drawable = ContextCompat.getDrawable(getActivity(),
                                                          locationStatus.getMilestone()
                                                                        .getStatusDrawableId(true)
            );
            int dimension = getResources().getDimensionPixelSize(R.dimen.status_dot_dimension);
            drawable.setBounds(0, 0, dimension, dimension);
            mTextMilestoneStatus.setCompoundDrawables(drawable, null, null, null);
        }
        else
        {
            mTextMilestoneStatus.setVisibility(View.GONE);
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
        dataManager.getLocationStatus(
                mBooking.getId(),
                new DataManager.Callback<Booking.LocationStatus>()
                {
                    @Override
                    public void onSuccess(final Booking.LocationStatus response)
                    {
                        mLocationStatus = response;
                        if (response != null && !isBadLocation(response.getProviderLocation()) && isAttached())
                        {
                            adjustMapPositioning();
                            updateLocationStatus(response);
                        }
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        //don't worry about it.
                    }
                }
        );
    }

    /**
     * This will update the booking and/or provider location icons based on the milestone states {on
     * my way, arrived, completed, etc.}
     */
    private void updateLocationIcons()
    {
        if (mLocationStatus.getMilestone() != null)
        {
            String state = mLocationStatus.getMilestone().getState();
            switch (state)
            {
                //these are states that doesn't necessarily impact the state of the markers, so we exit.
                case JobStatus.Milestone.UNAVAILABLE:
                case JobStatus.Milestone.BEHIND_SCHEDULE:
                case JobStatus.Milestone.PRO_LATE:
                case JobStatus.Milestone.PRO_NO_SHOW:
                case JobStatus.Milestone.PRO_ARRIVED_LATE:
                    mBookingLocationMarker.setIcon(mHouseIcon);
                    showAppropriateProviderMarker();
                    break;
                case JobStatus.Milestone.STARTS_SOON:
                    mProviderLocationMarker.setVisible(false);
                    mBookingLocationMarker.setIcon(mHouseIcon);
                    break;
                case JobStatus.Milestone.ARRIVED:
                    mProviderLocationMarker.setVisible(false);
                    if (mBooking.getServiceMachineName().contains(Booking.SERVICE_CLEANING))
                    {
                        // this is a cleaning
                        mBookingLocationMarker.setIcon(mCleanerArrivedIcon);
                    }
                    else
                    {
                        mBookingLocationMarker.setIcon(mOtherServiceArrivedIcon);
                    }
                    break;
                case JobStatus.Milestone.COMPLETED:
                    mProviderLocationMarker.setVisible(false);
                    mBookingLocationMarker.setIcon(mCompletedIcon);
                    break;
                case JobStatus.Milestone.ON_MY_WAY:
                    mBookingLocationMarker.setIcon(mHouseIcon);
                    showAppropriateProviderMarker();
                    break;
            }
        }
    }

    /**
     * This switches between the active and inactive provider markers, depending on the ellapsed
     * time.
     */
    private void showAppropriateProviderMarker()
    {

        if (!isBadLocation(mLocationStatus.getProviderLocation()))
        {
            mProviderLocationMarker.setVisible(true);
            Date timeStamp = mLocationStatus.getProviderLocation().getTimeStamp();
            if (timeStamp != null &&
                    System.currentTimeMillis() - timeStamp.getTime() < PRO_LOCATION_ACTIVE_THRESHOLD_MS)
            {
                //not yet stale
                mProviderLocationMarker.setIcon(mActiveProIcon);
            }
            else
            {
                mProviderLocationMarker.setIcon(mInactiveProIcon);
            }
        }
        else
        {
            mProviderLocationMarker.setVisible(false);
        }
    }

    /**
     * This is where all the positioning of the marker is done.
     */
    private void adjustMapPositioning()
    {
        if (mGoogleMap == null || !isAttached())
        {
            return;
        }

        updateLocationIcons();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        LatLng addressLatLng = null;
        if (!isBadLocation(mLocationStatus.getBookingLocation()))
        {
            addressLatLng = new LatLng(
                    Double.valueOf(mLocationStatus.getBookingLocation().getLatitude()),
                    Double.valueOf(mLocationStatus.getBookingLocation().getLongitude())
            );

            builder.include(addressLatLng);
        }

        LatLng providerLatLng = null;
        if (!isBadLocation(mLocationStatus.getProviderLocation()))
        {
            providerLatLng = new LatLng(
                    Double.valueOf(mLocationStatus.getProviderLocation().getLatitude()),
                    Double.valueOf(mLocationStatus.getProviderLocation().getLongitude())
            );

            builder.include(providerLatLng);
            mProviderLocationMarker.setPosition(providerLatLng);
        }

        if (mFirstZoom)
        {
            mFirstZoom = false;
            if (addressLatLng != null && providerLatLng != null)
            {
                //we bound the map view by the two locations if we actually have 2 locations
                LatLngBounds bounds = builder.build();
                //gives it some padding, so that the markers are not right at the edge of the screen.
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDimensionPixelSize(R.dimen.active_booking_map_height);
                int padding = getResources().getDimensionPixelOffset(R.dimen.default_padding_4x);

                //first zoom to enclose all the markers.
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(
                        bounds,
                        width,
                        height,
                        padding
                );
                mGoogleMap.moveCamera(cu);
            }
            else if (addressLatLng != null)
            {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        addressLatLng,
                        MAP_CLOSEUP_ZOOM_LEVEL
                ));
            }
            else if (providerLatLng != null)
            {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        providerLatLng,
                        MAP_CLOSEUP_ZOOM_LEVEL
                ));
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
            initializeMap();
        }
    }

    @Override
    public void onAttach(final Context context)
    {
        super.onAttach(context);
        initializeMap();
    }

    private void initializeMap()
    {
        if (mGoogleMap != null && getActivity() != null && isMapBeingShown()) {
            // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
            MapsInitializer.initialize(this.getActivity());
            updateMap();
        }
    }

    @OnClick(R.id.active_booking_profile_container)
    public void profileClicked()
    {
        gotoBookingDetails();
    }

    @OnClick(R.id.booking_item_container)
    public void bookingClicked()
    {
        gotoBookingDetails();
    }

    @OnClick(R.id.active_booking_call)
    public void callClicked()
    {
        bus.post(new LogEvent.AddLogEvent(new ActiveBookingLog.BookingProContactedLog(
                mBooking.getId(), ActiveBookingLog.BookingProContactedLog.PHONE)));
        BookingUtil.callPhoneNumber(mBooking.getProvider().getPhone(), this.getActivity());
    }

    @OnClick(R.id.active_booking_text)
    public void textClicked()
    {
        bus.post(new LogEvent.AddLogEvent(new ActiveBookingLog.BookingProContactedLog(
                mBooking.getId(), ActiveBookingLog.BookingProContactedLog.SMS)));
        BookingUtil.textPhoneNumber(mBooking.getProvider().getPhone(), this.getActivity());
    }

    @OnClick(R.id.active_booking_report_issue)
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

            if (mGoogleMap != null)
            {
                //resume the periodic update of the location status
                startPeriodicUpdate();
            }

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

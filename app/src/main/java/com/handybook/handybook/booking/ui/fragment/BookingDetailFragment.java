package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.JobStatus;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.ui.activity.BookingCancelOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingDateActivity;
import com.handybook.handybook.booking.ui.activity.ReportIssueActivity;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragment;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentAddress;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentBookingActions;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentEntryInformation;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentExtras;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentLaundry;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentPayment;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentPreferences;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentProInformation;
import com.handybook.handybook.booking.ui.view.BookingDetailView;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.callback.FragmentSafeCallback;
import com.handybook.handybook.helpcenter.ui.activity.HelpActivity;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.module.chat.AuthenticationProvider;
import com.handybook.handybook.module.chat.LayerAuthenticationProvider;
import com.handybook.handybook.module.chat.LayerHelper;
import com.handybook.handybook.module.chat.PushNotificationReceiver;
import com.handybook.handybook.module.chat.SimpleRecyclerCallback;
import com.handybook.handybook.module.chat.builtin.MessagesListActivity;
import com.handybook.handybook.module.configuration.event.ConfigurationEvent;
import com.handybook.handybook.module.configuration.model.Configuration;
import com.handybook.handybook.module.referral.event.ReferralsEvent;
import com.handybook.handybook.module.referral.manager.ReferralsManager;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.layer.sdk.query.Queryable;
import com.layer.sdk.query.RecyclerViewController;
import com.layer.sdk.query.SortDescriptor;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingDetailFragment extends InjectedFragment implements PopupMenu.OnMenuItemClickListener
{
    private static final String TAG = BookingDetailFragment.class.getName();

    private static final String STATE_UPDATED_BOOKING = "STATE_UPDATED_BOOKING";
    private static final String STATE_SERVICES = "STATE_SERVICES";

    private Booking mBooking;
    private Configuration mConfiguration;
    private String mBookingId;
    private boolean mIsFromBookingFlow;
    private boolean mBookingUpdated;

    private RescheduleType mRescheduleType;

    @Bind(R.id.booking_detail_view)
    BookingDetailView mBookingDetailView;
    @Bind(R.id.nav_help)
    TextView mHelp;

    @Bind(R.id.image_chat)
    ImageView mImageChat;

    @Inject
    @Named("layerAppId")
    String mLayerAppId;

    @Inject
    LayerClient mLayerClient;

    @Inject
    LayerHelper mLayerHelper;

    private ArrayList<Service> mServices;
    private RecyclerViewController<Conversation> mQueryController;

    public static BookingDetailFragment newInstance(
            final Booking booking,
            final boolean isFromBookingFlow
    )
    {
        final BookingDetailFragment fragment = new BookingDetailFragment();
        final Bundle args = new Bundle();
        args.putParcelable(BundleKeys.BOOKING, booking);
        args.putBoolean(BundleKeys.IS_FROM_BOOKING_FLOW, isFromBookingFlow);
        fragment.setArguments(args);
        return fragment;
    }

    public static BookingDetailFragment newInstance(
            final String bookingId,
            final boolean isFromBookingFlow
    )
    {
        final BookingDetailFragment fragment = new BookingDetailFragment();
        final Bundle args = new Bundle();
        args.putString(BundleKeys.BOOKING_ID, bookingId);
        args.putBoolean(BundleKeys.IS_FROM_BOOKING_FLOW, isFromBookingFlow);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBooking = getArguments().getParcelable(BundleKeys.BOOKING);
        mBookingId = getArguments().getString(BundleKeys.BOOKING_ID);
        mIsFromBookingFlow = getArguments().getBoolean(BundleKeys.IS_FROM_BOOKING_FLOW, false);

        if (savedInstanceState != null)
        {
            if (savedInstanceState.getBoolean(STATE_UPDATED_BOOKING))
            {
                setUpdatedBookingResult();
            }

            mServices = (ArrayList<Service>) savedInstanceState.getSerializable(STATE_SERVICES);
        }
    }

    @OnClick(R.id.image_chat)
    public void chatClicked() {
        Intent intent = new Intent(getActivity(), MessagesListActivity.class);
        intent.putExtra(
                PushNotificationReceiver.LAYER_CONVERSATION_KEY,
                Uri.parse(mBooking.getConversationId())
        );
        startActivity(intent);
    }

    /**
     * We only come here if the layer auth is successful
     */
    private void syncConversation()
    {
        Log.d(TAG, "syncConversation() called");
        Conversation conversation = mLayerClient.getConversation(Uri.parse(mBooking.getConversationId()));

        if (conversation != null)
        {
            Log.d(TAG, "syncConversation: conversation is not null");
            Conversation.HistoricSyncStatus status = conversation.getHistoricSyncStatus();

            if (status == Conversation.HistoricSyncStatus.MORE_AVAILABLE)
            {
                Log.d(TAG, "syncConversation: There is more messages available for synching");
                conversation.syncMoreHistoricMessages(20);
            }

            if (getActivity() != null)
            {
                Log.d(TAG, "syncConversation: making chat icon visible on UI thread");
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mImageChat.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
        else
        {
            //the conversations haven't been initialized yet. Initialize them.
            Log.d(TAG, "syncConversation: conversation is NULL");
            initConversations();
        }

    }


    private void initConversations()
    {
        Log.d(TAG, "initConversations: ");
        if (mQueryController != null)
        {
            //only do this once, so we don't get ourselves into an infinite loop type scenario
            Log.d(
                    TAG,
                    "initConversations: query controller has already been created, not doing it again"
            );
            return;
        }
        Query<Conversation> query = Query.builder(Conversation.class)
                /* Only show conversations we're still a member of */
                                         .predicate(new Predicate(
                                                 Conversation.Property.PARTICIPANT_COUNT,
                                                 Predicate.Operator.GREATER_THAN,
                                                 1
                                         ))

                /* Sort by the last Message's receivedAt time */
                                         .sortDescriptor(new SortDescriptor(
                                                 Conversation.Property.LAST_MESSAGE_RECEIVED_AT,
                                                 SortDescriptor.Order.DESCENDING
                                         ))
                                         .build();

        mQueryController = mLayerClient.newRecyclerViewController(
                query,
                null,
                new SimpleRecyclerCallback()
                {
                    @Override
                    public void onQueryItemInserted(
                            final RecyclerViewController recyclerViewController,
                            final int i
                    )
                    {
                        Log.d(
                                TAG,
                                "onQueryItemInserted() called with: recyclerViewController = [" + recyclerViewController + "], i = [" + i + "]"
                        );
                        syncConversation();
                    }
                }
        );

        Log.d(TAG, "initConversations: executing query, look out for events coming back");
        mQueryController.execute();
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = inflater.inflate(R.layout.fragment_booking_detail, container, false);
        ButterKnife.bind(this, view);
        if (mBooking != null)
        {
            setupForBooking(mBooking);
        }
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (mBooking == null)
        {
            showUiBlockers();
            bus.post(new BookingEvent.RequestBookingDetails(mBookingId));
        }

        if (mServices == null)
        {
            bus.post(new BookingEvent.RequestCachedServices());
        }

        bus.post(new ConfigurationEvent.RequestConfiguration());

        if (mIsFromBookingFlow)
        {
            bus.post(new ReferralsEvent.RequestPrepareReferrals(
                    true,
                    ReferralsManager.Source.POST_BOOKING
            ));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.panic_menu, menu);
    }

    @Override
    public final void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data
    )
    {
        super.onActivityResult(requestCode, resultCode, data);

        //TODO: Should be checking and setting results codes not just request code in case we have functionality that returns to this page on failure

        if (resultCode == ActivityResult.RESCHEDULE_NEW_DATE)
        {
            if (data.getLongExtra(BundleKeys.RESCHEDULE_NEW_DATE, 0) != 0)
            {
                Date newDate = new Date(data.getLongExtra(BundleKeys.RESCHEDULE_NEW_DATE, 0));
                //TODO: We are manually updating the booking, which is something we should strive to avoid as the client is directly changing the model. API v4 should return the updated booking model
                mBookingDetailView.updateDateTimeInfoText(mBooking, newDate);
                setUpdatedBookingResult();
            }
        }
        else if (resultCode == ActivityResult.BOOKING_CANCELED)
        {
            setCanceledBookingResult();
            getActivity().finish();
        }
        else if (resultCode == ActivityResult.BOOKING_UPDATED)
        {
            //various fields could have been updated like note to pro or entry information, request booking details for this booking and redisplay them
            postBlockingEvent(new BookingEvent.RequestBookingDetails(mBooking.getId()));
            //setting the updated result with the new booking when we receive the new booking data
        }
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_UPDATED_BOOKING, mBookingUpdated);
        outState.putSerializable(STATE_SERVICES, mServices);
    }

    @Override
    protected void disableInputs()
    {
        super.disableInputs();
        mBookingDetailView.backButton.setClickable(false);
        setSectionFragmentInputsEnabled(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        mBookingDetailView.backButton.setClickable(true);
        setSectionFragmentInputsEnabled(true);
    }


    @OnClick(R.id.nav_help)
    void onHelpClicked(final View view)
    {
        PopupMenu popup = new PopupMenu(getActivity(), view);
        popup.getMenuInflater().inflate(R.menu.panic_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }


    private void setSectionFragmentInputsEnabled(boolean enabled)
    {
        bus.post(new BookingEvent.SetBookingDetailSectionFragmentActionControlsEnabled(enabled));
    }

    private void setupForBooking(Booking booking)
    {
        Log.d(TAG, "setupForBooking: ");
        mHelp.setVisibility(shouldShowPanicButtons(mBooking) ? View.VISIBLE : View.GONE);
        mBookingDetailView.updateDisplay(booking, mServices);
        mBookingDetailView.updateReportIssueButton(mBooking, new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                dataManager.getBookingMilestones(
                        mBooking.getId(),
                        new FragmentSafeCallback<JobStatus>(BookingDetailFragment.this)
                        {
                            @Override
                            public void onCallbackSuccess(JobStatus status)
                            {
                                Intent intent = new Intent(
                                        getContext(),
                                        ReportIssueActivity.class
                                );
                                intent.putExtra(
                                        BundleKeys.BOOKING,
                                        mBooking
                                );
                                intent.putExtra(
                                        BundleKeys.PRO_JOB_STATUS,
                                        status
                                );
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK |
                                                        Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }

                            @Override
                            public void onCallbackError(final DataManager.DataManagerError error)
                            {
                                if (!Strings.isNullOrEmpty(error.getMessage()))
                                {
                                    showToast(error.getMessage());
                                }
                                else
                                {
                                    showToast(R.string.an_error_has_occurred);
                                }
                            }
                        }
                );
            }
        });
        setupClickListeners();
        addSectionFragments();
        initLayer();
    }

    private void initLayer()
    {
        if (mConfiguration == null
                || !mConfiguration.isInAppChatEnabled()
                || mBooking == null
                || TextUtils.isEmpty(mBooking.getConversationId())
                )
        {
            //don't init layer if the configuration isn't back, or the booking isn't ready
            return;
        }

        if ((mLayerClient != null) && mLayerClient.isAuthenticated())
        {
            Log.d(TAG, "initLayer: Already logged in");
            waitForContent();
            syncConversation();
        }
        else
        {
            Log.d(TAG, "initLayer: Not logged in");
            final String name = "Jia";
            mLayerHelper.authenticate(
                    new LayerAuthenticationProvider.Credentials(mLayerAppId, name),
                    new AuthenticationProvider.Callback()
                    {
                        @Override
                        public void onSuccess(AuthenticationProvider provider, String userId)
                        {
                            Log.d(TAG, "AUTH onSuccess: ");
                            syncConversation();
                        }

                        @Override
                        public void onError(AuthenticationProvider provider, final String error)
                        {
                            Log.e(TAG, "Failed to authenticate as `" + name + "`: " + error);
                            //TODO: JIA: this should never happen, so if it does, then log something
                        }
                    }
            );

        }
    }

    private void waitForContent()
    {
        Log.d(TAG, "waitForContent: ");
        mLayerClient.waitForContent(
                Uri.parse(mBooking.getConversationId()),
                new LayerClient.ContentAvailableCallback()
                {
                    @Override
                    public void onContentAvailable(
                            final LayerClient layerClient, @NonNull final Queryable queryable
                    )
                    {
                        Log.d(
                                TAG,
                                "onContentAvailable() called with: layerClient = [" + layerClient + "], queryable = [" + queryable + "]"
                        );
                    }

                    @Override
                    public void onContentFailed(
                            final LayerClient layerClient,
                            final Uri uri,
                            final Exception e
                    )
                    {
                        Log.d(
                                TAG,
                                "onContentFailed() called with: layerClient = [" + layerClient + "], uri = [" + uri + "], e = [" + e + "]"
                        );
                    }
                }
        );
    }

    private void setupClickListeners()
    {
        mBookingDetailView.backButton.setOnClickListener(backButtonClicked);
    }

    //Section fragments to display, In display order
    protected List<BookingDetailSectionFragment> constructSectionFragments()
    {
        return Lists.newArrayList(
                new BookingDetailSectionFragmentProInformation(),
                new BookingDetailSectionFragmentLaundry(),
                new BookingDetailSectionFragmentEntryInformation(),
                new BookingDetailSectionFragmentPreferences(),
                new BookingDetailSectionFragmentExtras(),
                new BookingDetailSectionFragmentAddress(),
                new BookingDetailSectionFragmentPayment(),
                new BookingDetailSectionFragmentBookingActions()
        );
    }

    private void addSectionFragments()
    {
        clearSectionFragments();

        List<BookingDetailSectionFragment> sectionFragments = constructSectionFragments();

        //These are fragments nested inside this fragment, must use getChildFragmentManager instead of getFragmentManager
        for (BookingDetailSectionFragment sectionFragment : sectionFragments)
        {
            //Normally we would bundle all of these adds into one transaction but there is a bug
            //  with the fragment manager which displays them in reverse order if fragments were just cleared
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            Bundle args = new Bundle();
            args.putParcelable(BundleKeys.BOOKING, mBooking);
            sectionFragment.setArguments(args);
            transaction.add(R.id.section_fragment_container, sectionFragment);
            transaction.commit();
        }

    }

    private void clearSectionFragments()
    {
        //Remove all of the child fragments for this fragment
        List<Fragment> childFragments = getChildFragmentManager().getFragments();
        if (childFragments != null && childFragments.size() > 0)
        {
            FragmentTransaction removalTransaction = getChildFragmentManager().beginTransaction();
            for (Fragment frag : childFragments)
            {
                if (!(frag == null || frag.isDetached() || frag.isRemoving()))
                {
                    removalTransaction.remove(frag);
                }
            }
            removalTransaction.commit();
        }
    }

    //The on screen back button works as the softkey back button
    private View.OnClickListener backButtonClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            getActivity().onBackPressed();
        }
    };

    /**
     * Receives the services tree, and now we can fill the booking icon properly
     *
     * @param event
     */
    @Subscribe
    public void onReceiveCachedServicesSuccess(BookingEvent.ReceiveCachedServicesSuccess event)
    {
        mServices = (ArrayList<Service>) event.getServices();
        mBookingDetailView.updateServiceIcon(mBooking, mServices);
    }

    @Subscribe
    public void onReceivePreRescheduleInfoSuccess(BookingEvent.ReceivePreRescheduleInfoSuccess event)
    {
        removeUiBlockers();

        final Intent intent = new Intent(getActivity(), BookingDateActivity.class);
        intent.putExtra(BundleKeys.RESCHEDULE_BOOKING, mBooking);
        intent.putExtra(BundleKeys.RESCHEDULE_NOTICE, event.notice);
        intent.putExtra(BundleKeys.RESCHEDULE_TYPE, mRescheduleType);

        startActivityForResult(intent, ActivityResult.RESCHEDULE_NEW_DATE);
    }

    @Subscribe
    public void onReceivePreRescheduleInfoError(BookingEvent.ReceivePreRescheduleInfoError event)
    {
        if (mRescheduleType != null && mRescheduleType == RescheduleType.FROM_CANCELATION)
        {
            //if this reschedule event was originated from a cancelation, and it fails, then we
            //should just go forward with the cancelation
            bus.post(new BookingEvent.RequestPreCancelationInfo(mBooking.getId()));
        }
        else
        {
            removeUiBlockers();
            dataManagerErrorHandler.handleError(getActivity(), event.error);
        }
    }

    @Subscribe
    public void onReceiveConfigurationSuccess(
            final ConfigurationEvent.ReceiveConfigurationSuccess event
    )
    {
        if (event != null)
        {
            mConfiguration = event.getConfiguration();
            initLayer();
        }
    }

    @Subscribe
    public void onReceivePreCancellationInfoSuccess(BookingEvent.ReceivePreCancelationInfoSuccess event)
    {
        removeUiBlockers();

        Pair<String, List<String>> result = event.result;

        final Intent intent = new Intent(getActivity(), BookingCancelOptionsActivity.class);
        intent.putExtra(BundleKeys.OPTIONS, new ArrayList<>(result.second));
        intent.putExtra(BundleKeys.NOTICE, result.first);
        intent.putExtra(BundleKeys.BOOKING, mBooking);
        startActivityForResult(intent, ActivityResult.BOOKING_CANCELED);
    }

    @Subscribe
    public void onReceivePreCancellationInfoError(BookingEvent.ReceivePreCancelationInfoError event)
    {
        removeUiBlockers();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    @Subscribe
    public void onReceiveBookingDetailsSuccess(BookingEvent.ReceiveBookingDetailsSuccess event)
    {
        removeUiBlockers();

        mBooking = event.booking;
        getArguments().putParcelable(BundleKeys.BOOKING, event.booking);
        setUpdatedBookingResult();
        setupForBooking(event.booking);
    }

    @Subscribe
    public void onReceiveBookingDetailsError(BookingEvent.ReceiveBookingDetailsError event)
    {
        removeUiBlockers();

        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    private void setUpdatedBookingResult()
    {
        mBookingUpdated = true;
        final Intent intent = new Intent();
        intent.putExtra(BundleKeys.UPDATED_BOOKING, mBooking);
        getActivity().setResult(ActivityResult.BOOKING_UPDATED, intent);
    }

    private void setCanceledBookingResult()
    {
        final Intent intent = new Intent();
        intent.putExtra(BundleKeys.CANCELLED_BOOKING, mBooking);
        getActivity().setResult(ActivityResult.BOOKING_CANCELED, intent);
    }

    private boolean shouldShowPanicButtons(final Booking booking)
    {
        if (booking == null) {return false;}
        final Date now = new Date();

        final GregorianCalendar periodStart = new GregorianCalendar();
        periodStart.setTime(booking.getStartDate());
        periodStart.add(Calendar.HOUR, -1); // An hour before

        final GregorianCalendar periodEnd = new GregorianCalendar();
        periodEnd.setTime(booking.getEndDate());
        periodEnd.add(Calendar.MINUTE, 15); //And 15 minutes after
        return periodStart.getTime().before(now) && periodEnd.getTime().after(now);
    }

    @Override
    public boolean onMenuItemClick(final MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.menu_panic_cancel:
                startActivity(HelpActivity.DeepLink.CANCEL.getIntent(getActivity()));
                break;
            case R.id.menu_panic_pro_late:
                startActivity(HelpActivity.DeepLink.PRO_LATE.getIntent(getActivity()));
                break;
            case R.id.menu_panic_adjust_hours:
                startActivity(HelpActivity.DeepLink.ADJUST_HOURS.getIntent(getActivity()));
                break;
            case R.id.menu_panic_help:
                startActivity(new Intent(getActivity(), HelpActivity.class));
            default:
                return false;
        }
        return true;
    }

    public Configuration getConfiguration()
    {
        return mConfiguration;
    }

    public void setRescheduleType(final RescheduleType rescheduleType)
    {
        mRescheduleType = rescheduleType;
    }

    public enum RescheduleType
    {
        NORMAL, FROM_CANCELATION
    }
}

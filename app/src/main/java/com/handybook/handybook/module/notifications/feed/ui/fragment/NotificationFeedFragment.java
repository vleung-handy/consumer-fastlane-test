package com.handybook.handybook.module.notifications.feed.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.deeplink.DeepLinkUtils;
import com.handybook.handybook.module.notifications.HandyNotificationSplashPromoConverter;
import com.handybook.handybook.module.notifications.feed.HandyNotificationActionHandler;
import com.handybook.handybook.module.notifications.feed.NotificationFeedEvent;
import com.handybook.handybook.module.notifications.feed.NotificationRecyclerViewAdapter;
import com.handybook.handybook.module.notifications.feed.model.HandyNotification;
import com.handybook.handybook.module.notifications.feed.model.MarkNotificationsAsReadRequest;
import com.handybook.handybook.module.notifications.feed.viewmodel.HandyNotificationViewModel;
import com.handybook.handybook.module.notifications.splash.SplashNotificationEvent;
import com.handybook.handybook.module.notifications.splash.model.SplashPromo;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.view.EmptiableRecyclerView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationFeedFragment extends InjectedFragment
        implements SwipeRefreshLayout.OnRefreshListener, HandyNotificationActionHandler
{

    private static final String ARG_NOTIFICATIONS = "NOTIFICATIONS";

    private NotificationRecyclerViewAdapter mNotificationRecyclerViewAdapter;
    private HandyNotification.List mNotifications;

    private ActionBar mActionBar;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.notification_feed_srl)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.notifications_feed_rv)
    EmptiableRecyclerView mEmptiableRecyclerView;
    @Bind(R.id.card_empty)
    View mEmptyView;
    @Bind(R.id.card_empty_text)
    TextView mNoBookingsText;

    public NotificationFeedFragment() {}

    public static NotificationFeedFragment newInstance()
    {
        NotificationFeedFragment fragment = new NotificationFeedFragment();
        return fragment;

    }

    public static NotificationFeedFragment newInstance(final HandyNotification.List notifications)
    {
        NotificationFeedFragment fragment = new NotificationFeedFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTIFICATIONS, notifications);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    )
    {
        View root = inflater.inflate(R.layout.fragment_notification_feed, container, false);
        ButterKnife.bind(this, root);
        // Toolbar
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(mToolbar);
        mActionBar = activity.getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        // SwipeRefresh
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.handy_service_handyman,
                R.color.handy_service_electrician,
                R.color.handy_service_cleaner,
                R.color.handy_service_painter,
                R.color.handy_service_plumber
        );
        // RecyclerView
        mNotificationRecyclerViewAdapter = new NotificationRecyclerViewAdapter(mNotifications, this);
        mEmptiableRecyclerView.setAdapter(mNotificationRecyclerViewAdapter);
        mEmptiableRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        mEmptiableRecyclerView.setEmptyView(mEmptyView);
        // Only allow SwipeRefresh when Recycler scrolled all the way up
        mEmptiableRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(-1))
                {
                    mSwipeRefreshLayout.setEnabled(true);
                }
                else
                {
                    mSwipeRefreshLayout.setEnabled(false);
                }
            }
        });
        if (getArguments() != null)
        {
            mNotificationRecyclerViewAdapter.mergeNotifications(
                    (HandyNotification.List) getArguments().getSerializable(ARG_NOTIFICATIONS)
            );
        }
        else
        {
            requestNotifications();
        }
        return root;
    }

    @Override
    public final void onStart()
    {
        super.onStart();
        // Workaround to be able to display the SwipeRefreshLayout onStart
        // as in: http://stackoverflow.com/a/26860930/486332
        TypedValue typed_value = new TypedValue();
        getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));
        // Workaround to actually get the SwipeRefreshLayout to show it's refreshing
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onRefresh()
    {
        requestNotifications();
    }

    /**
     * currently marking notifications as read once we receive them from the server
     * <p/>
     * TODO: mark as read only when user actually views them
     *
     * @param notifications
     */
    private void markNotificationsAsRead(@NonNull HandyNotification.List notifications)
    {
        if (userManager.isUserLoggedIn())
        {
            //request to mark only the unread notifications as read
            List<Long> readNotificationIdsList = new ArrayList<>();
            for (HandyNotification handyNotification : notifications)
            {
                if (!handyNotification.isRead())
                {
                    readNotificationIdsList.add(handyNotification.getId());
                }
            }
            if (!readNotificationIdsList.isEmpty())
            {
                bus.post(
                        new NotificationFeedEvent.RequestMarkNotificationAsRead(
                                new MarkNotificationsAsReadRequest(readNotificationIdsList)
                        )
                );
            }

            MarkNotificationsAsReadRequest markNotificationsAsReadRequest =
                    new MarkNotificationsAsReadRequest(readNotificationIdsList);
            bus.post(new NotificationFeedEvent.RequestMarkNotificationAsRead(
                    markNotificationsAsReadRequest));
        }

    }

    @Subscribe
    public void onNotificationResponseReceived(final NotificationFeedEvent.HandyNotificationsSuccess e)
    {
        if (e.getPayload() != null)
        {
            HandyNotification.List notificationsList = e.getPayload().getHandyNotifications();
            if (notificationsList != null && !notificationsList.isEmpty())
            {
                mNotificationRecyclerViewAdapter.mergeNotifications(e.getPayload().getHandyNotifications());

                //mark these notifications as read
                markNotificationsAsRead(notificationsList);
            }
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe
    public void onNotificationResponseError(final NotificationFeedEvent.HandyNotificationsError e)
    {
        showToast(e.getPayload().getMessage());
        mSwipeRefreshLayout.setRefreshing(false);
    }


    private void requestNotifications()
    {
        mSwipeRefreshLayout.setRefreshing(true);
        bus.post(new NotificationFeedEvent.HandyNotificationsEvent(null, null, null));
    }

    /**
     * does something based on the Action item in a notification feed item
     *
     * called when certain views in a notification feed item are clicked
     * @param action
     * @return
     */
    @Override
    public boolean handleNotificationAction(@Nullable HandyNotification.Action action)
    {
        if (action == null)
        {
            Crashlytics.logException(
                    new RuntimeException("Action is now null"));
        }
        else
        {
            final String deeplink = action.getDeeplink();
            if (deeplink == null)
            {
                Crashlytics.logException(new RuntimeException("Action without a deeplink received: "
                        + action.toString()));
            }
            else
            {
                return DeepLinkUtils.safeLaunchDeepLink(deeplink, getContext());
            }
        }
        return false;
    }

    /**
     * called when a notification feed promo item is clicked
     *
     * currently requests to launch the splash promo
     * @param promoNotificationViewModel
     */
    @Override
    public void handleNotificationPromoItemClicked(@NonNull HandyNotificationViewModel
                                                               promoNotificationViewModel)
    {
        SplashPromo splashPromo = HandyNotificationSplashPromoConverter.
                convertToSplashPromo(promoNotificationViewModel, getContext());
        bus.post(new SplashNotificationEvent.RequestShowSplashPromo(splashPromo));
    }
}

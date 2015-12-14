package com.handybook.handybook.module.notifications.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.handybook.handybook.R;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.module.notifications.model.response.HandyNotification;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.fragment.NotificationRecyclerViewAdapter;
import com.handybook.handybook.ui.view.EmptiableRecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationFeedFragment extends InjectedFragment
        implements SwipeRefreshLayout.OnRefreshListener
{

    private static final String ARG_NOTIFICATIONS = "NOTIFICATIONS";
    private HandyNotification.List mNotifications;

    @Bind(R.id.notification_feed_srl)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.notifications_feed_rv)
    EmptiableRecyclerView mEmptiableRecyclerView;
    @Bind(R.id.card_empty)
    CardView mEmptyView;
    @Bind(R.id.card_empty_text)
    TextView mNoBookingsText;
    private NotificationRecyclerViewAdapter mNotificationRecyclerViewAdapter;

    public NotificationFeedFragment()
    {
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_notification_feed, container, false);
        ButterKnife.bind(this, root);
        mNotificationRecyclerViewAdapter = new NotificationRecyclerViewAdapter(mNotifications);
        mEmptiableRecyclerView.setAdapter(mNotificationRecyclerViewAdapter);
        mEmptiableRecyclerView.setEmptyView(mEmptyView);
        if (getArguments() != null)
        {
            mNotificationRecyclerViewAdapter.mergeNotifications(
                    (HandyNotification.List)getArguments().getSerializable(ARG_NOTIFICATIONS)
            );
        }

        return root;
    }

    @Override
    public void onRefresh()
    {
        requestNotifications();
    }

    @Subscribe()
    void onNotificationReceived(final HandyEvent.ResponseEvent.HandyNotificationsSuccess e)
    {
        mNotificationRecyclerViewAdapter.mergeNotifications(e.getPayload().getHandyNotifications());
    }


    private void requestNotifications()
    {
        mSwipeRefreshLayout.setRefreshing(true);
        bus.post(
                new HandyEvent.RequestEvent.HandyNotificationsEvent(
                        Long.parseLong(userManager.getCurrentUser().getId()),
                        null,
                        null,
                        null
                )
        );
    }

}

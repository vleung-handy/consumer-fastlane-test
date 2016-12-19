package com.handybook.handybook.notifications.feed.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.event.HandyEvent;
import com.handybook.handybook.notifications.feed.model.HandyNotification;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.core.ui.fragment.NotificationRecyclerViewAdapter;
import com.handybook.handybook.library.ui.view.EmptiableRecyclerView;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationFeedFragment extends InjectedFragment
        implements SwipeRefreshLayout.OnRefreshListener
{

    private static final String ARG_NOTIFICATIONS = "NOTIFICATIONS";
    private HandyNotification.List mNotifications;

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
    private NotificationRecyclerViewAdapter mNotificationRecyclerViewAdapter;

    public NotificationFeedFragment()
    {
    }

    public static NotificationFeedFragment newInstance()
    {
        return new NotificationFeedFragment();
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
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mEmptiableRecyclerView.setAdapter(mNotificationRecyclerViewAdapter);
        mEmptiableRecyclerView.setEmptyView(mEmptyView);
        if (getArguments() != null)
        {
            mNotificationRecyclerViewAdapter.mergeNotifications(
                    (HandyNotification.List)getArguments().getSerializable(ARG_NOTIFICATIONS)
            );
        } else {
            requestNotifications();
        }

        return root;
    }

    @Override
    public void onRefresh()
    {
        requestNotifications();
    }

    @Subscribe
    public void onNotificationResponseReceived(final HandyEvent.ResponseEvent.HandyNotificationsSuccess e)
    {
        mNotificationRecyclerViewAdapter.mergeNotifications(e.getPayload().getHandyNotifications());
    }

    @Subscribe
    public void onNotificationResponseError(final HandyEvent.ResponseEvent.HandyNotificationsError e){
        showToast(e.getPayload().getMessage());
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

package com.handybook.handybook.module.notifications.feed.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.User;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.module.notifications.feed.model.HandyNotification;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.module.notifications.feed.NotificationRecyclerViewAdapter;
import com.handybook.handybook.ui.view.EmptiableRecyclerView;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationFeedFragment extends InjectedFragment
        implements SwipeRefreshLayout.OnRefreshListener
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
    CardView mEmptyView;
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
        mNotificationRecyclerViewAdapter = new NotificationRecyclerViewAdapter(mNotifications);
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

    @Subscribe
    public void onNotificationResponseReceived(final HandyEvent.ResponseEvent.HandyNotificationsSuccess e)
    {
        mNotificationRecyclerViewAdapter.mergeNotifications(e.getPayload().getHandyNotifications());
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe
    public void onNotificationResponseError(final HandyEvent.ResponseEvent.HandyNotificationsError e)
    {
        showToast(e.getPayload().getMessage());
        mSwipeRefreshLayout.setRefreshing(false);
    }


    private void requestNotifications()
    {
        mSwipeRefreshLayout.setRefreshing(true);
        if (userManager.isLoggedIn()){

            final User currentUser = userManager.getCurrentUser();
            bus.post(
                    new HandyEvent.RequestEvent.HandyNotificationsEvent(
                            Long.parseLong(currentUser.getId()),
                            currentUser.getAuthToken(),
                            null,
                            null,
                            null
                    )
            );

        } else {
            bus.post(
                    new HandyEvent.RequestEvent.HandyNotificationsEvent(
                            null,
                            null,
                            null
                    )
            );
        }
    }

}

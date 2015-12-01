package com.handybook.handybook.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.module.notifications.model.HandyNotification;
import com.handybook.handybook.module.notifications.model.HandyNotificationViewModel;

public class NotificationFeedFragment extends Fragment
{

    private static final String ARG_NOTIFICATIONS = "NOTIFICATIONS";
    private HandyNotification.List mNotifications;

    public NotificationFeedFragment()
    {
    }

    public static NotificationFeedFragment newInstance(){
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
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            mNotifications = (HandyNotification.List) getArguments().getSerializable(ARG_NOTIFICATIONS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_notification_feed, container, false);

        // Set the adapter
        if (view instanceof RecyclerView)
        {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(
                    new NotificationRecyclerViewAdapter(
                            HandyNotificationViewModel.List.from(mNotifications)
                    )
            );
        }
        return view;
    }

}

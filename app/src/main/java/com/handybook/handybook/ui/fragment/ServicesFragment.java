package com.handybook.handybook.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.BookingManager;
import com.handybook.handybook.core.Service;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.ui.activity.ServicesActivity;
import com.handybook.handybook.util.TextUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class ServicesFragment extends BookingFlowFragment {
    static final String EXTRA_SERVICE = "com.handy.handy.EXTRA_SERVICE";
    static final String EXTRA_NAV_HEIGHT = "com.handy.handy.EXTRA_NAV_HEIGHT";

    private Service service;
    private ArrayList<Service> services;

    @Inject BookingManager bookingManager;
    @Inject UserManager userManager;

    @Bind(R.id.nav_text)
    TextView navText;
    @Bind(android.R.id.list)
    ListView listView;

    public static ServicesFragment newInstance(final Service service, final int navHeight) {
        final ServicesFragment fragment = new ServicesFragment();
        final Bundle args = new Bundle();
        args.putParcelable(EXTRA_SERVICE, service);
        args.putInt(EXTRA_NAV_HEIGHT, navHeight);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = getArguments().getParcelable(EXTRA_SERVICE);
        services = new ArrayList<>(service.getServices());
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_services,container, false);

        ButterKnife.bind(this, view);
        navText.setText(TextUtils.toTitleCase(service.getName()));

        return view;
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view,
                                    final int i, final long l) {
                final Service next = service.getServices().get(i);
                if (next.getServices().size() > 0) {
                    final Intent intent = new Intent(getActivity(), ServicesActivity.class);
                    intent.putExtra(ServicesActivity.EXTRA_SERVICE, next);
                    intent.putExtra(ServicesActivity.EXTRA_NAV_HEIGHT, navText.getHeight());
                    startActivity(intent);
                }
                else startBookingFlow(next.getId(), next.getUniq());
            }
        });
    }

    @Override
    public final void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setAdapter(new ArrayAdapter<Service>(getActivity(),
                R.layout.list_item_service, services) {
            @Override
            public final View getView(final int position, final View convertView, final ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    final LayoutInflater inflater = (LayoutInflater)getContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.list_item_service, parent, false);
                }

                final Service service = services.get(position);
                final TextView item = (TextView)view.findViewById(R.id.service);
                item.setText(service.getName());
                return view;
            }
        });
    }
}

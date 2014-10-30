package com.handybook.handybook;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class ServicesFragment extends InjectedListFragment {
    static final String EXTRA_SERVICES = "com.handy.handy.EXTRA_SERVICES";
    static final String EXTRA_NAV_HEIGHT = "com.handy.handy.EXTRA_NAV_HEIGHT";

    private ArrayList<Service> services = new ArrayList<>();

    @InjectView(R.id.nav_text) TextView navText;

    static ServicesFragment newInstance(final ArrayList<Service> services, final int navHeight) {
        final ServicesFragment fragment = new ServicesFragment();
        final Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_SERVICES, services);
        args.putInt(EXTRA_NAV_HEIGHT, navHeight);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        services = getArguments().getParcelableArrayList(EXTRA_SERVICES);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_services, container, false);
        ButterKnife.inject(this, view);

        final int navHeight;
        if ((navHeight = getArguments().getInt(EXTRA_NAV_HEIGHT)) > 0) navText.setHeight(navHeight);

        return view;
    }

    @Override
    public final void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setAdapter(new ArrayAdapter<Service>(getActivity(),
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

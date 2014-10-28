package com.handybook.handybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class ServicesFragment extends InjectedFragment {
    static final String EXTRA_SERVICES = "com.handy.handy.EXTRA_SERVICES";

    private ArrayList<Service> services = new ArrayList<>();

    @InjectView(R.id.demo) TextView demo;

    static ServicesFragment newInstance(final ArrayList<Service> services) {
        final ServicesFragment fragment = new ServicesFragment();
        final Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_SERVICES, services);
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

        String s = "";
        for (Service service : services) {
            s += "\n" + service.getName();
        }
        demo.setText(s);

        return view;
    }
}

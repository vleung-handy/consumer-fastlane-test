package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Service;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.ui.activity.ServicesActivity;
import com.handybook.handybook.ui.widget.MenuButton;
import com.handybook.handybook.ui.widget.ServiceCategoryView;
import com.handybook.handybook.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class ServiceCategoriesFragment extends BookingFlowFragment {
    private List<Service> services = new ArrayList<>();
    private boolean usedCache;

    @InjectView(R.id.category_layout) LinearLayout categoryLayout;
    @InjectView(R.id.logo) ImageView logo;
    @InjectView(R.id.menu_button_layout) ViewGroup menuButtonLayout;

    public static ServiceCategoriesFragment newInstance() {
        return new ServiceCategoriesFragment();
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (!prefs.getBoolean("APP_OPENED_PREV", false)) {
            mixpanel.trackEventFirstTimeUse();

            final SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("APP_OPENED_PREV", true);
            edit.apply();
        }
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_service_categories,container, false);

        ButterKnife.inject(this, view);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AnimationDrawable logoSpin = (AnimationDrawable) logo.getBackground();
                logoSpin.stop();
                logoSpin.start();
            }
        });

        final MenuButton menuButton = new MenuButton(getActivity());
        Utils.extendHitArea(menuButton, menuButtonLayout, Utils.toDP(32, getActivity()));
        menuButtonLayout.addView(menuButton);

        return view;
    }

    @Override
    public final void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        allowCallbacks = true;
        loadServices();
    }

    private void displayServices() {
        categoryLayout.removeAllViews();
        int pos = 0;

        for (final Service service : services) {
            final ServiceCategoryView categoryView = new ServiceCategoryView(getActivity());

            categoryView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

            categoryView.setText(service.getName());

            categoryView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (service.getServices().size() > 0) {
                        final Intent intent = new Intent(getActivity(), ServicesActivity.class);
                        intent.putExtra(ServicesActivity.EXTRA_SERVICE, service);
                        intent.putExtra(ServicesActivity.EXTRA_NAV_HEIGHT, categoryView.getHeight());
                        startActivity(intent);
                    }
                    else startBookingFlow(service.getId(), service.getUniq());
                }
            });
            categoryLayout.addView(categoryView, pos++);
        }
    }

    private void loadServices() {
        progressDialog.show();
        usedCache = false;

        dataManager.getServices(new DataManager.CacheResponse<List<Service>>() {
            @Override
            public void onResponse(final List<Service> response) {
                if (!allowCallbacks) return;
                usedCache = true;
                services = response;
                displayServices();
                progressDialog.dismiss();
            }
        },
        new DataManager.Callback<List<Service>>() {
            @Override
            public void onSuccess(final List<Service> response) {
                if (!allowCallbacks) return;
                services = response;
                displayServices();
                progressDialog.dismiss();
            }

            @Override
            public void onError(final DataManager.DataManagerError error) {
                if (!allowCallbacks || usedCache) return;
                progressDialog.dismiss();
                dataManagerErrorHandler.handleError(getActivity(), error);
            }
        });
    }
}
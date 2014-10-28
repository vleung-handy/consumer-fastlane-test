package com.handybook.handybook;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class ServiceCategoriesFragment extends InjectedFragment {
    private List<Service> services = new ArrayList<>();
    private ProgressDialog progressDialog;

    @InjectView(R.id.category_layout) LinearLayout categoryLayout;
    @InjectView(R.id.logo) ImageView logo;

    @Inject UserManager userManager;
    @Inject DataManager dataManager;
    @Inject DataManagerErrorHandler dataManagerErrorHandler;

    static ServiceCategoriesFragment newInstance() {
        return new ServiceCategoriesFragment();
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_service_categories, container, false);
        ButterKnife.inject(this, view);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setDelay(500);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AnimationDrawable logoSpin = (AnimationDrawable) logo.getBackground();
                logoSpin.stop();
                logoSpin.start();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadServices();
    }

    private void displayServices() {
        categoryLayout.removeAllViews();
        for (final Service service : services) {
            final ServiceCategoryView categoryView = new ServiceCategoryView(getActivity());

            categoryView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

            categoryView.setText(service.getName());

            categoryView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userManager.getCurrentUser() == null) {
                        Toast.makeText(getActivity(), "Please Login", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final ArrayList<Service> nextServices;
                    if ((nextServices = new ArrayList<>(service.getServices())).size() > 0) {
                        final Intent intent = new Intent(getActivity(), ServicesActivity.class);
                        intent.putParcelableArrayListExtra(ServicesActivity.EXTRA_SERVICES, nextServices);
                        startActivity(intent);
                    }
                }
            });
            categoryLayout.addView(categoryView, 0);
        }
    }

    private void loadServices() {
        progressDialog.show();
        dataManager.getServices(new DataManager.CacheResponse<List<Service>>() {
            @Override
            public void onResponse(final List<Service> response) {
                services = response;
                displayServices();
                progressDialog.dismiss();
            }
        },
        new DataManager.Callback<List<Service>>() {
            @Override
            public void onSuccess(final List<Service> response) {
                services = response;
                displayServices();
                progressDialog.dismiss();
            }

            @Override
            public void onError(final DataManager.DataManagerError error) {
                progressDialog.dismiss();
                dataManagerErrorHandler.handleError(getActivity(), error);
            }
        });
    }
}

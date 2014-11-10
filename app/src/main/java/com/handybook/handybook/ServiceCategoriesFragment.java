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
    private boolean usedCache;

    @InjectView(R.id.category_layout) LinearLayout categoryLayout;
    @InjectView(R.id.logo) ImageView logo;

    @Inject UserManager userManager;
    @Inject DataManager dataManager;
    @Inject BookingRequestManager requestManager;
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
                    if (userManager.getCurrentUser() == null) {
                        Toast.makeText(getActivity(), "Please Login", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (service.getServices().size() > 0) {
                        final Intent intent = new Intent(getActivity(), ServicesActivity.class);
                        intent.putExtra(ServicesActivity.EXTRA_SERVICE, service);
                        intent.putExtra(ServicesActivity.EXTRA_NAV_HEIGHT, categoryView.getHeight());
                        startActivity(intent);
                    }
                    else {
                        final BookingRequest request = new BookingRequest();
                        request.setServiceId(service.getId());
                        requestManager.setCurrentRequest(request);

                        final Intent intent = new Intent(getActivity(), BookingLocationActivity.class);
                        startActivity(intent);
                    }
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

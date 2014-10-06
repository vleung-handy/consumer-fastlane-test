package com.handybook.handybook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class ServiceCategoriesFragment extends InjectedFragment {
    @InjectView(R.id.category_layout) LinearLayout categoryLayout;
    @Inject DataManager dataManager;

    private String[] services;

    static ServiceCategoriesFragment newInstance() {
        return new ServiceCategoriesFragment();
    }

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        services = dataManager.getServices();
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_service_categories, container, false);
        ButterKnife.inject(this, view);

        for (String category : services) {
            final ServiceCategoryView categoryView = new ServiceCategoryView(getActivity());
            categoryView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
            categoryView.setText(category);

            categoryView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ServiceCategoriesActivity.class);
                    startActivity(intent);
                }
            });

            categoryLayout.addView(categoryView, 0);
        }

        return view;
    }

    @Override
    public final void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}

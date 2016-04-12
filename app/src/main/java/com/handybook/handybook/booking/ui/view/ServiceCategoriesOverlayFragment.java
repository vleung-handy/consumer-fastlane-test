package com.handybook.handybook.booking.ui.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;

import com.handybook.handybook.R;
import com.handybook.handybook.analytics.MixpanelEvent;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.ui.activity.ServicesActivity;
import com.handybook.handybook.booking.ui.fragment.BookingFlowFragment;
import com.handybook.handybook.constant.BundleKeys;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ServiceCategoriesOverlayFragment extends BookingFlowFragment
{
    private static final String SHARED_ICON_ELEMENT_NAME = "icon";
    private static final int SERVICE_CATEGORY_MOVEMENT_DURATION_MILLIS = 200;
    private static final int SERVICE_CATEGORIES_ANIMATION_DELAY_MILLIS = 200;
    private static final int FRAGMENT_DISMISSAL_DELAY_MILLIS = 200;

    @Bind(R.id.close_services_button_wrapper)
    View mCloseButtonWrapper;
    @Bind(R.id.close_button)
    View mCloseButton;
    @Bind(R.id.services_wrapper)
    ViewGroup mServicesWrapper;
    List<ServiceCategorySimpleView> mServiceCategorySimpleViews;

    public static ServiceCategoriesOverlayFragment newInstance(@NonNull List<Service> services)
    {
        ServiceCategoriesOverlayFragment fragment = new ServiceCategoriesOverlayFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelableArrayList(BundleKeys.SERVICES, new ArrayList<>(services));
        fragment.setArguments(arguments);
        return fragment;
    }

    @OnClick(R.id.close_button)
    public void onCloseButtonClicked()
    {
        bus.post(new MixpanelEvent.TrackAddBookingFabMenuDismissed());
        getActivity().onBackPressed();
    }

    public void animateAndDismissFragment()
    {
        if (isRemoving())
        {
            //this fragment is already being removed, exit and do nothing. This typically
            //happens if someone is frantically tapping the back button to remove this fragment
            return;
        }
        mCloseButton.setClickable(false);
        for (ServiceCategorySimpleView view : mServiceCategorySimpleViews)
        {
            float toYDelta = mCloseButtonWrapper.getY() - view.getY();
            TranslateAnimation animation =
                    new TranslateAnimation(0, 0, 0, toYDelta);
            animation.setDuration(SERVICE_CATEGORY_MOVEMENT_DURATION_MILLIS);
            view.startAnimation(animation);
            view.getTitle().setVisibility(View.INVISIBLE);
            view.setVisibility(View.INVISIBLE);
        }

        mServicesWrapper.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }, FRAGMENT_DISMISSAL_DELAY_MILLIS);

    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        View view = inflater.inflate(R.layout.fragment_service_categories_overlay,
                container, false);
        ButterKnife.bind(this, view);
        initServices();
        bus.post(new MixpanelEvent.TrackAddBookingFabMenuShown());
        return view;
    }

    private void initServices()
    {
        mCloseButton.setClickable(false);
        ArrayList<Service> services = getArguments().getParcelableArrayList(BundleKeys.SERVICES);
        mServiceCategorySimpleViews = new ArrayList<>();
        for (final Service service : services)
        {
            final ServiceCategorySimpleView serviceCategorySimpleView =
                    new ServiceCategorySimpleView(getContext());
            serviceCategorySimpleView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final android.view.View v)
                {
                    handleServiceCategoryClicked(serviceCategorySimpleView, service);
                }
            });
            mServicesWrapper.addView(serviceCategorySimpleView,
                    mServicesWrapper.getChildCount() - 1);
            serviceCategorySimpleView.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    serviceCategorySimpleView.init(service);
                    float fromYDelta =
                            mCloseButtonWrapper.getY() - serviceCategorySimpleView.getY();
                    TranslateAnimation animation = new TranslateAnimation(0, 0, fromYDelta, 0);
                    animation.setDuration(SERVICE_CATEGORY_MOVEMENT_DURATION_MILLIS);
                    animation.setInterpolator(new OvershootInterpolator());
                    animation.setAnimationListener(mServiceIconAnimationListener);
                    serviceCategorySimpleView.startAnimation(animation);
                    serviceCategorySimpleView.setVisibility(View.VISIBLE);
                }
            }, SERVICE_CATEGORIES_ANIMATION_DELAY_MILLIS);
            mServiceCategorySimpleViews.add(serviceCategorySimpleView);
        }
    }

    private final Animation.AnimationListener mServiceIconAnimationListener =
            new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart(final Animation animation)
                {
                }

                @Override
                public void onAnimationEnd(final Animation animation)
                {
                    mCloseButton.setClickable(true);
                }

                @Override
                public void onAnimationRepeat(final Animation animation)
                {
                }
            };


    private void handleServiceCategoryClicked(
            final ServiceCategorySimpleView serviceCategorySimpleView,
            final Service service
    )
    {
        bus.post(new MixpanelEvent.TrackAddBookingFabServiceSelected(service.getId(), service.getUniq()));
        if (service.getServices().size() > 0)
        {
            final Intent intent = new Intent(getActivity(), ServicesActivity.class);
            intent.putExtra(ServicesActivity.EXTRA_SERVICE, service);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(), serviceCategorySimpleView.getIcon(), SHARED_ICON_ELEMENT_NAME
                );
                getActivity().startActivity(intent, options.toBundle());
            }
            else
            {
                startActivity(intent);
            }
        }
        else
        {
            startBookingFlow(service.getId(), service.getUniq());
        }
    }
}

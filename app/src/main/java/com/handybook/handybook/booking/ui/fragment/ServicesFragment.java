package com.handybook.handybook.booking.ui.fragment;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.ui.view.ServiceView;
import com.handybook.handybook.ui.descriptor.ServiceCategoryDescriptor;
import com.handybook.handybook.ui.descriptor.ServiceDescriptor;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class ServicesFragment extends BookingFlowFragment
{
    private static final String EXTRA_SERVICE = "com.handy.handy.EXTRA_SERVICE";

    private Service mService;

    @Bind(R.id.content)
    ScrollView mContent;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.header)
    ViewGroup mHeader;
    @Bind(R.id.list)
    ViewGroup mList;
    @Bind(R.id.icon)
    ImageView mIcon;
    @Bind(R.id.toolbar_icon)
    ImageView mToolbarIcon;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.subtitle)
    TextView mSubtitle;

    public static ServicesFragment newInstance(final Service service)
    {
        final ServicesFragment fragment = new ServicesFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_SERVICE, service);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_services, container, false);

        ButterKnife.bind(this, view);
        mService = getArguments().getParcelable(EXTRA_SERVICE);

        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        try
        {
            String serviceCategoryMachineName = mService.getUniq().toUpperCase();
            final ServiceCategoryDescriptor descriptor = ServiceCategoryDescriptor.valueOf(serviceCategoryMachineName);
            initStatusBar(descriptor);
            initHeader(descriptor);
            initServiceIcon(descriptor);
            initToolbar(descriptor);
            initHeaderAdjustmentsOnScroll(descriptor);
        } catch (IllegalArgumentException e)
        {
            Crashlytics.logException(new RuntimeException("Cannot display service: " + mService.getUniq()));
        }
    }

    private void initStatusBar(ServiceCategoryDescriptor descriptor)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getActivity().getWindow().setStatusBarColor(getResources().getColor(descriptor.getColorDark()));
        }
    }

    private void initHeader(ServiceCategoryDescriptor descriptor)
    {
        mTitle.setText(descriptor.getTitle());
        mSubtitle.setText(descriptor.getSlogan());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mHeader.setBackgroundResource(descriptor.getBackground());
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    forceRippleAnimation();
                }
            }, 200);
        }
        else
        {
            mHeader.setBackgroundColor(getResources().getColor(descriptor.getColor()));
        }
    }

    private void initToolbar(ServiceCategoryDescriptor descriptor)
    {
        mToolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        mToolbarIcon.setImageResource(descriptor.getIcon());
    }

    private void initServiceIcon(ServiceCategoryDescriptor descriptor)
    {
        mIcon.setImageResource(descriptor.getIcon());
        mIcon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                forceRippleAnimation();
            }
        });
    }

    private void initHeaderAdjustmentsOnScroll(final ServiceCategoryDescriptor descriptor)
    {
        mContent.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener()
        {
            @Override
            public void onScrollChanged()
            {
                int[] toolbarCoordinates = new int[2];
                int[] titleCoordinates = new int[2];
                mToolbar.getLocationOnScreen(toolbarCoordinates);
                mTitle.getLocationOnScreen(titleCoordinates);
                int toolbarY = toolbarCoordinates[1];
                int titleY = titleCoordinates[1];

                if (toolbarY > titleY)
                {
                    adjustForSmallerHeader(descriptor.getColor());
                }

                if (toolbarY < titleY)
                {
                    adjustForLargerHeader();
                }
            }
        });
    }

    private void adjustForLargerHeader()
    {
        mTitle.setVisibility(View.VISIBLE);
        mSubtitle.setVisibility(View.VISIBLE);
        showView(mToolbarIcon, new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                hideView(mIcon, null);
                mToolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }
        });
    }

    private void adjustForSmallerHeader(final int color)
    {
        mTitle.setVisibility(View.INVISIBLE);
        mSubtitle.setVisibility(View.INVISIBLE);
        showView(mIcon, new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                hideView(mToolbarIcon, null);
                mToolbar.setBackgroundColor(getResources().getColor(color));
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }
        });
    }

    private void hideView(View view, @Nullable Animation.AnimationListener listener)
    {
        animateVisibility(view, View.VISIBLE, android.R.anim.fade_in, listener);
    }

    private void showView(View view, @Nullable Animation.AnimationListener listener)
    {
        animateVisibility(view, View.INVISIBLE, android.R.anim.fade_out, listener);
    }

    private void animateVisibility(final View view, final int visibility, final int animId, @Nullable Animation.AnimationListener listener)
    {
        if (view.getVisibility() != visibility)
        {
            final Animation animation = AnimationUtils.loadAnimation(getActivity(), animId);
            if (listener != null)
            {
                animation.setAnimationListener(listener);
            }
            view.startAnimation(animation);
            view.setVisibility(visibility);
        }
    }

    private void forceRippleAnimation()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Drawable background = mHeader.getBackground();
            if (background instanceof RippleDrawable)
            {
                int[] iconCoordinates = new int[2];
                mIcon.getLocationOnScreen(iconCoordinates);
                float hotspotX = mHeader.getWidth() / 2;
                float hotspotY = iconCoordinates[1] - getStatusBarHeight() + (mIcon.getHeight() / 2);

                final RippleDrawable ripple = (RippleDrawable) background;
                int[] state = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
                ripple.setState(state);
                ripple.setHotspot(hotspotX, hotspotY);
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ripple.setState(new int[]{});
                    }
                }, 400);
            }
        }
    }

    private int getStatusBarHeight()
    {
        int result = 0;
        final Resources resources = getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
        {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public final void onActivityCreated(final Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        ServiceView lastViewAdded = null;
        for (final Service service : mService.getServices())
        {
            ServiceView serviceView = new ServiceView(getActivity());
            String serviceMachineName = service.getUniq().toUpperCase();
            if (ServiceDescriptor.hasValueOf(serviceMachineName))
            {
                ServiceDescriptor serviceDescriptor = ServiceDescriptor.valueOf(serviceMachineName);
                serviceView.init(serviceDescriptor);
                serviceView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        startBookingFlow(service.getId(), service.getUniq());
                    }
                });
                mList.addView(serviceView);
                lastViewAdded = serviceView;
            }
        }

        if (lastViewAdded != null)
        {
            // remove bottom border of last element
            final View container = lastViewAdded.findViewById(R.id.container);
            if (container != null)
            {
                container.setBackgroundResource(0);
            }
        }
    }

}

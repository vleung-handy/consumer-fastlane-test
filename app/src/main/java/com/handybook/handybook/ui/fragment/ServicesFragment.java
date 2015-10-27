package com.handybook.handybook.ui.fragment;

import android.os.Build;
import android.os.Bundle;
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
import com.handybook.handybook.core.Service;
import com.handybook.handybook.ui.widget.ServiceView;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class ServicesFragment extends BookingFlowFragment
{
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
        fragment.mService = service;
        return fragment;
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_services, container, false);

        ButterKnife.bind(this, view);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getActivity().onBackPressed();
            }
        });
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        return view;
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        try
        {
            String serviceCategoryMachineName = mService.getUniq().toUpperCase();
            final ServiceCategoryAttributes attributes = ServiceCategoryAttributes.valueOf(serviceCategoryMachineName);
            mHeader.setBackgroundColor(getResources().getColor(attributes.getColor()));
            mIcon.setImageResource(attributes.getIcon());
            mToolbarIcon.setImageResource(attributes.getIcon());
            mTitle.setText(attributes.getTitle());
            mSubtitle.setText(attributes.getSlogan());
            setStatusBarColor(attributes.getColorDark());

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
                        adjustForSmallerHeader(attributes.getColor());
                    }

                    if (toolbarY < titleY)
                    {
                        adjustForLargerHeader();
                    }
                }
            });
        } catch (IllegalArgumentException e)
        {
            Crashlytics.logException(new RuntimeException("Cannot display service: " + mService.getUniq()));
        }
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

    private void setStatusBarColor(int color)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getActivity().getWindow().setStatusBarColor(getResources().getColor(color));
        }
    }

    @Override
    public final void onActivityCreated(final Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        for (final Service service : mService.getServices())
        {
            ServiceView serviceView = new ServiceView(getActivity());
            if (serviceView.init(service))
            {
                serviceView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        startBookingFlow(service.getId(), service.getUniq());
                    }
                });
                mList.addView(serviceView);
            }
        }

        // remove bottom border of last element
        mList.getChildAt(mList.getChildCount() - 1).findViewById(R.id.container).setBackgroundResource(0);
    }

    @Override
    public void onPause()
    {
        mIcon.setVisibility(View.VISIBLE);
        super.onPause();
    }

    private enum ServiceCategoryAttributes
    {
        HANDYMAN(R.string.handyman, R.string.handyman_slogan_long, R.drawable.ic_handyman_fill, R.color.handy_service_handyman, R.color.handy_service_handyman_darkened),
        PLUMBING(R.string.plumber, R.string.plumber_slogan_long, R.drawable.ic_plumber_fill, R.color.handy_service_plumber, R.color.handy_service_plumber_darkened),
        ELECTRICIAN(R.string.electrician, R.string.electrician_slogan_long, R.drawable.ic_electrician_fill, R.color.handy_service_electrician, R.color.handy_service_electrician_darkened),;

        private final int mTitle;
        private final int mSlogan;
        private final int mIcon;
        private final int mColor;
        private final int mColorDark;

        ServiceCategoryAttributes(int title, int slogan, int icon, int color, int colorDark)
        {
            mTitle = title;
            mSlogan = slogan;
            mIcon = icon;
            mColor = color;
            mColorDark = colorDark;
        }

        public int getTitle()
        {
            return mTitle;
        }

        public int getIcon()
        {
            return mIcon;
        }

        public int getSlogan()
        {
            return mSlogan;
        }

        public int getColor()
        {
            return mColor;
        }

        public int getColorDark()
        {
            return mColorDark;
        }
    }
}

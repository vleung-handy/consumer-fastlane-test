package com.handybook.handybook.booking.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.ui.view.ServiceView;
import com.handybook.handybook.core.ui.descriptor.ServiceCategoryDescriptor;
import com.handybook.handybook.core.ui.descriptor.ServiceDescriptor;
import com.handybook.handybook.library.util.AnimationUtil;
import com.handybook.handybook.library.util.TransitionListenerAdapter;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.HandybookDefaultLog;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.os.Build.VERSION_CODES.LOLLIPOP;

public final class ServicesFragment extends BookingFlowFragment {

    private static final String EXTRA_SERVICE = "com.handy.handy.EXTRA_SERVICE";
    private static final int ANIMATION_DELAY_MS = 100;

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
    @Bind(R.id.list_wrapper)
    LinearLayout mListWrapper;

    Interpolator mInterpolator;
    private boolean mHasInComingEnterTransition;

    public static ServicesFragment newInstance(final Service service) {
        final ServicesFragment fragment = new ServicesFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_SERVICE, service);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && !getArguments().isEmpty()) {
            mService = getArguments().getParcelable(EXTRA_SERVICE);
        }
        if (mService != null) {
            bus.post(new LogEvent.AddLogEvent(new HandybookDefaultLog.SubServicePageShownLog(
                    mService.getId())));
        }
        else {
            bus.post(new LogEvent.AddLogEvent(new HandybookDefaultLog.SubServicePageShownLog(-1)));
        }

        mInterpolator = new LinearOutSlowInInterpolator();
        determineHasIncomingEnterTransition();
    }

    @SuppressWarnings("NewApi")
    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        final View view = getActivity().getLayoutInflater()
                                       .inflate(R.layout.fragment_services, container, false);

        ButterKnife.bind(this, view);

        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        if (shouldShowFancyAnimations()) {
            //there is an existing transition, then alter that to use the arc motion transition
            Transition transition = TransitionInflater
                    .from(getActivity())
                    .inflateTransition(R.transition.changebounds_with_arcmotion);

            getActivity().getWindow().setSharedElementEnterTransition(transition);
            transition.addListener(new TransitionListenerAdapter() {
                @Override
                public void onTransitionEnd(final Transition transition) {
                    revealHeader();
                }
            });
        }

        return view;
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            String serviceCategoryMachineName = mService.getUniq().toUpperCase();
            final ServiceCategoryDescriptor descriptor = ServiceCategoryDescriptor.valueOf(
                    serviceCategoryMachineName);
            initStatusBar(descriptor);
            initHeader(descriptor);
            initServiceIcon(descriptor);
            initToolbar(descriptor);
            initHeaderAdjustmentsOnScroll(descriptor);
        }
        catch (IllegalArgumentException e) {
            Crashlytics.logException(new RuntimeException(
                    "Cannot display service: " + mService.getUniq()));
        }
    }

    private void initStatusBar(ServiceCategoryDescriptor descriptor) {
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(
                    getContext(),
                    descriptor.getColorDark()
            ));
        }
    }

    /**
     * Determines whether or not there is an enter transition associated with this fragment. This
     * must be done onCreate()
     * @return
     */
    @TargetApi(LOLLIPOP)
    private void determineHasIncomingEnterTransition() {
        mHasInComingEnterTransition = getActivity().getWindow().getSharedElementEnterTransition() !=
                                      null;
    }

    /**
     * We should only show fancy animations if both the SDK supports it, and that
     * we are moving forward in the flow. If we are coming "back" into this activity, no need
     * for animations.
     * @return
     */
    private boolean shouldShowFancyAnimations() {
        return Build.VERSION.SDK_INT >= LOLLIPOP && mHasInComingEnterTransition;
    }

    private void initHeader(ServiceCategoryDescriptor descriptor) {
        mTitle.setText(descriptor.getTitle());
        mSubtitle.setText(descriptor.getSlogan());
        if (shouldShowFancyAnimations()) {
            mHeader.setBackgroundResource(descriptor.getBackground());
        }
        else {
            mHeader.setBackgroundColor(ContextCompat.getColor(getContext(), descriptor.getColor()));

            //just show everything, since there is no shared element transition on the activity level
            revealHeader();
        }
    }

    @SuppressWarnings("NewApi")
    public void revealHeader() {
        if (shouldShowFancyAnimations()) {
            AnimationUtil.revealView(
                    mHeader,
                    mIcon,
                    new AccelerateInterpolator(),
                    getResources().getInteger(R.integer.anim_duration_short),
                    new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(final Animator animation) {
                            animateListItems();
                        }
                    }
            );
        }
        else {
            mHeader.setVisibility(View.VISIBLE);
        }
    }

    private void initToolbar(ServiceCategoryDescriptor descriptor) {
        mToolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent));
        mToolbarIcon.setImageResource(descriptor.getIcon());
    }

    private void initServiceIcon(ServiceCategoryDescriptor descriptor) {
        mIcon.setImageResource(descriptor.getIcon());
        mIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forceRippleAnimation();
            }
        });
    }

    private void initHeaderAdjustmentsOnScroll(final ServiceCategoryDescriptor descriptor) {
        mContent.getViewTreeObserver()
                .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        int[] toolbarCoordinates = new int[2];
                        int[] titleCoordinates = new int[2];
                        mToolbar.getLocationOnScreen(toolbarCoordinates);
                        mTitle.getLocationOnScreen(titleCoordinates);
                        int toolbarY = toolbarCoordinates[1];
                        int titleY = titleCoordinates[1];

                        if (toolbarY > titleY) {
                            adjustForSmallerHeader(descriptor.getColor());
                        }

                        if (toolbarY < titleY) {
                            adjustForLargerHeader();
                        }
                    }
                });
    }

    private void adjustForLargerHeader() {
        mTitle.setVisibility(View.VISIBLE);
        mSubtitle.setVisibility(View.VISIBLE);
        showView(mToolbarIcon, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                hideView(mIcon, null);
                mToolbar.setBackgroundColor(ContextCompat.getColor(
                        getContext(),
                        R.color.transparent
                ));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void adjustForSmallerHeader(final int color) {
        mTitle.setVisibility(View.INVISIBLE);
        mSubtitle.setVisibility(View.INVISIBLE);
        showView(mIcon, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                hideView(mToolbarIcon, null);
                mToolbar.setBackgroundColor(ContextCompat.getColor(getContext(), color));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void hideView(View view, @Nullable Animation.AnimationListener listener) {
        animateVisibility(view, View.VISIBLE, android.R.anim.fade_in, listener);
    }

    private void showView(View view, @Nullable Animation.AnimationListener listener) {
        animateVisibility(view, View.INVISIBLE, android.R.anim.fade_out, listener);
    }

    private void animateVisibility(
            final View view,
            final int visibility,
            final int animId,
            @Nullable Animation.AnimationListener listener
    ) {
        if (view.getVisibility() != visibility) {
            final Animation animation = AnimationUtils.loadAnimation(getActivity(), animId);
            if (listener != null) {
                animation.setAnimationListener(listener);
            }
            view.startAnimation(animation);
            view.setVisibility(visibility);
        }
    }

    private void forceRippleAnimation() {
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            Drawable background = mHeader.getBackground();
            if (background instanceof RippleDrawable) {
                int[] iconCoordinates = new int[2];
                mIcon.getLocationOnScreen(iconCoordinates);
                float hotspotX = mHeader.getWidth() / 2;
                float hotspotY = iconCoordinates[1] - getStatusBarHeight() +
                                 (mIcon.getHeight() / 2);

                final RippleDrawable ripple = (RippleDrawable) background;
                int[] state = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
                ripple.setState(state);
                ripple.setHotspot(hotspotX, hotspotY);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ripple.setState(new int[]{});
                    }
                }, 400);
            }
        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        final Resources resources = getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public final void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ServiceView lastViewAdded = null;

        if (shouldShowFancyAnimations()) {
            mListWrapper.setAlpha(0);
        }
        for (final Service service : mService.getChildServices()) {
            ServiceView serviceView = new ServiceView(getActivity());

            if (shouldShowFancyAnimations()) {
                //if this is lollipop or higher, we'll set it up for some fancy animations
                serviceView.setAlpha(0);
                serviceView.setScaleX(0);
                serviceView.setScaleY(0);
            }
            String serviceMachineName = service.getUniq().toUpperCase();
            if (ServiceDescriptor.hasValueOf(serviceMachineName)) {
                ServiceDescriptor serviceDescriptor = ServiceDescriptor.valueOf(serviceMachineName);
                serviceView.init(serviceDescriptor);
                serviceView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bus.post(new LogEvent.AddLogEvent(new HandybookDefaultLog.SubServicePageSubmittedLog(
                                service.getId())));
                        startBookingFlow(service.getId(), service.getUniq());
                    }
                });
                mList.addView(serviceView);
                lastViewAdded = serviceView;
            }
        }

        if (lastViewAdded != null) {
            // remove bottom border of last element
            final View container = lastViewAdded.findViewById(R.id.container);
            if (container != null) {
                container.setBackgroundResource(0);
            }
        }
    }

    private void animateListItems() {
        mListWrapper
                .animate()
                .alpha(1)
                .start();

        for (int i = 0; i < mList.getChildCount(); i++) {
            View view = mList.getChildAt(i);
            view.animate()
                .setStartDelay(i * ANIMATION_DELAY_MS)
                .setInterpolator(mInterpolator)
                .alpha(1)
                .scaleX(1)
                .scaleY(1);
        }
    }
}

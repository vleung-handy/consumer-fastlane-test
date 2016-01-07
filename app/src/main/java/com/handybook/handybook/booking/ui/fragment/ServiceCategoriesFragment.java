package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.PromoCode;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.ui.activity.ServicesActivity;
import com.handybook.handybook.booking.ui.view.ServiceCategoryView;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.module.notifications.feed.ui.activity.NotificationsActivity;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.activity.OnboardActivity;
import com.handybook.handybook.ui.descriptor.ServiceCategoryListDescriptor;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class ServiceCategoriesFragment extends BookingFlowFragment
{
    private static final String EXTRA_SERVICE_ID = "EXTRA_SERVICE_ID";
    private static final String EXTRA_PROMO_CODE = "EXTRA_PROMO_CODE";
    private static final String SHARED_ICON_ELEMENT_NAME = "icon";
    private List<Service> mServices = new ArrayList<>();
    private boolean mUsedCache;

    @Bind(R.id.category_layout)
    LinearLayout mCategoryLayout;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.coupon_layout)
    View mCouponLayout;
    @Bind(R.id.promo_img)
    ImageView mPromoImage;
    @Bind(R.id.promo_text)
    TextView mPromoText;

    public static ServiceCategoriesFragment newInstance(String serviceId, String promoCode)
    {
        ServiceCategoriesFragment fragment = new ServiceCategoriesFragment();
        final Bundle bundle = new Bundle();
        bundle.putString(EXTRA_SERVICE_ID, serviceId);
        bundle.putString(EXTRA_PROMO_CODE, promoCode);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ServiceCategoriesFragment newInstance()
    {
        return new ServiceCategoriesFragment();
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final SharedPreferences.Editor edit = prefs.edit();

        if (!prefs.getBoolean("APP_OPENED_PREV", false))
        {
            mixpanel.trackEventFirstTimeUse();
            edit.putBoolean("APP_OPENED_PREV", true);
            edit.apply();
        }

        if (!prefs.getBoolean("APP_ONBOARD_SHOWN", false))
        {
            final Intent intent = new Intent(getActivity(), OnboardActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_service_categories, container, false);
        ButterKnife.bind(this, view);

        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MenuDrawerActivity activity = (MenuDrawerActivity) getActivity();
                activity.getMenuDrawer().toggleMenu();
            }
        });

        mPromoImage.setColorFilter(
                getResources().getColor(R.color.handy_blue),
                PorterDuff.Mode.SRC_ATOP);

        return view;
    }

    @Override
    public final void onActivityCreated(final Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        allowCallbacks = true;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        loadServices();
    }

    /**
     * handles bundle arguments. currently only from deeplinks
     *
     * should be called after handleLoadServicesResponse() so that we have the list of services
     */
    private void handleBundleArguments()
    {
        final Bundle args = getArguments();
        if (args != null)
        {
            String promoCode = args.getString(EXTRA_PROMO_CODE);
            if (promoCode != null)
            {
                args.remove(EXTRA_PROMO_CODE); //only handle once
                bus.post(new HandyEvent.RequestPreBookingPromo(promoCode));
            }

            String extraServiceIdString = args.getString(EXTRA_SERVICE_ID);
            if (extraServiceIdString != null)
            {
                args.remove(EXTRA_SERVICE_ID); //only handle once
                try
                {
                    int extraServiceId = Integer.parseInt(extraServiceIdString);
                    if (mServices != null && extraServiceId >= 0)
                    {
                        for (Service service : mServices)
                        {
                            if (service.getId() == extraServiceId)
                            {
                                launchServiceActivity(service);
                                break;
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    //probably an integer parsing error
                    Crashlytics.logException(e);
                }
            }
        }
    }

    private void loadServices()
    {
        progressDialog.show();
        mUsedCache = false;
        bus.post(new HandyEvent.RequestServices());
    }

    @Subscribe
    public void onReceivePreBookingPromoSuccess(HandyEvent.ReceivePreBookingPromoSuccess event)
    {
        PromoCode promoCode = event.getPromoCode();
        showCouponAppliedNotificationIfNecessary(); //could have removed the promo code
        if (promoCode != null)
        {
            if (promoCode.getType() == PromoCode.Type.VOUCHER)
            {
                startBookingFlow(promoCode.getServiceId(), promoCode.getUniq(), promoCode);
            }
        }
    }

    @Subscribe
    public void onReceiveServicesSuccess(final HandyEvent.ReceiveServicesSuccess event)
    {
        handleLoadServicesResponse(event.getServices(), false);
    }

    @Subscribe
    public void onReceiveCachedServicesSuccess(final HandyEvent.ReceiveCachedServicesSuccess event)
    {
        handleLoadServicesResponse(event.getServices(), true);
    }

    private void handleLoadServicesResponse(List<Service> services, boolean usedCache)
    {
        if (!allowCallbacks)
        {
            return;
        }
        mUsedCache = usedCache;
        mServices = services;
        handleBundleArguments();
        displayServices();
        progressDialog.dismiss();
    }

    @Subscribe
    public void onReceiveServicesError(final HandyEvent.ReceiveServicesError event)
    {
        if (!allowCallbacks || mUsedCache)
        {
            return;
        }
        progressDialog.dismiss();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    private void showCouponAppliedNotificationIfNecessary()
    {
        final String coupon = bookingManager.getPromoTabCoupon();
        if (coupon != null)
        {
            final Spannable text = new SpannableString(String.format(getString(R.string.using_promo), coupon));

            final int index = text.toString().indexOf(coupon);
            text.setSpan(
                    new ForegroundColorSpan(
                            getResources().getColor(R.color.handy_blue)
                    ),
                    index,
                    index + coupon.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            mPromoText.setText(text, TextView.BufferType.SPANNABLE);
            mCouponLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            mCouponLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        showCouponAppliedNotificationIfNecessary();
    }

    /**
     * launches the activity to start a booking for the given service
     *
     * @param service
     */
    private void launchServiceActivity(@NonNull Service service)
    {
        if (service.getServices().size() > 0)
        {
            final Intent intent = new Intent(getActivity(), ServicesActivity.class);
            intent.putExtra(ServicesActivity.EXTRA_SERVICE, service);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                Bundle bundle = null;
                ImageView transitionImageView = getServiceTransitionImageView(service);
                if (transitionImageView != null)
                {
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            getActivity(), transitionImageView, SHARED_ICON_ELEMENT_NAME
                    );
                    bundle = options.toBundle();
                }
                getActivity().startActivity(intent, bundle);
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

    /**
     * @param service
     * @return the ImageView for the given service,
     * which is needed for making a transition
     * in ActivityOptionsCompat.makeSceneTransitionAnimation() in launchServiceActivity()
     */
    private ImageView getServiceTransitionImageView(Service service)
    {
        if(service != null && service.getUniq() != null)
        {
            String serviceMachineName = service.getUniq().toUpperCase();
            ServiceCategoryListDescriptor descriptor = ServiceCategoryListDescriptor.valueOf(serviceMachineName);
            if(descriptor != null)
            {
                ImageView imageView = new ImageView(this.getContext());
                imageView.setImageResource(descriptor.getIconDrawable());
            }
        }
        return null;
    }

    private void displayServices()
    {
        mCategoryLayout.removeAllViews();
        for (final Service service : mServices)
        {
            ServiceCategoryView serviceCategoryView = new ServiceCategoryView(getActivity());
            serviceCategoryView.init(service);
            serviceCategoryView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    launchServiceActivity(service);
                }
            });
            mCategoryLayout.addView(serviceCategoryView);
        }
    }

    @OnClick(R.id.ib_notification_feed)
    void onNotificationFeedButtonClicked()
    {
        Intent launchIntent = new Intent(getActivity(), NotificationsActivity.class);
        startActivity(launchIntent);
    }
}

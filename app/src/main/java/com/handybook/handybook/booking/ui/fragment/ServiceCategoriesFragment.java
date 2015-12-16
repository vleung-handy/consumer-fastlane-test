package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.activity.OnboardActivity;
import com.handybook.handybook.booking.ui.activity.ServicesActivity;
import com.handybook.handybook.booking.ui.view.ServiceCategoryView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class ServiceCategoriesFragment extends BookingFlowFragment
{
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
        loadServices();
    }

    @Override
    public void onStart()
    {
        super.onStart();
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

    private void displayServices()
    {
        mCategoryLayout.removeAllViews();
        for (final Service service : mServices)
        {
            final ServiceCategoryView categoryView = new ServiceCategoryView(getActivity());
            categoryView.init(service);
            categoryView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (service.getServices().size() > 0)
                    {
                        final Intent intent = new Intent(getActivity(), ServicesActivity.class);
                        intent.putExtra(ServicesActivity.EXTRA_SERVICE, service);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        {
                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    getActivity(), categoryView.getIcon(), SHARED_ICON_ELEMENT_NAME
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
            });
            mCategoryLayout.addView(categoryView);
        }
    }

    private void loadServices()
    {
        progressDialog.show();
        mUsedCache = false;
        dataManager.getServices(new DataManager.CacheResponse<List<Service>>()
        {
            @Override
            public void onResponse(final List<Service> response)
            {
                if (!allowCallbacks)
                {
                    return;
                }
                mUsedCache = true;
                mServices = response;
                displayServices();
                progressDialog.dismiss();
            }
        }, new DataManager.Callback<List<Service>>()
        {
            @Override
            public void onSuccess(final List<Service> response)
            {
                if (!allowCallbacks)
                {
                    return;
                }
                mServices = response;
                displayServices();
                progressDialog.dismiss();
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                if (!allowCallbacks || mUsedCache)
                {
                    return;
                }
                progressDialog.dismiss();
                dataManagerErrorHandler.handleError(getActivity(), error);
            }
        });
    }
}

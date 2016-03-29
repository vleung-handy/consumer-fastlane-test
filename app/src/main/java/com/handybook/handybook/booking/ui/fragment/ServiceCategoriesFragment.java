package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.PromoCode;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.ui.activity.PromosActivity;
import com.handybook.handybook.booking.ui.activity.ServicesActivity;
import com.handybook.handybook.booking.ui.view.ServiceCategoryView;
import com.handybook.handybook.core.User;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.activity.OnboardActivity;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class ServiceCategoriesFragment extends BookingFlowFragment
{
    private static final String EXTRA_SERVICE_ID = "EXTRA_SERVICE_ID";
    private static final String EXTRA_PROMO_CODE = "EXTRA_PROMO_CODE";
    private static final String SHARED_ICON_ELEMENT_NAME = "icon";
    private static final String TAG = ServiceCategoriesFragment.class.getName();

    private List<Service> mServices = new ArrayList<>();
    private boolean mUsedCache;
    /**
     * maps the service id to its icon image view as rendered.
     * using service id rather than service object as key in case the object references differ
     * <p>
     * used for the cool icon transition to ServicesActivity
     * <p>
     * we need this because the transition requires a
     * reference to the EXACT image view of the service icon
     * rendered in the service category views
     */
    private Map<Integer, ImageView> mServiceIconMap = new HashMap<>();

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.coupon_layout)
    View mCouponLayout;
    @Bind(R.id.promo_img)
    ImageView mPromoImage;
    @Bind(R.id.promo_text)
    TextView mPromoText;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    RecyclerViewAdapter mAdapter;

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

        final User user = userManager.getCurrentUser();
        if (!prefs.getBoolean("APP_ONBOARD_SHOWN", false) && user == null)
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

        final MenuDrawerActivity activity = (MenuDrawerActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        activity.setupHamburgerMenu(mToolbar);

        mPromoImage.setColorFilter(
                getResources().getColor(R.color.handy_blue),
                PorterDuff.Mode.SRC_ATOP);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

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
     * <p>
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
                bus.post(new BookingEvent.RequestPreBookingPromo(promoCode));
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
        bus.post(new BookingEvent.RequestServices());
    }

    @Subscribe
    public void onReceivePreBookingPromoSuccess(BookingEvent.ReceivePreBookingPromoSuccess event)
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
    public void onReceiveServicesSuccess(final BookingEvent.ReceiveServicesSuccess event)
    {
        handleLoadServicesResponse(event.getServices(), false);
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

        if (mAdapter == null)
        {
            Log.d(TAG, "recycler: new adapter");
            mAdapter = new RecyclerViewAdapter(mServices, new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    int itemPosition = mRecyclerView.getChildLayoutPosition(view);
                    Service service = mServices.get(itemPosition);
                    mServiceIconMap.put(service.getId(), ((ServiceCategoryView) view).getIcon());
                    launchServiceActivity(service);
                }
            });
            mRecyclerView.setAdapter(mAdapter);
        }
        else
        {
            Log.d(TAG, "recycler: clearing and add");
            mAdapter.clearAndAdd(mServices);
        }

        progressDialog.dismiss();
    }

    @Subscribe
    public void onReceiveServicesError(final BookingEvent.ReceiveServicesError event)
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

    @OnClick(R.id.coupon_layout)
    public void onCouponClick()
    {
        ((MenuDrawerActivity) getActivity()).navigateToActivity(PromosActivity.class);
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
                ImageView transitionImageView = mServiceIconMap.get(service.getId());
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

    static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        public RecyclerViewHolder(View itemView)
        {
            super(itemView);
        }
    }


    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder>
    {
        private final String TAG = RecyclerViewAdapter.class.getName();

        private List<Service> services;
        private View.OnClickListener mListener;

        public RecyclerViewAdapter(List<Service> itemList, View.OnClickListener listener)
        {
            services = itemList;
            mListener = listener;
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            ServiceCategoryView v = new ServiceCategoryView(getContext());
            v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            v.setOnClickListener(mListener);
            RecyclerViewHolder rcv = new RecyclerViewHolder(v);
            return rcv;
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position)
        {
            ((ServiceCategoryView) holder.itemView).init(services.get(position));
        }

        @Override
        public int getItemCount()
        {
            return services.size();
        }

        public void clearAndAdd(List<Service> s)
        {
            services.clear();
            services.addAll(s);
            notifyDataSetChanged();
        }
    }
}

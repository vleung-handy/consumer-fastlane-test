package com.handybook.handybook.booking.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.login.LoginManager;
import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.PromoCode;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.ui.activity.PromosActivity;
import com.handybook.handybook.booking.ui.activity.ServicesActivity;
import com.handybook.handybook.booking.ui.activity.ZipActivity;
import com.handybook.handybook.booking.ui.adapter.ServicesCategoryHomeAdapter;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.EnvironmentModifier;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.core.manager.SecurePreferencesManager;
import com.handybook.handybook.core.ui.activity.LoginActivity;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.core.ui.activity.SplashActivity;
import com.handybook.handybook.library.util.FragmentUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.HandybookDefaultLog;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public final class ServiceCategoriesHomeFragment extends BookingFlowFragment {

    private static final String EXTRA_SERVICE_ID = "EXTRA_SERVICE_ID";
    private static final String EXTRA_PROMO_CODE = "EXTRA_PROMO_CODE";
    private static final String SHARED_ICON_ELEMENT_NAME = "icon";
    public static final int REQUEST_CODE_ZIP = 1001;

    private List<Service> mServices;
    private boolean mUsedCache;

    @Bind(R.id.fragment_service_categories_home_toolbar)
    Toolbar mToolbar;
    @Bind(R.id.fragment_service_categories_home_services_list)
    GridView mGridView;
    @Bind(R.id.fragment_service_categories_home_change_zip_container)
    ViewGroup mChangeZipContainer;
    @Bind(R.id.fragment_service_categories_home_promo_container)
    ViewGroup mPromoContainer;
    @Bind(R.id.fragment_service_categories_home_promo_image)
    ImageView mPromoImage;
    @Bind(R.id.fragment_service_categories_home_promo_text)
    TextView mPromoText;
    @Bind(R.id.fragment_service_categories_home_not_in_zip)
    TextView mNotInZip;
    @Bind(R.id.fragment_service_categories_home_sign_in_text)
    TextView mSignInLink;
    @Bind(R.id.fragment_service_categories_home_env_button)
    TextView mEnvLink;

    @Inject
    UserManager mUserManager;
    @Inject
    SecurePreferencesManager mSecurePreferencesManager;
    @Inject
    EnvironmentModifier mEnvironmentModifier;

    private ServicesCategoryHomeAdapter mAdapter;

    public static ServiceCategoriesHomeFragment newInstance(String serviceId, String promoCode) {
        ServiceCategoriesHomeFragment fragment = new ServiceCategoriesHomeFragment();
        final Bundle bundle = new Bundle();
        bundle.putString(EXTRA_SERVICE_ID, serviceId);
        bundle.putString(EXTRA_PROMO_CODE, promoCode);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ServiceCategoriesHomeFragment newInstance() {
        return new ServiceCategoriesHomeFragment();
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mServices = new ArrayList<>();

        bus.post(new LogEvent.AddLogEvent(new HandybookDefaultLog.AllServicesPageShownLog()));

    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(R.layout.fragment_service_categories_home, container, false));
        ButterKnife.bind(this, view);

        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        mPromoImage.setColorFilter(
                ContextCompat.getColor(getContext(), R.color.handy_blue),
                PorterDuff.Mode.SRC_ATOP
        );

        refreshZipLabel();
        updateSigninButtonDisplay();
        // We will only enable environment modifier if this is a stage build
        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_STAGE)) {
            mEnvLink.setText(getString(
                    R.string.environment_name,
                    mEnvironmentModifier.getEnvironment()
            ));
            mEnvLink.setVisibility(View.VISIBLE);
        }
        else {
            mEnvLink.setVisibility(View.GONE);
        }

        return view;
    }

    /**
     * If the user is already logged in, don't show the sign in button
     */
    private void updateSigninButtonDisplay() {
        mSignInLink.setVisibility(mUserManager.isUserLoggedIn() ? View.GONE : View.VISIBLE);
    }

    private void refreshZipLabel() {
        String zip = mDefaultPreferencesManager.getString(PrefsKey.ZIP);
        if (!TextUtils.isEmpty(zip)) {
            mNotInZip.setText(getString(R.string.not_in_zip, zip));
            mChangeZipContainer.setVisibility(View.VISIBLE);
        }
    }

    //only enabled when bottom nav enabled
    @OnClick(R.id.fragment_service_categories_home_sign_in_text)
    public void onSignInTextClicked() {
        final Intent intent = new Intent(getActivity(), LoginActivity.class);
        getActivity().startActivity(intent);
    }

    @OnClick(R.id.fragment_service_categories_home_change_button)
    public void onChangeButtonClicked() {
        startActivityForResult(
                new Intent(getActivity(), ZipActivity.class),
                REQUEST_CODE_ZIP
        );
    }

    @OnClick(R.id.fragment_service_categories_home_env_button)
    public void onEnvButtonClicked() {
        final EditText input = new EditText(getContext());
        input.setText(mEnvironmentModifier.getEnvironment());
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.set_environment)
                .setView(input)
                .setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // change the environment and update the menu text
                        mEnvironmentModifier.setEnvironment(input.getText().toString());
                        mEnvLink.setText(getString(
                                R.string.environment_name,
                                input.getText().toString()
                        ));
                        //Log user out
                        mConfigurationManager.invalidateCache();
                        mUserManager.setCurrentUser(null);
                        //log out of Facebook also
                        LoginManager.getInstance().logOut();
                        Intent intent = new Intent(
                                getContext(),
                                SplashActivity.class
                        );
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    @Override
    public final void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        allowCallbacks = true;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Only request if we don't have it
        if (mServices == null || mServices.size() == 0 ||
            mConfigurationManager.getPersistentConfiguration().isSaveZipCodeEnabled()) {
            loadServices();
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ZIP && resultCode == RESULT_OK) {
            //this is guaranteed to have at least one
            List<Service> services
                    = (List<Service>) data.getSerializableExtra(ZipActivity.EXTRA_SERVICES);

            handleLoadServicesResponse(services, mUsedCache);

            refreshZipLabel();
        }
    }

    /**
     * handles bundle arguments. currently only from deeplinks
     * <p/>
     * should be called after handleLoadServicesResponse() so that we have the list of services
     */
    private void handleBundleArguments() {
        final Bundle args = getArguments();
        if (args != null) {
            String promoCode = args.getString(EXTRA_PROMO_CODE);
            if (promoCode != null) {
                args.remove(EXTRA_PROMO_CODE); //only handle once
                bus.post(new BookingEvent.RequestPreBookingPromo(promoCode));
            }

            String extraServiceIdString = args.getString(EXTRA_SERVICE_ID);
            if (extraServiceIdString != null) {
                args.remove(EXTRA_SERVICE_ID); //only handle once
                try {
                    int extraServiceId = Integer.parseInt(extraServiceIdString);
                    if (mServices != null && extraServiceId >= 0) {
                        for (Service service : mServices) {
                            if (service.getId() == extraServiceId) {
                                launchServiceActivity(service, null);
                                break;
                            }
                        }
                    }
                }
                catch (Exception e) {
                    //probably an integer parsing error
                    Crashlytics.logException(e);
                }
            }
        }
    }

    private void loadServices() {
        showProgressSpinner();
        mUsedCache = false;
        bus.post(new BookingEvent.RequestServices());
    }

    @Subscribe
    public void onReceivePreBookingPromoSuccess(BookingEvent.ReceivePreBookingPromoSuccess event) {
        PromoCode promoCode = event.getPromoCode();
        showCouponAppliedNotificationIfNecessary(); //could have removed the promo code
        if (promoCode != null) {
            if (promoCode.getType() == PromoCode.Type.VOUCHER) {
                startBookingFlow(promoCode.getServiceId(), promoCode.getUniq(), promoCode);
            }
        }
    }

    @Subscribe
    public void onReceiveServicesSuccess(final BookingEvent.ReceiveServicesSuccess event) {
        handleLoadServicesResponse(event.getServices(), false);
    }

    private void handleLoadServicesResponse(List<Service> services, boolean usedCache) {
        if (!allowCallbacks) {
            return;
        }

        mUsedCache = usedCache;
        mServices = services;
        handleBundleArguments();

        if (mAdapter == null) {
            mAdapter = new ServicesCategoryHomeAdapter(getContext(), mServices);
            mGridView.setAdapter(mAdapter);
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(
                        final AdapterView<?> parent,
                        final View view,
                        final int position,
                        final long id
                ) {
                    //Get the service and launch it
                    launchServiceActivity(mAdapter.getItem(position), view);
                }
            });
        }
        else {
            mAdapter.refreshData(mServices);
        }

        hideProgressSpinner();
    }

    @Subscribe
    public void onReceiveServicesError(final BookingEvent.ReceiveServicesError event) {
        if (!allowCallbacks || mUsedCache) {
            return;
        }
        hideProgressSpinner();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    private void showCouponAppliedNotificationIfNecessary() {
        //TODO currently not showing anything for hidden coupons; confirm with PM this behavior is OK
        final String coupon = bookingManager.getPromoTabCoupon();
        if (coupon != null) {
            final Spannable text =
                    new SpannableString(String.format(getString(R.string.using_promo), coupon));

            final int index = text.toString().indexOf(coupon);
            text.setSpan(
                    new ForegroundColorSpan(
                            ContextCompat.getColor(getContext(), R.color.handy_blue)
                    ),
                    index,
                    index + coupon.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            mPromoText.setText(text, TextView.BufferType.SPANNABLE);
            mPromoContainer.setVisibility(View.VISIBLE);
        }
        else {
            mPromoContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        showCouponAppliedNotificationIfNecessary();
    }

    @OnClick(R.id.fragment_service_categories_home_promo_container)
    public void onCouponClick() {
        if (getActivity() instanceof MenuDrawerActivity) {
            ((MenuDrawerActivity) getActivity()).navigateToActivity(PromosActivity.class, null);
        }
        else {
            FragmentUtils.switchToFragment(this, PromosFragment.newInstance(null), true);
        }
    }

    /**
     * launches the activity to start a booking for the given service
     *
     * @param service
     */
    private void launchServiceActivity(@NonNull Service service, @Nullable View view) {
        if (service.getChildServices().size() > 0) {
            final Intent intent = new Intent(getActivity(), ServicesActivity.class);
            intent.putExtra(ServicesActivity.EXTRA_SERVICE, service);
            Bundle bundle = null;
            //This is just for animating the icon from home screen to sub-categories screen
            if (view != null) {
                ImageView transitionImageView
                        = ((ServicesCategoryHomeAdapter.CategoryViewHolder) view
                        .getTag()).getIcon();

                ActivityOptionsCompat options
                        = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(), transitionImageView, SHARED_ICON_ELEMENT_NAME
                );
                bundle = options.toBundle();
            }
            startActivity(intent, bundle);
        }
        else {
            startBookingFlow(service.getId(), service.getUniq());
        }
    }
}

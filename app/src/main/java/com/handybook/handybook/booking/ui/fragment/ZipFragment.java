package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.ui.activity.ZipActivity;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.core.manager.ServicesManager;
import com.handybook.handybook.core.ui.widget.ZipCodeInputTextView;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.OnboardingLog;
import com.handybook.handybook.onboarding.ServiceNotSupportedActivity;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Takes input for a ZIP. The zip will be sent to the server for supported services.
 * If there are supported services, we return the services back to the home page. Otherwise,
 * we redirect to {@link com.handybook.handybook.onboarding.ServiceNotSupportedActivity},
 * to allow the user to change zip. This should only be used in conjuction with the
 * new onboarding flow being turned on.
 */
public class ZipFragment extends InjectedFragment {

    @BindView(R.id.zip_text)
    ZipCodeInputTextView mZipCodeInputTextView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Inject
    protected ServicesManager mServicesManager;

    private List<Service> mServices;

    public static ZipFragment newInstance() {
        return new ZipFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_zip, container, false);
        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.zip_code));

        mZipCodeInputTextView.setText(mDefaultPreferencesManager.getString(PrefsKey.ZIP));

        return view;
    }

    @OnClick(R.id.zip_submit_button)
    public void submitClicked() {
        if (mZipCodeInputTextView.validate()) {
            mServicesManager.requestServices(mZipCodeInputTextView.getZipCode(), false);
        }
    }

    @Subscribe
    public void onReceiveServicesSuccess(final BookingEvent.ReceiveServicesSuccess event) {
        mServices = event.getServices();
        bus.post(new LogEvent.AddLogEvent(new OnboardingLog.ZipSubmittedLog(
                event.getZip(), Locale.getDefault().toString()
        )));

        if (mServices != null && !mServices.isEmpty()) {
            //return back to home page
            mDefaultPreferencesManager.setString(PrefsKey.ZIP, event.getZip());
            ((ZipActivity) getActivity()).finishWithResults(mServices);
        }
        else {
            //there are no services, so we redirect to the service not supported page
            Intent intent = new Intent(getActivity(), ServiceNotSupportedActivity.class);
            intent.putExtra(BundleKeys.ZIP, event.getZip());
            intent.putExtra(ServiceNotSupportedActivity.EXTRA_FROM_ZIP, true);
            startActivity(intent);
        }
    }

    @Subscribe
    public void onReceiveServicesError(final BookingEvent.ReceiveServicesError error) {
        dataManagerErrorHandler.handleError(getActivity(), error.error);
    }
}

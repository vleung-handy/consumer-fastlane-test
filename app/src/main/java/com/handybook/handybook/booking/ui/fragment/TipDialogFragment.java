package com.handybook.handybook.booking.ui.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.LocalizedMonetaryAmount;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.library.ui.fragment.BaseDialogFragment;
import com.handybook.handybook.library.ui.view.HandySnackbar;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TipDialogFragment extends BaseDialogFragment {

    public static final String TAG = TipDialogFragment.class.getSimpleName();

    static final String EXTRA_PRO_NAME = "com.handy.handy.EXTRA_PRO_NAME";
    static final String EXTRA_BOOKING_ID = "com.handy.handy.EXTRA_BOOKING_ID";

    @Inject
    UserManager mUserManager;

    @BindView(R.id.title_text)
    TextView mTitleText;
    @BindView(R.id.tip_notice)
    View mTipNotice;

    private String mProName;

    public static TipDialogFragment newInstance(int bookingId, String proName) {
        TipDialogFragment tipDialogFragment = new TipDialogFragment();
        tipDialogFragment.canDismiss = true;

        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_PRO_NAME, proName);
        bundle.putInt(EXTRA_BOOKING_ID, bookingId);
        tipDialogFragment.setArguments(bundle);

        return tipDialogFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    public void onStop() {
        mBus.unregister(this);
        super.onStop();
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.dialog_tip, container, true);
        ButterKnife.bind(this, view);

        mProName = getArguments().getString(EXTRA_PRO_NAME);
        mTitleText.setText(getString(R.string.leave_tip_prompt_formatted, mProName));

        final User currentUser = mUserManager.getCurrentUser();
        final ArrayList<LocalizedMonetaryAmount> defaultTipAmounts
                = currentUser.getDefaultTipAmounts();
        final TipFragment tipFragment = TipFragment.newInstance(
                defaultTipAmounts,
                currentUser.getCurrencyChar()
        );

        getChildFragmentManager().beginTransaction()
                                 .replace(R.id.tip_layout_container, tipFragment)
                                 .commit();

        final int screenOrientation = getResources().getConfiguration().orientation;
        boolean isPortrait = screenOrientation == Configuration.ORIENTATION_PORTRAIT;
        mTipNotice.setVisibility(isPortrait ? View.VISIBLE : View.GONE);

        return view;
    }

    @OnClick(R.id.tip_dialog_container)
    public void onTipDialogContainerClicked() {
        dismiss();
    }

    @OnClick(R.id.submit_button)
    public void onSubmitButtonClicked(View view) {
        view.setClickable(false);
        final int bookingId = getArguments().getInt(EXTRA_BOOKING_ID);
        final Integer tipAmount = getTipAmount();
        if (tipAmount != null && tipAmount > 0) {
            mBus.post(new BookingEvent.RequestTipPro(bookingId, tipAmount));
        }
        else {
            dismiss();
        }
    }

    @Subscribe
    public void onReceiveTipProSuccess(BookingEvent.ReceiveTipProSuccess event) {
        final String message = getString(R.string.tip_success_message_formatted, mProName);
        HandySnackbar.show(getActivity(), message, HandySnackbar.TYPE_SUCCESS);
        dismiss();
    }

    @Subscribe
    public void onReceiveTipProError(BookingEvent.ReceiveTipProError event) {
        showToast(R.string.an_error_has_occurred);
        dismiss();
    }

    private Integer getTipAmount() {
        final TipFragment tipFragment = (TipFragment) getChildFragmentManager()
                .findFragmentById(R.id.tip_layout_container);
        if (tipFragment != null) {
            return tipFragment.getTipAmount();
        }
        else {
            Crashlytics.logException(new RuntimeException("Tip fragment not found"));
            return null;
        }
    }
}

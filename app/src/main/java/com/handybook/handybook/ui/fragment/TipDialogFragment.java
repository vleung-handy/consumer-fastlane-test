package com.handybook.handybook.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.core.LocalizedMonetaryAmount;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.event.HandyEvent;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TipDialogFragment extends BaseDialogFragment
{
    static final String EXTRA_PRO_NAME = "com.handy.handy.EXTRA_PRO_NAME";
    static final String EXTRA_BOOKING_ID = "com.handy.handy.EXTRA_BOOKING_ID";

    @Inject
    UserManager mUserManager;

    @Bind(R.id.title_text)
    TextView mTitleText;

    public static TipDialogFragment newInstance(int bookingId, String proName)
    {
        TipDialogFragment tipDialogFragment = new TipDialogFragment();
        tipDialogFragment.canDismiss = true;

        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_PRO_NAME, proName);
        bundle.putInt(EXTRA_BOOKING_ID, bookingId);
        tipDialogFragment.setArguments(bundle);

        return tipDialogFragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.dialog_tip, container, true);
        ButterKnife.bind(this, view);

        String proName = getArguments().getString(EXTRA_PRO_NAME);
        mTitleText.setText(getString(R.string.leave_tip_prompt, proName));

        final User currentUser = mUserManager.getCurrentUser();
        final ArrayList<LocalizedMonetaryAmount> defaultTipAmounts = currentUser.getDefaultTipAmounts();
        final TipFragment tipFragment = TipFragment.newInstance(defaultTipAmounts);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.tip_layout_container, tipFragment)
                .commit();

        return view;
    }

    @OnClick(R.id.submit_button)
    public void onSubmitButtonClicked(View view)
    {
        view.setClickable(false);
        final int bookingId = getArguments().getInt(EXTRA_BOOKING_ID);
        final Integer tipAmount = getTipAmount();
        if (tipAmount != null && tipAmount > 0)
        {
            mBus.post(new HandyEvent.RequestTipPro(bookingId, tipAmount));
        }
        else
        {
            dismiss();
        }
    }

    @Subscribe
    public void onReceiveTipProSuccess(HandyEvent.ReceiveTipProSuccess event)
    {
        showToast(R.string.thanks_for_leaving_a_tip);
        dismiss();
    }

    @Subscribe
    public void onReceiveTipProError(HandyEvent.ReceiveTipProError event)
    {
        showToast(R.string.an_error_has_occurred);
        dismiss();
    }

    private Integer getTipAmount()
    {
        final TipFragment tipFragment = (TipFragment) getChildFragmentManager()
                .findFragmentById(R.id.tip_layout_container);
        if (tipFragment != null)
        {
            return tipFragment.getTipAmount();
        }
        else
        {
            Crashlytics.logException(new RuntimeException("Tip fragment not found"));
            return null;
        }
    }
}

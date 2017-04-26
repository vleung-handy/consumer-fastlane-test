package com.handybook.handybook.booking.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.android.gms.wallet.WalletConstants;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.fragment.BookingPaymentFragment;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;

public final class BookingPaymentActivity extends MenuDrawerActivity {

    @Override
    protected final Fragment createFragment() {
        Fragment fragment = BookingPaymentFragment.newInstance();
        fragment.setArguments(getIntent().getExtras());
        return fragment;
    }

    @Override
    protected final String getNavItemTitle() {
        return null;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disableDrawer = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        BookingPaymentFragment bookingPaymentFragment =
                (BookingPaymentFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (bookingPaymentFragment != null) {
            int errorCode = -1;
            if (data != null) {
                errorCode = data.getIntExtra(WalletConstants.EXTRA_ERROR_CODE, -1);
            }
            switch (requestCode) {
                case ActivityResult.LOAD_MASKED_WALLET:
                    bookingPaymentFragment.handleLoadMaskedWalletResult(
                            resultCode,
                            data,
                            errorCode
                    );
                    break;
                case ActivityResult.LOAD_FULL_WALLET:
                    bookingPaymentFragment.handleLoadFullWalletResult(resultCode, data, errorCode);
                    break;
            }
        }
    }
}

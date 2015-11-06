package com.handybook.handybook.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.android.gms.wallet.WalletConstants;
import com.handybook.handybook.ui.fragment.BookingPaymentFragment;
import com.handybook.handybook.util.WalletUtils;

public final class BookingPaymentActivity extends MenuDrawerActivity
{

    private BookingPaymentFragment mBookingPaymentFragment;

    @Override
    protected final Fragment createFragment()
    {
        mBookingPaymentFragment = BookingPaymentFragment.newInstance();
        return mBookingPaymentFragment;
    }

    @Override
    protected final String getNavItemTitle()
    {
        return null;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        disableDrawer = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (mBookingPaymentFragment != null)
        {
            int errorCode = -1;
            if (data != null)
            {
                errorCode = data.getIntExtra(WalletConstants.EXTRA_ERROR_CODE, -1);
            }
            switch (requestCode)
            {
                case WalletUtils.REQUEST_CODE_LOAD_MASKED_WALLET:
                    mBookingPaymentFragment.handleLoadMaskedWalletResult(resultCode, data, errorCode);
                    break;
                case WalletUtils.REQUEST_CODE_LOAD_FULL_WALLET:
                    mBookingPaymentFragment.handleLoadFullWalletResult(resultCode, data, errorCode);
                    break;
            }
        }
    }
}

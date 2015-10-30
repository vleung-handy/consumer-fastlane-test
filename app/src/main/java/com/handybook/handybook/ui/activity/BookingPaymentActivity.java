package com.handybook.handybook.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.WalletConstants;
import com.handybook.handybook.R;
import com.handybook.handybook.ui.fragment.BookingPaymentFragment;

public final class BookingPaymentActivity extends MenuDrawerActivity {

    private BookingPaymentFragment mBookingPaymentFragment;

    @Override
    protected final Fragment createFragment() {
        mBookingPaymentFragment = BookingPaymentFragment.newInstance();
        return mBookingPaymentFragment;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // retrieve the error code, if available
        int errorCode = -1;
        if (data != null)
        {
            errorCode = data.getIntExtra(WalletConstants.EXTRA_ERROR_CODE, -1);
        }
        switch (requestCode)
        {
            case BookingPaymentFragment.REQUEST_CODE_MASKED_WALLET:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        MaskedWallet maskedWallet =
                                data.getParcelableExtra(WalletConstants.EXTRA_MASKED_WALLET);
                        handleSuccess(maskedWallet);
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                    default:
                        handleError(errorCode);
                        break;
                }
                break;
            case WalletConstants.RESULT_ERROR:
                handleError(errorCode);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void handleError(int errorCode)
    {
        switch (errorCode)
        {
            case WalletConstants.ERROR_CODE_SPENDING_LIMIT_EXCEEDED:
                Toast.makeText(this, R.string.spending_limit_exceeded,
                        Toast.LENGTH_LONG).show();
                break;
            case WalletConstants.ERROR_CODE_INVALID_PARAMETERS:
            case WalletConstants.ERROR_CODE_AUTHENTICATION_FAILURE:
            case WalletConstants.ERROR_CODE_BUYER_ACCOUNT_ERROR:
            case WalletConstants.ERROR_CODE_MERCHANT_ACCOUNT_ERROR:
            case WalletConstants.ERROR_CODE_SERVICE_UNAVAILABLE:
            case WalletConstants.ERROR_CODE_UNSUPPORTED_API_VERSION:
            case WalletConstants.ERROR_CODE_UNKNOWN:
            default:
                // unrecoverable error
                String errorMessage = getString(R.string.android_pay_unavailable) + "\n" +
                        getString(R.string.error_code, errorCode);
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void handleSuccess(MaskedWallet maskedWallet)
    {
        mBookingPaymentFragment.showMaskedWalletInfo(maskedWallet);
    }
}

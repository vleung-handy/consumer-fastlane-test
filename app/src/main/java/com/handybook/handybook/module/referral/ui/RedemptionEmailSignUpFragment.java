package com.handybook.handybook.module.referral.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.ui.fragment.InjectedFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class RedemptionEmailSignUpFragment extends InjectedFragment
{
    private static final String KEY_REFERRAL_GUID = "referral_guid";

    private String mReferralGuid;

    public static RedemptionEmailSignUpFragment newInstance(final String referralGuid)
    {
        final RedemptionEmailSignUpFragment fragment = new RedemptionEmailSignUpFragment();
        final Bundle arguments = new Bundle();
        arguments.putString(KEY_REFERRAL_GUID, referralGuid);
        fragment.setArguments(arguments);
        return fragment;
    }

    @OnClick(R.id.back_button)
    public void onBackButtonClicked()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mReferralGuid = getArguments().getString(KEY_REFERRAL_GUID);
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view =
                inflater.inflate(R.layout.fragment_redemption_email_sign_up, container, false);

        ButterKnife.bind(this, view);

        return view;
    }
}

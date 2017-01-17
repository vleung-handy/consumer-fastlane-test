package com.handybook.handybook.onboarding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;

import butterknife.ButterKnife;

/**
 * This is the new onboarding fragment that is supposed to
 */
public class OnboardV2Fragment extends InjectedFragment
{
    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    )
    {
        View view = inflater.inflate(R.layout.fragment_onboard_v2, container, false);

        ButterKnife.bind(this, view);
        return view;
    }
}

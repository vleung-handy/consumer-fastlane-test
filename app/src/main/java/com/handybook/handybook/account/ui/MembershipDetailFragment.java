package com.handybook.handybook.account.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.account.model.Membership;
import com.handybook.handybook.library.ui.fragment.ProgressSpinnerFragment;

public class MembershipDetailFragment extends ProgressSpinnerFragment {

    public static final String TAG = MembershipDetailFragment.class.getSimpleName();
    private static final String KEY_MEMBERSHIP = "key:membership";
    private static Membership mMembership;

    public static MembershipDetailFragment newInstance(final Membership membership) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_MEMBERSHIP, membership);
        MembershipDetailFragment fragment = new MembershipDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMembership = (Membership) getArguments().getSerializable(KEY_MEMBERSHIP);
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(R.layout.membership_detail_fragment, container, false));

/*
        mBinding = .inflate(inflater, container, false);

        final View view = mBinding.getRoot();
        mBinding.setFragment(this);
        mBinding.setGame(mVegasGame);
*/

        return view;
    }
}

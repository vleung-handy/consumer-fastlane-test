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

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TipDialogFragment extends BaseDialogFragment
{
    static final String EXTRA_PRO_NAME = "com.handy.handy.EXTRA_PRO_NAME";

    @Inject
    UserManager mUserManager;

    @Bind(R.id.title_text)
    TextView mTitleText;

    public static TipDialogFragment newInstance(String proName)
    {
        TipDialogFragment tipDialogFragment = new TipDialogFragment();
        tipDialogFragment.canDismiss = true;

        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_PRO_NAME, proName);
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
    public void onSubmitButtonClicked()
    {

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

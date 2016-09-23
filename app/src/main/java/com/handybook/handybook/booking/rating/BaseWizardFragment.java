package com.handybook.handybook.booking.rating;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;

/**
 *
 * This is the base fragment for a fragment that wants to be part of a wizard. It provides
 * a wizard call back, so that it can notify its parent that the current fragment is done and
 * ready to move on.
 *
 * Created by jtse on 3/30/16.
 */
public abstract class BaseWizardFragment extends InjectedFragment implements Serializable
{
    protected WizardCallback mCallback;

    @Bind(R.id.tv_rating_title)
    TextView mTvTitle;

    @Bind(R.id.tv_rating_anonymous)
    TextView mTvAnonymous;

    @Bind(R.id.tv_rating_all_apply)
    TextView mTvAllApply;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ((BaseApplication) getActivity().getApplication()).inject(this);
    }

    @Override
    public void onAttach(final Context context)
    {
        super.onAttach(context);
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof WizardCallback)
        {
            mCallback = (WizardCallback) parentFragment;
        }
    }

    abstract HashMap<String, ArrayList<String>> getSelectedItemsMap();
}

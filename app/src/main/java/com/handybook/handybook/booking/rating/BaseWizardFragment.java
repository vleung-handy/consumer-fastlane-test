package com.handybook.handybook.booking.rating;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.BaseApplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;

/**
 * Created by jtse on 3/30/16.
 */
public abstract class BaseWizardFragment extends Fragment implements Serializable
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

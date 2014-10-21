package com.handybook.handybook;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

import butterknife.ButterKnife;

public class InjectedListFragment extends ListFragment {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseApplication)getActivity().getApplication()).inject(this);
    }

    @Override
    public final void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}

package com.handybook.handybook;

import android.os.Bundle;

import butterknife.ButterKnife;

public class InjectedFragment extends android.support.v4.app.Fragment {

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

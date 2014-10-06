package com.handybook.handybook;

import android.app.Fragment;
import android.os.Bundle;

public class InjectedFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseApplication)getActivity().getApplication()).inject(this);
    }
}

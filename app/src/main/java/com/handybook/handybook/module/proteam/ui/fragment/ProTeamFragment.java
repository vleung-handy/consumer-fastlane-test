package com.handybook.handybook.module.proteam.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.ui.fragment.InjectedFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProTeamFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProTeamFragment extends InjectedFragment
{

    public ProTeamFragment()
    {
        // Required empty public constructor
    }

    public static ProTeamFragment newInstance()
    {
        ProTeamFragment fragment = new ProTeamFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    )
    {
        return inflater.inflate(R.layout.fragment_pro_team, container, false);
    }

}

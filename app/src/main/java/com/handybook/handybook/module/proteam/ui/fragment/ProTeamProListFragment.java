package com.handybook.handybook.module.proteam.ui.fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.module.proteam.model.ProTeam;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.fragment.InjectedFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProTeamProListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProTeamProListFragment extends InjectedFragment
{
    private static final String KEY_PROTEAM_PROTEAM = "ProTeam:ProTeam";
    private static final String KEY_PROTEAM_CATEGORY_TYPE = "ProTeam:CategoryType";

    @Bind(R.id.pro_team_toolbar)
    Toolbar mToolbar;
    @Bind(R.id.pro_team_tab_layout)
    TabLayout mTabLayout;
    @Bind(R.id.pro_team_add_pros)
    FloatingActionButton mFab;

    private ProTeam mProteam;
    private
    @ProTeam.ProTeamCategory.ProTeamCategoryType
    String mProTeamCategoryType;


    public ProTeamProListFragment()
    {
        // Required empty public constructor
    }

    public static ProTeamProListFragment newInstance(
            ProTeam proTeam,
            @ProTeam.ProTeamCategory.ProTeamCategoryType String proTeamCategoryType
    )
    {
        ProTeamProListFragment fragment = new ProTeamProListFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_PROTEAM_PROTEAM, proTeam);
        bundle.putString(KEY_PROTEAM_CATEGORY_TYPE, proTeamCategoryType);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void initialize(final Bundle arguments)
    {
        mProteam = arguments.getParcelable(KEY_PROTEAM_PROTEAM);
        final String tempProTeamCategoryType = arguments.getString(KEY_PROTEAM_CATEGORY_TYPE);
        if (tempProTeamCategoryType != null)
        {
            switch (tempProTeamCategoryType)
            {
                case ProTeam.ProTeamCategory.HANDYMEN:
                    mProTeamCategoryType = ProTeam.ProTeamCategory.HANDYMEN;
                    break;
                case ProTeam.ProTeamCategory.CLEANING:
                    mProTeamCategoryType = ProTeam.ProTeamCategory.CLEANING;
                    break;
            }
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    )
    {
        final View view = inflater.inflate(R.layout.fragment_pro_team_pro_list, container, false);
        ButterKnife.bind(this, view);
        final MenuDrawerActivity activity = (MenuDrawerActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.setupHamburgerMenu(mToolbar);

        final Bundle arguments = getArguments();
        if (arguments != null)
        {
            initialize(arguments);
        }
        return view;
    }


}

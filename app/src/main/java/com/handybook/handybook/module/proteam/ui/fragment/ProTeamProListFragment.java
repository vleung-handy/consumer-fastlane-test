package com.handybook.handybook.module.proteam.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.module.proteam.adapter.ProTeamCategoryAdapter;
import com.handybook.handybook.module.proteam.model.ProTeam;
import com.handybook.handybook.module.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.view.EmptiableRecyclerView;

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

    @Bind(R.id.pro_team_pro_list_recycler_view)
    EmptiableRecyclerView mRecyclerView;
    @Bind(R.id.pro_team_empty_view)
    View mEmptyView;
    @Bind(R.id.pro_team_empty_view_title)
    TextView mEmptyViewTitle;
    @Bind(R.id.pro_team_empty_view_text)
    TextView mEmptyViewText;

    private ProTeam mProteam;
    private ProTeamCategoryType mProTeamCategoryType;
    private RecyclerView.Adapter mProCardCardAdapter;


    public ProTeamProListFragment()
    {
        // Required empty public constructor
    }

    public static ProTeamProListFragment newInstance(
            ProTeam proTeam,
            ProTeamCategoryType proTeamCategoryType
    )
    {
        ProTeamProListFragment fragment = new ProTeamProListFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_PROTEAM_PROTEAM, proTeam);
        bundle.putParcelable(KEY_PROTEAM_CATEGORY_TYPE, proTeamCategoryType);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void initialize(final Bundle arguments)
    {
        mProteam = arguments.getParcelable(KEY_PROTEAM_PROTEAM);
        mProTeamCategoryType = arguments.getParcelable(KEY_PROTEAM_CATEGORY_TYPE);
        initEmptyView();
        initRecyclerView();
    }

    private void initEmptyView()
    {
        if (mProteam.hasAvailableProsInCategory(mProTeamCategoryType))
        {
            mEmptyViewTitle.setText(R.string.pro_team_empty_card_title_has_available);
            mEmptyViewText.setText(R.string.pro_team_empty_card_text_has_available);
        }
        else
        {
            mEmptyViewTitle.setText(R.string.pro_team_empty_card_title_no_available);
            mEmptyViewText.setText(R.string.pro_team_empty_card_text_no_available);
        }
    }

    private void initRecyclerView()
    {
        mProCardCardAdapter = new ProTeamCategoryAdapter(
                getContext(),
                mProteam.getCategory(mProTeamCategoryType).getPreferred()
        );
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mProCardCardAdapter);
        mRecyclerView.setEmptyView(mEmptyView);
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
        final Bundle arguments = getArguments();
        if (arguments != null)
        {
            initialize(arguments);
        }
        return view;
    }


}

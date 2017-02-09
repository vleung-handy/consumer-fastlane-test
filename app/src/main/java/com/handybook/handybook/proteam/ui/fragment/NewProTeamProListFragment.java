package com.handybook.handybook.proteam.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.ui.view.EmptiableRecyclerView;
import com.handybook.handybook.proteam.adapter.NewProTeamCategoryAdapter;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.proteam.model.ProTeamPro;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewProTeamProListFragment extends InjectedFragment
{
    @Bind(R.id.edit_my_pros_list_recycler_view)
    EmptiableRecyclerView mRecyclerView;
    @Bind(R.id.pro_team_empty_view)
    View mEmptyView;
    @Bind(R.id.pro_team_empty_view_title)
    TextView mEmptyViewTitle;
    @Bind(R.id.pro_team_empty_view_text)
    TextView mEmptyViewText;

    private ProTeam.ProTeamCategory mProTeamCategory;
    private ProTeamCategoryType mProTeamCategoryType;
    private NewProTeamCategoryAdapter.ActionCallbacks mProTeamActionCallbacks =
            new NewProTeamCategoryAdapter.ActionCallbacks()
            {
                @Override
                public void onHeartClick(final ProTeamPro proTeamPro)
                {
                    // TODO: Implement
                }

                @Override
                public void onLongClick(final ProTeamPro proTeamPro)
                {
                    // TODO: Implement
                }
            };

    public static NewProTeamProListFragment newInstance(
            @NonNull final ProTeam.ProTeamCategory proTeamCategory,
            @NonNull final ProTeamCategoryType proTeamCategoryType
    )
    {
        final NewProTeamProListFragment fragment = new NewProTeamProListFragment();
        final Bundle arguments = new Bundle();
        arguments.putParcelable(BundleKeys.PRO_TEAM_CATEGORY, proTeamCategory);
        arguments.putSerializable(BundleKeys.PRO_TEAM_CATEGORY_TYPE, proTeamCategoryType);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mProTeamCategory = getArguments().getParcelable(BundleKeys.PRO_TEAM_CATEGORY);
        mProTeamCategoryType = (ProTeamCategoryType) getArguments().getSerializable(
                BundleKeys.PRO_TEAM_CATEGORY_TYPE);
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    )
    {
        final View view = inflater.inflate(
                R.layout.fragment_new_pro_team_pro_list,
                container,
                false
        );
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setEmptyView(mEmptyView);
        mEmptyViewTitle.setText(R.string.pro_team_empty_card_title);
        mEmptyViewText.setText(R.string.pro_team_empty_card_text);
        mRecyclerView.setAdapter(new NewProTeamCategoryAdapter(
                getActivity(),
                mProTeamCategory,
                mProTeamActionCallbacks
        ));
    }
}

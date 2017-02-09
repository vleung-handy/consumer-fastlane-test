package com.handybook.handybook.proteam.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.handybook.handybook.R;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.ui.view.EmptiableRecyclerView;
import com.handybook.handybook.library.util.FragmentUtils;
import com.handybook.handybook.proteam.adapter.NewProTeamCategoryAdapter;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.proteam.model.ProTeamPro;
import com.handybook.handybook.proteam.viewmodel.ProTeamActionPickerViewModel;
import com.handybook.handybook.proteam.viewmodel.ProTeamActionPickerViewModel.ActionType;

import java.util.ArrayList;
import java.util.List;

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
                    String title;
                    String subtitle = null;
                    List<ActionType> actionTypes = new ArrayList<>();
                    if (proTeamPro.isFavorite())
                    {
                        title = getString(
                                R.string.remove_as_favorite_formatted,
                                proTeamPro.getName()
                        );
                        actionTypes.add(ActionType.REMOVE);
                        actionTypes.add(ActionType.BLOCK);
                    }
                    else
                    {
                        title = getString(R.string.set_as_favorite_formatted, proTeamPro.getName());
                        final ProTeamPro favoritePro = mProTeamCategory.getFavoritePro();
                        if (favoritePro != null)
                        {
                            subtitle = getString(
                                    R.string.auto_remove_as_favorite_warning_formatted,
                                    favoritePro.getName()
                            );
                        }
                        actionTypes.add(ActionType.FAVORITE);
                    }

                    final ProTeamActionPickerViewModel viewModel = new ProTeamActionPickerViewModel(
                            proTeamPro.getImageUrl(),
                            title,
                            subtitle,
                            actionTypes
                    );
                    final ProTeamActionPickerDialogFragment dialogFragment =
                            ProTeamActionPickerDialogFragment.newInstance(viewModel);
                    FragmentUtils.safeLaunchDialogFragment(dialogFragment, getActivity(), null);
                }

                @Override
                public void onLongClick(final ProTeamPro proTeamPro)
                {

                    final ProTeamActionPickerViewModel viewModel = new ProTeamActionPickerViewModel(
                            proTeamPro.getImageUrl(),
                            getString(R.string.block_formatted, proTeamPro.getName()),
                            null,
                            Lists.newArrayList(ActionType.BLOCK)
                    );
                    final ProTeamActionPickerDialogFragment dialogFragment =
                            ProTeamActionPickerDialogFragment.newInstance(viewModel);
                    FragmentUtils.safeLaunchDialogFragment(dialogFragment, getActivity(), null);
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

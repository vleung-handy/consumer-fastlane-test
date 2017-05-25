package com.handybook.handybook.proteam.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.constant.RequestCode;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.HandyRetrofitService;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.ui.view.EmptiableRecyclerView;
import com.handybook.handybook.library.util.FragmentUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.ProTeamPageLog;
import com.handybook.handybook.proteam.adapter.NewProTeamCategoryAdapter;
import com.handybook.handybook.proteam.event.ProTeamEvent;
import com.handybook.handybook.proteam.manager.ProTeamManager;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.proteam.model.ProTeamEdit;
import com.handybook.handybook.proteam.model.ProTeamEditWrapper;
import com.handybook.handybook.proteam.model.ProTeamWrapper;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.proteam.viewmodel.ProTeamActionPickerViewModel;
import com.handybook.handybook.proteam.viewmodel.ProTeamActionPickerViewModel.ActionType;
import com.handybook.handybook.referral.model.ProReferral;
import com.handybook.handybook.referral.model.ReferralDescriptor;
import com.squareup.otto.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewProTeamProListFragment extends InjectedFragment {

    @Inject
    HandyRetrofitService mService;
    @Inject
    ProTeamManager mProTeamManager;

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
    private Map<String, ProReferral> mProReferrals;

    //we only need mReferralDescriptor because it contains the sender/receiver referral amounts
    private ReferralDescriptor mReferralDescriptor;

    private NewProTeamCategoryAdapter.ActionCallbacks mProTeamActionCallbacks =
            new NewProTeamCategoryAdapter.ActionCallbacks() {
                @Override
                public void onHeartClick(final Provider proTeamPro) {
                    String title;
                    String subtitle = null;
                    List<ActionType> actionTypes = new ArrayList<>();
                    if (proTeamPro.isFavorite()) {
                        title = getString(
                                R.string.remove_as_favorite_formatted,
                                proTeamPro.getName()
                        );
                        actionTypes.add(ActionType.UNFAVORITE);
                        actionTypes.add(ActionType.BLOCK);
                    }
                    else {
                        title = getString(R.string.set_as_favorite_formatted, proTeamPro.getName());
                        final Provider favoritePro = mProTeamCategory.getFavoritePro();
                        if (favoritePro != null) {
                            subtitle = getString(
                                    R.string.auto_remove_as_favorite_warning_formatted,
                                    favoritePro.getName()
                            );
                        }
                        actionTypes.add(ActionType.FAVORITE);
                    }

                    launchProTeamActionPicker(new ProTeamActionPickerViewModel(
                            Integer.parseInt(proTeamPro.getId()),
                            proTeamPro.getCategoryType(), proTeamPro.getImageUrl(),
                            title,
                            subtitle,
                            actionTypes
                    ));
                }

                @Override
                public void onLongClick(final Provider proTeamPro) {
                    launchProTeamActionPicker(new ProTeamActionPickerViewModel(
                            Integer.parseInt(proTeamPro.getId()),
                            proTeamPro.getCategoryType(), proTeamPro.getImageUrl(),
                            getString(R.string.block_formatted, proTeamPro.getName()),
                            null,
                            Lists.newArrayList(ActionType.BLOCK)
                    ));
                }
            };

    /**
     * We do this typically after a pro has been "favorited" -- launch a dialog offering the user
     * the ability to share this current pro.
     * @param proId
     */
    private void launchProReferralDialog(final int proId) {
        FavProReferralDialogFragment dialogFragment = FavProReferralDialogFragment.newInstance(
                mProReferrals.get(String.valueOf(proId)),
                mReferralDescriptor
        );
        FragmentUtils.safeLaunchDialogFragment(dialogFragment, getActivity(), null);
    }

    private void launchProTeamActionPicker(final ProTeamActionPickerViewModel viewModel) {
        final ProTeamActionPickerDialogFragment dialogFragment =
                ProTeamActionPickerDialogFragment.newInstance(viewModel);
        dialogFragment.setTargetFragment(this, RequestCode.EDIT_PRO_TEAM_PREFERENCE);
        FragmentUtils.safeLaunchDialogFragment(dialogFragment, getActivity(), null);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK
            && requestCode == RequestCode.EDIT_PRO_TEAM_PREFERENCE
            && data != null) {
            final int proId = data.getIntExtra(BundleKeys.PRO_TEAM_PRO_ID, -1);
            final ActionType actionType = (ActionType)
                    data.getSerializableExtra(BundleKeys.EDIT_PRO_TEAM_PREFERENCE_ACTION_TYPE);
            if (proId != -1 && actionType != null) {
                applyPreference(proId, actionType.getMatchPreference());
            }
        }
    }

    private void applyPreference(
            final int proId,
            @NonNull final ProviderMatchPreference matchPreference
    ) {
        final User currentUser = userManager.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        final ProTeamEdit proTeamEdit = new ProTeamEdit(matchPreference);
        proTeamEdit.addId(proId, mProTeamCategoryType);
        final ArrayList<ProTeamEdit> proTeamEdits = Lists.newArrayList(proTeamEdit);

        final DataManager.Callback<ProTeamWrapper> cb =
                new FragmentSafeCallback<ProTeamWrapper>(this) {
                    @Override
                    public void onCallbackSuccess(final ProTeamWrapper proTeamWrapper) {
                        progressDialog.dismiss();
                        showToast(R.string.pro_team_update_successful);
                        final ProTeam proTeam = proTeamWrapper.getProTeam();
                        if (proTeam != null) {
                            mProTeamCategory = proTeam.getCategory(mProTeamCategoryType);
                            initRecyclerView();
                            bus.post(new ProTeamEvent.ProTeamUpdated(proTeam));
                        }
                        bus.post(new LogEvent.AddLogEvent(
                                new ProTeamPageLog.EditProTeamSuccess(proTeamEdits)));

                        if (matchPreference == ProviderMatchPreference.FAVORITE
                            && mProReferrals != null
                            && mReferralDescriptor != null) {
                            launchProReferralDialog(proId);
                        }
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        progressDialog.dismiss();
                        showToast(!TextUtils.isEmpty(error.getMessage()) ? error.getMessage() :
                                  getString(R.string.default_error_string));
                        bus.post(new LogEvent.AddLogEvent(
                                new ProTeamPageLog.EditProTeamFailure(proTeamEdits)));
                    }
                };

        final String source = ProTeamEvent.Source.PRO_MANAGEMENT.toString();
        progressDialog.show();
        mProTeamManager.editProTeam(currentUser.getId(), new ProTeamEditWrapper(
                proTeamEdits,
                source
        ), cb);

        bus.post(new LogEvent.AddLogEvent(
                new ProTeamPageLog.EditProTeamSubmitted(proTeamEdits)));
    }

    public static NewProTeamProListFragment newInstance(
            @NonNull final ProTeam.ProTeamCategory proTeamCategory,
            @NonNull final ProTeamCategoryType proTeamCategoryType,
            @Nullable final Map<String, ProReferral> proReferrals,
            @Nullable final ReferralDescriptor referralDescriptor
    ) {
        final NewProTeamProListFragment fragment = new NewProTeamProListFragment();
        final Bundle arguments = new Bundle();
        arguments.putParcelable(BundleKeys.PRO_TEAM_CATEGORY, proTeamCategory);
        arguments.putSerializable(BundleKeys.PRO_TEAM_CATEGORY_TYPE, proTeamCategoryType);
        arguments.putSerializable(BundleKeys.REFERRAL_DESCRIPTOR, referralDescriptor);

        arguments.putSerializable(BundleKeys.PRO_REFERRAL, (Serializable) proReferrals);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProTeamCategory = getArguments().getParcelable(BundleKeys.PRO_TEAM_CATEGORY);
        mProTeamCategoryType = (ProTeamCategoryType) getArguments().getSerializable(
                BundleKeys.PRO_TEAM_CATEGORY_TYPE);
        mProReferrals
                = (Map<String, ProReferral>) getArguments().getSerializable(BundleKeys.PRO_REFERRAL);
        mReferralDescriptor
                = (ReferralDescriptor) getArguments().getSerializable(BundleKeys.REFERRAL_DESCRIPTOR);
    }


    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        final View view = inflater.inflate(
                R.layout.fragment_new_pro_team_pro_list,
                container,
                false
        );
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setEmptyView(mEmptyView);
        mEmptyViewTitle.setText(R.string.pro_team_empty_card_title);
        mEmptyViewText.setText(R.string.pro_team_empty_card_text);
        initRecyclerView();
    }

    @Subscribe
    public void onProTeamUpdated(final ProTeamEvent.ProTeamUpdated event) {
        mProTeamCategory = event.getUpdatedProTeam().getCategory(mProTeamCategoryType);
        initRecyclerView();
    }

    private void initRecyclerView() {
        mRecyclerView.setAdapter(new NewProTeamCategoryAdapter(
                getActivity(),
                mProTeamCategory,
                mProTeamActionCallbacks
        ));
    }
}

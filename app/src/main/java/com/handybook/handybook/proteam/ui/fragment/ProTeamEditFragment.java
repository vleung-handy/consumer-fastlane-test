package com.handybook.handybook.proteam.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.constant.RequestCode;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.ui.fragment.ProgressSpinnerFragment;
import com.handybook.handybook.library.util.FragmentUtils;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.ProTeamPageLog;
import com.handybook.handybook.proteam.event.ProTeamEvent;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.proteam.viewmodel.ProTeamActionPickerViewModel;
import com.handybook.handybook.proteam.viewmodel.ProTeamActionPickerViewModel.ActionType;
import com.handybook.handybook.referral.model.ProReferral;
import com.handybook.handybook.referral.model.ReferralDescriptor;
import com.handybook.handybook.referral.model.ReferralResponse;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;

import static com.handybook.handybook.proteam.viewmodel.ProTeamActionPickerViewModel.ActionType.BLOCK;

/**
 * A simple {@link Fragment} subclass. Use the {@link ProTeamEditFragment#newInstance} factory
 * method to create an instance of this fragment.
 */
public class ProTeamEditFragment extends ProgressSpinnerFragment implements
        ProTeamProListFragment.OnProInteraction {

    @Bind(R.id.pro_team_toolbar)
    Toolbar mToolbar;
    @Bind(R.id.pro_team_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.pro_team_view_pager)
    ViewPager mViewPager;
    @Bind(R.id.pro_team_pro_list_holder)
    ViewGroup mListHolder;
    @Bind(R.id.pro_team_tab_layout)
    TabLayout mTabLayout;
    @Bind(R.id.pro_team_toolbar_save_button)
    View mToolbarSaveButton;

    private ProTeam mProTeam;
    private Map<String, ProReferral> mProReferrals;
    private ReferralDescriptor mReferralDescriptor;
    private boolean mProTeamRequestDone = false;
    private boolean mReferralDescriptorRequestDone = false;

    private TabAdapter mTabAdapter;
    private HashSet<Provider> mCleanersToAdd = new HashSet<>();
    private HashSet<Provider> mCleanersToRemove = new HashSet<>();
    private HashSet<Provider> mHandymenToAdd = new HashSet<>();
    private HashSet<Provider> mHandymenToRemove = new HashSet<>();
    private ProTeamProListFragment mProTeamListFragment;

    public static ProTeamEditFragment newInstance() {
        return new ProTeamEditFragment();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(R.layout.fragment_pro_team_edit, container, false));

        ButterKnife.bind(this, view);

        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.handy_service_handyman,
                R.color.handy_service_electrician,
                R.color.handy_service_cleaner,
                R.color.handy_service_painter,
                R.color.handy_service_plumber
        );
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestProTeam();
            }
        });
        mToolbarSaveButton.setVisibility(isSettingFavoriteProEnabled() ? View.GONE : View.VISIBLE);
        if (mProTeam != null) {
            initialize();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupToolbar(mToolbar, getString(R.string.edit_pro_team));
        if (mProTeam == null) {
            mSwipeRefreshLayout.setRefreshing(true);
            requestProTeam();
            requestReferralDescriptor();
        }
    }

    private void requestProTeam() {
        mProTeamRequestDone = false;
        bus.post(new ProTeamEvent.RequestProTeam());
    }

    private void requestReferralDescriptor() {
        mReferralDescriptorRequestDone = false;
        dataManager.requestPrepareReferrals(
                false,
                new FragmentSafeCallback<ReferralResponse>(this) {
                    @Override
                    public void onCallbackSuccess(final ReferralResponse response) {
                        mReferralDescriptor = response.getReferralDescriptor();
                        mReferralDescriptorRequestDone = true;
                        if (isInitialRequestsDone()) {
                            initialize();
                        }
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        // do nothing
                        mReferralDescriptorRequestDone = true;
                        dataManagerErrorHandler.handleError(getContext(), error);
                        if (isInitialRequestsDone()) {
                            initialize();
                        }
                    }
                }
        );
    }

    @Subscribe
    public void onReceiveProTeamSuccess(final ProTeamEvent.ReceiveProTeamSuccess event) {
        mProTeamRequestDone = true;
        mProTeam = event.getProTeam();
        mProReferrals = event.getProReferral();
        if (isInitialRequestsDone()) {
            initialize();
        }
        bus.post(new ProTeamEvent.ProTeamUpdated(mProTeam));
    }

    private boolean isInitialRequestsDone() {
        return mProTeamRequestDone && mReferralDescriptorRequestDone;
    }

    @Subscribe
    public void onReceiveProTeamError(final ProTeamEvent.ReceiveProTeamError event) {
        mSwipeRefreshLayout.setRefreshing(false);
        showToast(R.string.default_error_string);
    }

    private void initialize() {
        mSwipeRefreshLayout.setRefreshing(false);
        if (isSettingFavoriteProEnabled()) {
            initProTeamViewPager();
        }
        else {
            initProTeamListFragment();
        }
    }

    private boolean isSettingFavoriteProEnabled() {
        return mConfigurationManager.getPersistentConfiguration().isSettingFavoriteProEnabled();
    }

    private void initProTeamListFragment() {
        mViewPager.setVisibility(View.GONE);
        mListHolder.setVisibility(View.VISIBLE);
        mProTeamListFragment = ProTeamProListFragment.newInstance(mProTeam, null, false);
        mProTeamListFragment.setOnProInteraction(this);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.pro_team_pro_list_holder, mProTeamListFragment)
                .commit();
        mProTeamListFragment.setProTeam(mProTeam);
    }

    private void initProTeamViewPager() {
        if (mProTeam != null) {
            mListHolder.setVisibility(View.GONE);
            mViewPager.setVisibility(View.VISIBLE);
            mTabAdapter = new TabAdapter(getActivity(), getChildFragmentManager());
            mViewPager.setAdapter(mTabAdapter);
            mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(
                    mTabLayout));
            mTabLayout.setupWithViewPager(mViewPager);
            mTabLayout.setVisibility(mTabAdapter.getCount() == 1 ? View.GONE : View.VISIBLE);
        }
    }

    @Subscribe
    public void onReceiveProTeamEditSuccess(final ProTeamEvent.ReceiveProTeamEditSuccess event) {
        mProTeam = event.getProTeam();
        if (mProTeamListFragment != null) {
            mProTeamListFragment.setProTeam(mProTeam);
        }
        clearEditHolders();
        hideProgressSpinner();
        showToast(R.string.pro_team_update_successful);
        setActivityResult();
        bus.post(new ProTeamEvent.ProTeamUpdated(mProTeam));
    }

    // This is triggered by NewProTeamProListFragment
    @Subscribe
    public void onProTeamUpdated(final ProTeamEvent.ProTeamUpdated event) {
        mProTeam = event.getUpdatedProTeam();
        setActivityResult();
    }

    private void setActivityResult() {
        getActivity().setResult(
                Activity.RESULT_OK,
                new Intent().putExtra(BundleKeys.PRO_TEAM, mProTeam)
        );
    }

    private void clearEditHolders() {
        mCleanersToAdd.clear();
        mCleanersToRemove.clear();
        mHandymenToAdd.clear();
        mHandymenToRemove.clear();
    }

    @Subscribe
    public void onReceiveProTeamEditError(final ProTeamEvent.ReceiveProTeamEditError event) {
        hideProgressSpinner();
    }

    @OnClick(R.id.pro_team_toolbar_save_button)
    void saveProTeamEdits() {
        if (mCleanersToAdd.isEmpty()
            && mCleanersToRemove.isEmpty()
            && mHandymenToAdd.isEmpty()
            && mHandymenToRemove.isEmpty()) {
            //no changes were made, so we'll just tell them save successful
            showToast(R.string.pro_team_update_successful);
            return;
        }
        bus.post(
                new ProTeamEvent.RequestProTeamEdit(
                        mCleanersToAdd,
                        mHandymenToAdd,
                        mCleanersToRemove,
                        mHandymenToRemove,
                        ProTeamEvent.Source.PRO_MANAGEMENT
                )
        );
        bus.post(new LogEvent.AddLogEvent(new ProTeamPageLog.UpdateSubmitted(
                mCleanersToAdd.size() + mHandymenToAdd.size(),
                mCleanersToRemove.size() + mHandymenToRemove.size(),
                ProTeamPageLog.Context.MAIN_MANAGEMENT
        )));
        showProgressSpinner(true);
    }

    /**
     * Implementation of ProTeamProListFragment.OnProInteraction listener
     */
    @Override
    public void onProRemovalRequested(
            final Provider proTeamPro,
            final ProviderMatchPreference providerMatchPreference
    ) {
        final ProTeamActionPickerViewModel viewModel = new ProTeamActionPickerViewModel(
                Integer.parseInt(proTeamPro.getId()),
                proTeamPro.getCategoryType(),
                proTeamPro.getImageUrl(),
                getString(R.string.block_formatted, proTeamPro.getName()),
                null,
                Lists.newArrayList(BLOCK)
        );
        final ProTeamActionPickerDialogFragment dialogFragment =
                ProTeamActionPickerDialogFragment.newInstance(viewModel);
        dialogFragment.setTargetFragment(this, RequestCode.EDIT_PRO_TEAM_PREFERENCE);
        FragmentUtils.safeLaunchDialogFragment(dialogFragment, getActivity(), null);

        bus.post(new LogEvent.AddLogEvent(new ProTeamPageLog.BlockProvider.Tapped(
                proTeamPro.getId(),
                providerMatchPreference,
                ProTeamPageLog.Context.MAIN_MANAGEMENT
        )));
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK
            && requestCode == RequestCode.EDIT_PRO_TEAM_PREFERENCE
            && data != null) {
            final int proId = data.getIntExtra(BundleKeys.PRO_TEAM_PRO_ID, -1);
            final ProTeamCategoryType categoryType = (ProTeamCategoryType)
                    data.getSerializableExtra(BundleKeys.PRO_TEAM_CATEGORY_TYPE);
            final ActionType actionType = (ActionType) data.getSerializableExtra(
                    BundleKeys.EDIT_PRO_TEAM_PREFERENCE_ACTION_TYPE);
            if (proId != -1 && actionType != null && categoryType != null && actionType == BLOCK) {
                bus.post(new ProTeamEvent.RequestProTeamEdit(
                        proId,
                        categoryType,
                        ProviderMatchPreference.NEVER,
                        ProTeamEvent.Source.PRO_MANAGEMENT
                ));
                bus.post(new ProTeamPageLog.BlockProvider.Submitted(
                        String.valueOf(proId),
                        ProviderMatchPreference.NEVER,
                        ProTeamPageLog.Context.MAIN_MANAGEMENT
                ));
                showProgressSpinner(true);
            }
        }
    }

    @Override
    public void onProCheckboxStateChanged(
            @NonNull final Provider proTeamPro,
            final boolean isChecked
    ) {
        if (isChecked) {
            switch (proTeamPro.getCategoryType()) {
                case CLEANING:
                    mCleanersToAdd.add(proTeamPro);
                    mCleanersToRemove.remove(proTeamPro);
                    break;
                case HANDYMEN:
                    mHandymenToAdd.add(proTeamPro);
                    mHandymenToRemove.remove(proTeamPro);
                    break;
            }
        }
        else {
            switch (proTeamPro.getCategoryType()) {
                case CLEANING:
                    mCleanersToRemove.add(proTeamPro);
                    mCleanersToAdd.remove(proTeamPro);
                    break;
                case HANDYMEN:
                    mHandymenToRemove.add(proTeamPro);
                    mHandymenToAdd.remove(proTeamPro);
                    break;
            }
        }
        bus.post(new LogEvent.AddLogEvent(new ProTeamPageLog.EnableButtonTapped(
                proTeamPro.getId(),
                isChecked,
                ProTeamPageLog.Context.MAIN_MANAGEMENT
        )));
    }

    @Override
    public void onSave() {
        saveProTeamEdits();
    }

    private class TabAdapter extends FragmentPagerAdapter {

        private final Context mContext;
        private List<InjectedFragment> mFragments = new ArrayList<>();
        private List<CharSequence> mPageTitles = new ArrayList<>();

        TabAdapter(final Context context, final FragmentManager fragmentManager) {
            super(fragmentManager);
            mContext = context;
            mFragments.add(NewProTeamProListFragment.newInstance(
                    mProTeam.getCategory(ProTeamCategoryType.CLEANING),
                    ProTeamCategoryType.CLEANING,
                    mProReferrals,
                    mReferralDescriptor
            ));
            mPageTitles.add(mContext.getString(R.string.cleaners));
            if (shouldShowHandymenTab()) {
                final ProTeamProListFragment fragment = ProTeamProListFragment.newInstance(
                        mProTeam,
                        ProTeamCategoryType.HANDYMEN,
                        true
                );
                fragment.setOnProInteraction(ProTeamEditFragment.this);
                mFragments.add(fragment);
                mPageTitles.add(mContext.getString(R.string.handymen));
            }
        }

        private boolean shouldShowHandymenTab() {
            final ProTeam.ProTeamCategory handymenCategory =
                    mProTeam.getCategory(ProTeamCategoryType.HANDYMEN);
            return handymenCategory != null && !handymenCategory.isEmpty();
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            final CharSequence text = mPageTitles.get(position);
            final CalligraphyTypefaceSpan titleType = new CalligraphyTypefaceSpan(
                    TextUtils.get(mContext, TextUtils.Fonts.CIRCULAR_BOOK));
            final SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
            stringBuilder.setSpan(titleType, 0, text.length(), 0);
            return stringBuilder;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public InjectedFragment getItem(int position) {
            return mFragments.get(position);
        }
    }
}

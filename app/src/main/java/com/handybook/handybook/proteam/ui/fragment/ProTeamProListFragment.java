package com.handybook.handybook.proteam.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.ui.view.EmptiableRecyclerView;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.ProTeamPageLog;
import com.handybook.handybook.proteam.adapter.ProTeamCategoryAdapter;
import com.handybook.handybook.proteam.event.ProTeamEvent;
import com.handybook.handybook.proteam.holder.ProTeamFacebookHolder;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.proteam.viewmodel.ProTeamProViewModel;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.handybook.handybook.proteam.model.ProTeamCategoryType.CLEANING;
import static com.handybook.handybook.proteam.model.ProviderMatchPreference.PREFERRED;

/**
 * A simple {@link Fragment} subclass. Use the {@link ProTeamProListFragment#newInstance} factory
 * method to create an instance of this fragment.
 */
public class ProTeamProListFragment extends InjectedFragment {

    private static final String KEY_PROTEAM = "ProTeamProList:ProTeam";
    private static final String KEY_PROTEAM_CATEGORY_TYPE = "ProTeamProList:CategoryType";
    private static final String KEY_SAVE_BUTTON_ENABLED = "ProTeamProList:SaveButtonEnabled";

    @Bind(R.id.pro_team_pro_list_recycler_view)
    EmptiableRecyclerView mRecyclerView;
    @Bind(R.id.pro_team_pro_list_save_button)
    Button mSaveButton;
    @Bind(R.id.pro_team_empty_view)
    View mEmptyView;
    @Bind(R.id.pro_team_empty_view_title)
    TextView mEmptyViewTitle;
    @Bind(R.id.pro_team_empty_view_text)
    TextView mEmptyViewText;

    private ProTeam mProTeam;
    private ProTeamCategoryType mProTeamCategoryType;
    private OnProInteraction mOnProInteraction;
    private ProTeamProViewModel.OnInteractionListener mOnInteractionListener;
    private CallbackManager mFacebookCallbackManager;

    private static boolean sXButtonPressed = false;
    private boolean mSaveButtonEnabled;

    {
        mOnInteractionListener = new ProTeamProViewModel.OnInteractionListener() {
            @Override
            public void onLongClick(
                    final Provider proTeamPro,
                    final ProviderMatchPreference providerMatchPreference
            ) {
                if (mOnProInteraction == null) {
                    return;
                }
                mOnProInteraction.onProRemovalRequested(proTeamPro, providerMatchPreference);
            }

            @Override
            public void onCheckedChanged(final Provider proTeamPro, final boolean checked) {
                if (mOnProInteraction == null) {
                    return;
                }
                if (mSaveButtonEnabled) {
                    mSaveButton.setVisibility(View.VISIBLE);
                }
                mOnProInteraction.onProCheckboxStateChanged(proTeamPro, checked);
            }
        };
    }

    public static ProTeamProListFragment newInstance(
            @NonNull final ProTeam proTeam,
            @Nullable final ProTeamCategoryType proTeamCategoryType,
            final boolean saveButtonEnabled
    ) {
        ProTeamProListFragment fragment = new ProTeamProListFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_PROTEAM, proTeam);
        bundle.putSerializable(KEY_PROTEAM_CATEGORY_TYPE, proTeamCategoryType);
        bundle.putBoolean(KEY_SAVE_BUTTON_ENABLED, saveButtonEnabled);
        fragment.setArguments(bundle);
        return fragment;
    }

    @OnClick(R.id.pro_team_pro_list_save_button)
    public void onSaveClicked() {
        if (mOnProInteraction != null) {
            mOnProInteraction.onSave();
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFacebookCallbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        final Bundle arguments = getArguments();
        if (arguments != null) {
            mProTeam = arguments.getParcelable(KEY_PROTEAM);
            mProTeamCategoryType = (ProTeamCategoryType)
                    arguments.getSerializable(KEY_PROTEAM_CATEGORY_TYPE);
            mSaveButtonEnabled = arguments.getBoolean(KEY_SAVE_BUTTON_ENABLED);
        }

        final View view = inflater.inflate(R.layout.fragment_pro_team_pro_list, container, false);
        ButterKnife.bind(this, view);

        initialize();
        return view;
    }

    @Override
    public final void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void initialize() {
        if (mSaveButton != null) {
            mSaveButton.setVisibility(View.GONE);
        }
        initEmptyView();
        initRecyclerView();
    }

    private void initEmptyView() {
        if (mEmptyViewTitle == null || mEmptyViewText == null) {
            return;
        }
        if (mProTeam == null) {
            mEmptyViewTitle.setText(R.string.pro_team_empty_card_title_loading);
            mEmptyViewText.setText(R.string.pro_team_empty_card_text_loading);
        }
        else {
            mEmptyViewTitle.setText(R.string.pro_team_empty_card_title);
            mEmptyViewText.setText(R.string.pro_team_empty_card_text);
        }
    }

    private void initRecyclerView() {
        if (mRecyclerView == null) {
            return;
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setEmptyView(mEmptyView);
        if (mProTeam == null) {
            return;
        }

        final boolean shouldShowProImage = mConfigurationManager
                .getPersistentConfiguration().isProTeamProfilePicturesEnabled();
        final ProTeam.ProTeamCategory proTeamCategory = mProTeamCategoryType != null
                                                        ?
                                                        mProTeam.getCategory(mProTeamCategoryType)
                                                        : mProTeam.getAllCategories();
        final boolean shouldShowHandymanIndicators = mProTeamCategoryType == null;
        final ProTeamCategoryAdapter proCardCardAdapter = new ProTeamCategoryAdapter(
                proTeamCategory,
                shouldShowProImage,
                shouldShowHandymanIndicators,
                mOnInteractionListener
        );

        if (mConfigurationManager.getPersistentConfiguration().isProTeamFacebookLoginEnabled()
            && !sXButtonPressed
            && AccessToken.getCurrentAccessToken() == null) {
            addFacebookHeader(proCardCardAdapter);
        }
        mRecyclerView.setAdapter(proCardCardAdapter);
        proCardCardAdapter.notifyDataSetChanged();
    }

    private void addFacebookHeader(final ProTeamCategoryAdapter proTeamCategoryAdapter) {
        View facebookHeaderView = LayoutInflater
                .from(getContext())
                .inflate(R.layout.view_pro_team_facebook, null, false);
        View.OnClickListener logInButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                bus.post(new LogEvent.AddLogEvent(
                        new ProTeamPageLog.FacebookConnectTapped(
                                mProTeam.getCount(CLEANING, PREFERRED))));
            }
        };
        View.OnClickListener closeButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                sXButtonPressed = true;
                proTeamCategoryAdapter.enableFacebookHeader(false);
                proTeamCategoryAdapter.notifyDataSetChanged();
            }
        };
        ProTeamFacebookHolder proTeamFacebookHolder = new ProTeamFacebookHolder(
                facebookHeaderView,
                this,
                mFacebookCallbackManager,
                logInButtonListener,
                closeButtonListener
        );

        proTeamCategoryAdapter.setFacebookHeaderHolder(proTeamFacebookHolder);
    }

    public void setProTeam(final ProTeam proTeam) {
        mProTeam = proTeam;
        initialize();
    }

    @Subscribe
    public void onProTeamUpdated(final ProTeamEvent.ProTeamUpdated event) {
        mProTeam = event.getUpdatedProTeam();
        initialize();
    }

    public void setOnProInteraction(final OnProInteraction onProInteraction) {
        mOnProInteraction = onProInteraction;
    }

    /**
     * Implement this interface to be notified when user clicks on one of the pro cards.
     */
    public interface OnProInteraction {

        void onProRemovalRequested(
                Provider proTeamPro,
                ProviderMatchPreference providerMatchPreference
        );

        void onProCheckboxStateChanged(
                Provider proTeamPro,
                boolean state
        );

        void onSave();
    }
}

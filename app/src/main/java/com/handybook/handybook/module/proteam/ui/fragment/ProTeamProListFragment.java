package com.handybook.handybook.module.proteam.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.handybook.handybook.R;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.ui.view.EmptiableRecyclerView;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.ProTeamPageLog;
import com.handybook.handybook.module.proteam.adapter.ProTeamCategoryAdapter;
import com.handybook.handybook.module.proteam.holder.ProTeamFacebookHolder;
import com.handybook.handybook.module.proteam.model.ProTeam;
import com.handybook.handybook.module.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.module.proteam.model.ProTeamPro;
import com.handybook.handybook.module.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.module.proteam.viewmodel.ProTeamProViewModel;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.handybook.handybook.module.proteam.model.ProTeamCategoryType.CLEANING;
import static com.handybook.handybook.module.proteam.model.ProviderMatchPreference.PREFERRED;

/**
 * A simple {@link Fragment} subclass. Use the {@link ProTeamProListFragment#newInstance} factory
 * method to create an instance of this fragment.
 */
public class ProTeamProListFragment extends InjectedFragment
{
    private static final String KEY_PROTEAM = "ProTeamProList:ProTeam";
    private static final String KEY_PROTEAM_CATEGORY_TYPE = "ProTeamProList:CategoryType";

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
    private OnProInteraction mOnProInteraction;
    private ProTeamProViewModel.OnInteractionListener mOnInteractionListener;
    private CallbackManager mFacebookCallbackManager;

    private static boolean sXButtonPressed = false;

    {
        mOnInteractionListener = new ProTeamProViewModel.OnInteractionListener()
        {
            @Override
            public void onLongClick(
                    final ProTeamPro proTeamPro,
                    final ProviderMatchPreference providerMatchPreference
            )
            {
                if (mOnProInteraction == null)
                {
                    return;
                }
                mOnProInteraction.onProRemovalRequested(
                        mProTeamCategoryType,
                        proTeamPro,
                        providerMatchPreference
                );
            }

            @Override
            public void onCheckedChanged(final ProTeamPro proTeamPro, final boolean checked)
            {
                if (mOnProInteraction == null)
                {
                    return;
                }
                mOnProInteraction.onProCheckboxStateChanged(
                        mProTeamCategoryType,
                        proTeamPro,
                        checked
                );
            }
        };
    }

    public ProTeamProListFragment()
    {
        // Required empty public constructor

    }

    public static ProTeamProListFragment newInstance(
            @Nullable ProTeam proTeam,
            @NonNull ProTeamCategoryType proTeamCategoryType
    )
    {
        ProTeamProListFragment fragment = new ProTeamProListFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_PROTEAM, proTeam);
        bundle.putParcelable(KEY_PROTEAM_CATEGORY_TYPE, proTeamCategoryType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments != null)
        {
            mProteam = arguments.getParcelable(KEY_PROTEAM);
            mProTeamCategoryType = arguments.getParcelable(KEY_PROTEAM_CATEGORY_TYPE);
        }
        mFacebookCallbackManager = CallbackManager.Factory.create();
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

        initialize();
        return view;
    }

    @Override
    public final void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void initialize()
    {
        initEmptyView();
        initRecyclerView();
    }

    private void initEmptyView()
    {
        if (mEmptyViewTitle == null || mEmptyViewText == null)
        {
            return;
        }
        if (mProteam == null)
        {
            mEmptyViewTitle.setText(R.string.pro_team_empty_card_title_loading);
            mEmptyViewText.setText(R.string.pro_team_empty_card_text_loading);
        }
        else
        {
            mEmptyViewTitle.setText(R.string.pro_team_empty_card_title);
            mEmptyViewText.setText(R.string.pro_team_empty_card_text);
        }
    }

    private void initRecyclerView()
    {
        if (mRecyclerView == null)
        {
            return;
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setEmptyView(mEmptyView);
        if (mProteam == null)
        {
            return;
        }

        final ProTeamCategoryAdapter proCardCardAdapter = new ProTeamCategoryAdapter(
                userManager.getCurrentUser(),
                mProteam,
                mProTeamCategoryType,
                mOnInteractionListener
        );

        if (configurationManager.getPersistentConfiguration().isProTeamFacebookLoginEnabled()
                && mProTeamCategoryType == CLEANING
                && !sXButtonPressed
                && AccessToken.getCurrentAccessToken() == null)
        {
            addFacebookHeader(proCardCardAdapter);
        }
        mRecyclerView.setAdapter(proCardCardAdapter);
        proCardCardAdapter.notifyDataSetChanged();
    }

    private void addFacebookHeader(final ProTeamCategoryAdapter proTeamCategoryAdapter)
    {
        View facebookHeaderView = LayoutInflater
                .from(getContext())
                .inflate(R.layout.view_pro_team_facebook, null, false);
        View.OnClickListener logInButtonListener = new View.OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
                bus.post(new LogEvent.AddLogEvent(
                        new ProTeamPageLog.FacebookConnectTapped(
                                mProteam.getCount(CLEANING, PREFERRED))));
            }
        };
        View.OnClickListener closeButtonListener = new View.OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
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

    /**
     * for logging purposes
     *
     * @return
     */
    public ProTeamCategoryType getProTeamCategoryType()
    {
        return mProTeamCategoryType;
    }

    public void setProTeam(final ProTeam proTeam)
    {
        mProteam = proTeam;
        initialize();
    }

    public void setOnProInteraction(final OnProInteraction onProInteraction)
    {
        mOnProInteraction = onProInteraction;
    }

    /**
     * Implement this interface to be notified when user clicks on one of the pro cards.
     */
    public interface OnProInteraction
    {
        void onProRemovalRequested(
                ProTeamCategoryType proTeamCategoryType,
                ProTeamPro proTeamPro,
                ProviderMatchPreference providerMatchPreference
        );

        void onProCheckboxStateChanged(
                ProTeamCategoryType proTeamCategoryType,
                ProTeamPro proTeamPro,
                boolean state
        );
    }
}

package com.handybook.handybook.proteam.mypros;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.ui.view.LoadingLayout;
import com.handybook.handybook.proteam.manager.ProTeamManager;
import com.handybook.handybook.proteam.model.ProTeamWrapper;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * pro team info in the context of the my pros tab
 */
public class ProTeamInfoFragment extends InjectedFragment {

    @BindView(R.id.loading_error_layout)
    View mLoadingErrorLayout;

    @BindView(R.id.fragment_my_pros_pro_team_recycler_view_empty_layout)
    View mProTeamEmptyLayout;

    @BindView(R.id.fragment_my_pros_pro_team_recycler_view_loading_layout)
    LoadingLayout mLoadingLayout;

    @BindView(R.id.fragment_my_pros_pro_team_recycler_view)
    RecyclerView mProTeamRecyclerView;

    @Inject
    ProTeamManager mProTeamManager;

    private long mProTeamResponseLastReceivedTimestampMs = 0;

    public static ProTeamInfoFragment newInstance() {
        return new ProTeamInfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        final View view = inflater.inflate(
                R.layout.fragment_my_pros_pro_team_recycler_view_container,
                null
        );
        ButterKnife.bind(this, view);
        mProTeamRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        ));
        return view;
    }

    private void requestProTeam() {
        //hide all views except the loading view
        mProTeamRecyclerView.setVisibility(View.GONE);
        mProTeamEmptyLayout.setVisibility(View.GONE);
        mLoadingErrorLayout.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.VISIBLE);

        mProTeamManager.requestProTeam(new FragmentSafeCallback<ProTeamWrapper>(this) {
            @Override
            public void onCallbackSuccess(final ProTeamWrapper response) {
                onReceiveProTeamSuccess(response);
            }

            @Override
            public void onCallbackError(final DataManager.DataManagerError error) {
                onReceiveProTeamError(error);
            }
        });
    }

    public void onResume() {
        super.onResume();

        //pro team has definitely been updated
        if (mProTeamManager.isProTeamResponseDefinitelyOutdated(
                mProTeamResponseLastReceivedTimestampMs)) {
            requestProTeam();
        }
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //request pro team
        requestProTeam();
    }

    private void onReceiveProTeamError(final DataManager.DataManagerError error) {
        mLoadingLayout.setVisibility(View.GONE);
        mLoadingErrorLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.loading_error_try_again_button)
    public void onTryAgainButtonClicked() {
        requestProTeam();
    }

    private void onReceiveProTeamSuccess(@NonNull final ProTeamWrapper proTeamWrapper) {
        mProTeamResponseLastReceivedTimestampMs = System.currentTimeMillis();
        ProTeamRecyclerViewAdapter proTeamRecyclerViewAdapter
                = new ProTeamRecyclerViewAdapter(proTeamWrapper.getProTeam(), bus);

        if (proTeamRecyclerViewAdapter.isEmpty()) {
            //show empty layout
            mProTeamEmptyLayout.setVisibility(View.VISIBLE);
            mProTeamRecyclerView.setVisibility(View.GONE);
        }
        else {
            //show pro team recycler view
            mProTeamRecyclerView.setAdapter(proTeamRecyclerViewAdapter);
            mProTeamRecyclerView.setVisibility(View.VISIBLE);
        }

        mLoadingLayout.setVisibility(View.GONE);
    }
}

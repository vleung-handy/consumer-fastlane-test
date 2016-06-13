package com.handybook.handybook.module.proteam.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.module.proteam.model.ProTeamPro;
import com.handybook.handybook.module.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.module.proteam.viewmodel.ProTeamProViewModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class ProTeamProHolder extends RecyclerView.ViewHolder
{
    private ProTeamProViewModel mProTeamProViewModel;
    private ProTeamProViewModel.OnInteractionListener mOnInteractionListener;
    @Bind(R.id.pro_team_pro_card_pro_title)
    TextView mTitle;
    @Bind(R.id.pro_team_pro_card_pro_subtitle)
    TextView mSubtitle;
    @Bind(R.id.pro_team_pro_card_pro_footer)
    TextView mFooter;
    @Bind(R.id.pro_team_pro_card_checkbox)
    CheckBox mCheckbox;
    @Bind(R.id.pro_team_pro_card_x)
    ImageButton mXButton;


    public ProTeamProHolder(View itemView, ProTeamProViewModel.OnInteractionListener onInteractionListener)
    {
        super(itemView);
        mOnInteractionListener = onInteractionListener;
        ButterKnife.bind(this, itemView);
    }

    public void bindBookingCardViewModel(
            @NonNull final ProTeamPro proTeamPro,
            @NonNull ProviderMatchPreference providerMatchPreference
    )
    {
        mProTeamProViewModel = ProTeamProViewModel.from(proTeamPro);
        mTitle.setText(mProTeamProViewModel.getTitle());
        mSubtitle.setText(mProTeamProViewModel.getSubtitle());
        mFooter.setText(mProTeamProViewModel.getFooter());
        switch (providerMatchPreference)
        {
            case PREFERRED:
                mXButton.setVisibility(View.VISIBLE);
                mCheckbox.setVisibility(View.GONE);
                break;
            case INDIFFERENT:
                mXButton.setVisibility(View.GONE);
                mCheckbox.setVisibility(View.VISIBLE);
                break;
        }
    }

    @OnClick(R.id.pro_team_pro_card_x)
    void onXClicked(View view)
    {
        if (mOnInteractionListener != null)
        {
            mOnInteractionListener.onXClicked(mProTeamProViewModel.getProTeamPro());
        }
    }

    @OnCheckedChanged(R.id.pro_team_pro_card_checkbox)
    void onCheckedChanged(boolean checked)
    {
        mProTeamProViewModel.setChecked(checked);
        if (mOnInteractionListener != null)
        {
            mOnInteractionListener.onCheckedChanged(mProTeamProViewModel.getProTeamPro(), checked);
        }

    }
}

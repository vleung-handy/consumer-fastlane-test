package com.handybook.handybook.module.proteam.holder;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.module.proteam.viewmodel.ProTeamProViewModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProTeamProHolder extends RecyclerView.ViewHolder
    implements CompoundButton.OnCheckedChangeListener
{
    private ProTeamProViewModel mProTeamProViewModel;
    private ProTeamProViewModel.OnInteractionListener mOnInteractionListener;
    @Bind(R.id.pro_team_pro_card_pretext)
    TextView mPretext;
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


    public ProTeamProHolder(
            View itemView,
            ProTeamProViewModel.OnInteractionListener onInteractionListener
    )
    {
        super(itemView);
        mOnInteractionListener = onInteractionListener;
        ButterKnife.bind(this, itemView);
    }

    public void bindProTeamProViewModel(
            @NonNull final ProTeamProViewModel proTeamProViewModel
    )
    {
        mCheckbox.setOnCheckedChangeListener(null);
        mProTeamProViewModel = proTeamProViewModel;
        mTitle.setText(mProTeamProViewModel.getTitle());
        mSubtitle.setText(mProTeamProViewModel.getSubtitle());
        mSubtitle.setVisibility(mProTeamProViewModel.isSubtitleVisible() ? View.VISIBLE : View.GONE);
        mFooter.setText(mProTeamProViewModel.getFooter());
        mFooter.setVisibility(mProTeamProViewModel.isFooterVisible() ? View.VISIBLE : View.GONE);
        mCheckbox.setChecked(mProTeamProViewModel.isChecked());
        mCheckbox.setOnCheckedChangeListener(this);
        initTextColors();
    }

    public void showPretext()
    {
        mPretext.setVisibility(View.VISIBLE);
    }

    public void hidePretext()
    {
        mPretext.setVisibility(View.GONE);
    }

    @OnClick(R.id.pro_team_pro_card_x)
    void onXClicked()
    {
        if (mOnInteractionListener != null)
        {
            mOnInteractionListener.onXClicked(mProTeamProViewModel.getProTeamPro(), mProTeamProViewModel.getProviderMatchPreference());
        }
    }

    private void initTextColors()
    {
        if (mProTeamProViewModel.isChecked())
        {
            final int blackColor = ContextCompat.getColor(
                    mTitle.getContext(),
                    R.color.handy_text_black
            );
            mTitle.setTextColor(blackColor);
            mSubtitle.setTextColor(blackColor);
        }
        else
        {
            final int greyColor = ContextCompat.getColor(
                    mTitle.getContext(),
                    R.color.handy_text_gray
            );
            mTitle.setTextColor(greyColor);
            mSubtitle.setTextColor(greyColor);
        }
    }

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked)
    {
        mProTeamProViewModel.setChecked(isChecked);
        initTextColors();
        if (mOnInteractionListener != null)
        {
            mOnInteractionListener.onCheckedChanged(mProTeamProViewModel.getProTeamPro(), isChecked);
        }
    }
}

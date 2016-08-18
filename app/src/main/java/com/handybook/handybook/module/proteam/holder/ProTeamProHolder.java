package com.handybook.handybook.module.proteam.holder;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.module.proteam.viewmodel.ProTeamProViewModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProTeamProHolder extends RecyclerView.ViewHolder
        implements CompoundButton.OnCheckedChangeListener
{
    private Context mContext;
    private ProTeamProViewModel mProTeamProViewModel;
    private ProTeamProViewModel.OnInteractionListener mOnInteractionListener;
    @Bind(R.id.pro_team_pro_card_pretext)
    TextView mPretext;
    @Bind(R.id.pro_team_pro_card_pro_title)
    TextView mTitle;
    @Bind(R.id.pro_team_pro_card_pro_footer)
    TextView mFooter;
    @Bind(R.id.pro_team_pro_card_checkbox)
    CheckBox mCheckbox;
    @Bind(R.id.pro_team_rating_bar)
    RatingBar mProTeamRatingBar;
    @Bind(R.id.booking_count_text)
    TextView mBookingCountText;
    @Bind(R.id.pro_team_pro_card_x)
    ImageButton mXButton;


    public ProTeamProHolder(
            Context context,
            View itemView,
            ProTeamProViewModel.OnInteractionListener onInteractionListener
    )
    {
        super(itemView);
        mContext = context;
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
        mFooter.setText(mProTeamProViewModel.getLastSeenFooter());
        mFooter.setVisibility(mProTeamProViewModel.isFooterVisible() ? View.VISIBLE : View.GONE);
        mCheckbox.setChecked(mProTeamProViewModel.isChecked());
        mCheckbox.setOnCheckedChangeListener(this);

        // Make rating bar yellow
        mProTeamRatingBar.getProgressDrawable().mutate()
                .setColorFilter(ContextCompat.getColor(mContext, R.color.handy_yellow), PorterDuff.Mode.SRC_ATOP);
        final Float averageRating = mProTeamProViewModel.getAverageRating();
        if (averageRating != null)
        {
            mProTeamRatingBar.setRating(averageRating);
        }
        else
        {
            mProTeamRatingBar.setVisibility(View.INVISIBLE);
        }

        mBookingCountText.setText(mProTeamProViewModel.getBookingFooter());
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
        }
        else
        {
            final int greyColor = ContextCompat.getColor(
                    mTitle.getContext(),
                    R.color.handy_text_gray
            );
            mTitle.setTextColor(greyColor);
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

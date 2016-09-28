package com.handybook.handybook.module.proteam.holder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.module.proteam.viewmodel.ProTeamProViewModel;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

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
    @Bind(R.id.pro_team_pro_card_pro_footer)
    TextView mFooter;
    @Bind(R.id.pro_team_pro_card_checkbox)
    CheckBox mCheckbox;
    @Bind(R.id.pro_average_rating_layout)
    ViewGroup mProAverageRatingLayout;
    @Bind(R.id.average_rating_text)
    TextView mProAverageRating;
    @Nullable
    @Bind(R.id.booking_count_text)
    TextView mBookingCountText;
    @Nullable
    @Bind(R.id.pro_team_pro_image)
    ImageView mProImage;

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
        mFooter.setText(mProTeamProViewModel.getLastSeenFooter());
        mFooter.setVisibility(mProTeamProViewModel.isFooterVisible() ? View.VISIBLE : View.GONE);
        mCheckbox.setChecked(mProTeamProViewModel.isChecked());
        mCheckbox.setOnCheckedChangeListener(this);

        Float averageRating = mProTeamProViewModel.getAverageRating();
        if (averageRating != null)
        {
            averageRating /= 2; // Average rating is out of 10, needs to be out of 5
            DecimalFormat df = new DecimalFormat();
            df.setMinimumFractionDigits(1);
            df.setMaximumFractionDigits(1);
            mProAverageRating.setText(df.format(averageRating));
        }
        else
        {

            mProAverageRatingLayout.setVisibility(View.INVISIBLE);
        }

        if (mBookingCountText != null)
        {
            mBookingCountText.setText(mProTeamProViewModel.getBookingFooter());
        }

        final String imageUrl = mProTeamProViewModel.getImageUrl();
        if (mProImage != null)
        {
            if (imageUrl != null)
            {
                Picasso.with(mProImage.getContext())
                       .load(imageUrl)
                       .placeholder(R.drawable.img_pro_placeholder)
                       .noFade()
                       .into(mProImage);
            }
            else
            {
                mProImage.setImageResource(R.drawable.img_pro_placeholder);
            }
        }
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

    @Nullable
    @OnClick(R.id.pro_team_pro_card_x)
    void onXClicked()
    {
        if (mOnInteractionListener != null)
        {
            mOnInteractionListener.onXClicked(
                    mProTeamProViewModel.getProTeamPro(),
                    mProTeamProViewModel.getProviderMatchPreference()
            );
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
            mOnInteractionListener.onCheckedChanged(
                    mProTeamProViewModel.getProTeamPro(),
                    isChecked
            );
        }
    }
}

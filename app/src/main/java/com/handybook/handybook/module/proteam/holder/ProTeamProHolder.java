package com.handybook.handybook.module.proteam.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.module.proteam.model.ProTeamPro;
import com.handybook.handybook.module.proteam.viewmodel.ProTeamProViewModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProTeamProHolder extends RecyclerView.ViewHolder
{
    private ProTeamProViewModel mProTeamProViewModel;
    private ProTeamProViewModel.OnClickXListener mOnXClickedListener;
    @Bind(R.id.pro_team_pro_card_pro_title)
    TextView mTitle;
    @Bind(R.id.pro_team_pro_card_pro_subtitle)
    TextView mSubtitle;
    @Bind(R.id.pro_team_pro_card_pro_footer)
    TextView mFooter;


    public ProTeamProHolder(View itemView, ProTeamProViewModel.OnClickXListener onXClickedListener)
    {
        super(itemView);
        mOnXClickedListener = onXClickedListener;
        ButterKnife.bind(this, itemView);
    }

    public void bindBookingCardViewModel(@NonNull final ProTeamPro proTeamPro)
    {
        mProTeamProViewModel = ProTeamProViewModel.from(proTeamPro);
        mTitle.setText(mProTeamProViewModel.getTitle());
        mSubtitle.setText(mProTeamProViewModel.getSubtitle());
        mFooter.setText(mProTeamProViewModel.getFooter());
    }

    @OnClick(R.id.pro_team_pro_card_x)
    void onXClicked(View view)
    {
        mOnXClickedListener.onXClicked(mProTeamProViewModel.getProTeamPro());
    }


}

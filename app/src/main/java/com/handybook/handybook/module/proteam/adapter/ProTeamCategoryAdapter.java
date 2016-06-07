package com.handybook.handybook.module.proteam.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.module.proteam.holder.ProTeamProHolder;
import com.handybook.handybook.module.proteam.model.ProTeamPro;

import java.util.List;

public class ProTeamCategoryAdapter extends RecyclerView.Adapter<ProTeamProHolder>
{

    private Context mContext;
    private List<ProTeamPro> mProTeamPros;

    public ProTeamCategoryAdapter(
            @NonNull final Context context,
            @NonNull final List<ProTeamPro> proTeamPros
    )
    {
        mContext = context;
        mProTeamPros = proTeamPros;
    }

    @Override
    public ProTeamProHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View itemView = LayoutInflater
                .from(mContext)
                .inflate(R.layout.layout_pro_team_pro_card, parent, false);
        return new ProTeamProHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProTeamProHolder holder, int position)
    {
        final ProTeamPro proTeamPro = mProTeamPros.get(position);
        holder.bindBookingCardViewModel(proTeamPro);
    }

    @Override
    public int getItemCount()
    {
        return mProTeamPros.size();
    }
}

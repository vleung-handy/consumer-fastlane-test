package com.handybook.handybook.proteam.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.fragment.SlideUpDialogFragment;
import com.handybook.handybook.proteam.ui.view.ProTeamActionPickerItem;
import com.handybook.handybook.proteam.viewmodel.ProTeamActionPickerViewModel;
import com.handybook.handybook.proteam.viewmodel.ProTeamActionPickerViewModel.ActionType;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.OnClick;

public class ProTeamActionPickerDialogFragment extends SlideUpDialogFragment
{
    private static final String KEY_VIEW_MODEL = "pro_team_action_picker_view_model";

    @Bind(R.id.pro_image)
    ImageView mProImage;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.subtitle)
    TextView mSubtitle;
    @Bind(R.id.options_holder)
    ViewGroup mOptionsHolder;
    private ProTeamActionPickerViewModel mViewModel;

    public static ProTeamActionPickerDialogFragment newInstance(
            @NonNull ProTeamActionPickerViewModel viewModel
    )
    {
        final ProTeamActionPickerDialogFragment dialogFragment =
                new ProTeamActionPickerDialogFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(KEY_VIEW_MODEL, viewModel);
        dialogFragment.setArguments(arguments);
        return dialogFragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mViewModel = (ProTeamActionPickerViewModel) getArguments().getSerializable(KEY_VIEW_MODEL);
    }

    @Override
    protected View inflateContentView(
            final LayoutInflater inflater, final ViewGroup container
    )
    {
        return inflater.inflate(R.layout.layout_pro_team_action_picker, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mTitle.setText(mViewModel.getTitle());

        final String subtitle = mViewModel.getSubtitle();
        mSubtitle.setText(subtitle);
        mSubtitle.setVisibility(TextUtils.isEmpty(subtitle) ? View.GONE : View.VISIBLE);

        Picasso.with(getContext())
               .load(mViewModel.getProImageUrl())
               .placeholder(R.drawable.img_pro_placeholder)
               .noFade()
               .into(mProImage);

        for (final ActionType actionType : mViewModel.getActionTypes())
        {
            final ProTeamActionPickerItem actionPickerItem = new ProTeamActionPickerItem(
                    getActivity(),
                    actionType.getStringResId()
            );
            mOptionsHolder.addView(actionPickerItem, mOptionsHolder.getChildCount() - 1);
            actionPickerItem.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View view)
                {
                    final Fragment targetFragment = getTargetFragment();
                    if (targetFragment != null)
                    {
                        final Intent data = new Intent().putExtra(
                                BundleKeys.PRO_TEAM_PRO_ID,
                                mViewModel.getProId()
                        ).putExtra(
                                BundleKeys.EDIT_PRO_TEAM_PREFERENCE_ACTION_TYPE,
                                actionType
                        );
                        targetFragment.onActivityResult(
                                getTargetRequestCode(),
                                Activity.RESULT_OK,
                                data
                        );
                    }
                    dismiss();
                }
            });
        }
    }

    @OnClick(R.id.cancel)
    public void onCancelClicked()
    {
        dismiss();
    }
}

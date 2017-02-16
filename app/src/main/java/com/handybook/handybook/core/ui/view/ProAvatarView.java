package com.handybook.handybook.core.ui.view;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.proteam.viewmodel.ProTeamProViewModel;
import com.layer.atlas.util.picasso.transformations.CircleTransform;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class ProAvatarView extends RelativeLayout
{
    @Bind(R.id.avatar_image_profile)
    CircleImageView mCircleImageView;

    @Bind(R.id.avatar_heart_container)
    FrameLayout mHeartContainer;

    public ProAvatarView(final Context context, final int mImageSize)
    {
        super(context);
        init();

        if (mImageSize > 0)
        {
            mCircleImageView.setLayoutParams(new LayoutParams(mImageSize, mImageSize));
        }
    }

    public ProAvatarView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public ProAvatarView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.layout_pro_avatar, this);
        ButterKnife.bind(this);
    }

    /**
     * Displays the image, and the heart icon depending on whether it is preferred or not
     *
     * @param pro
     */
    public void bindPro(ProTeamProViewModel pro)
    {
        mHeartContainer.setVisibility(pro.isFavorite() ? VISIBLE : GONE);

        Picasso.with(mCircleImageView.getContext())
               .load(pro.getImageUrl())
               .placeholder(R.drawable.img_pro_placeholder)
               .noFade()
               .transform(new CircleTransform(pro.getImageUrl()))
               .into(mCircleImageView);
    }

    public void setHeartContainerBackground(@DrawableRes final int drawableResId)
    {
        mHeartContainer.setBackgroundResource(drawableResId);
    }
}

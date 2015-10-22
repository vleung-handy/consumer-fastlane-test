package com.handybook.handybook.ui.widget;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Service;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class ServiceCategoryView extends FrameLayout
{
    @Bind(R.id.card)
    CardView mCard;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.subtitle)
    TextView mSubtitle;
    @Bind(R.id.icon)
    ImageView mIcon;
    @Bind(R.id.image)
    ImageView mImage;

    public ServiceCategoryView(final Context context)
    {
        super(context);
    }

    ServiceCategoryView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    ServiceCategoryView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void init(final Service service)
    {
        LayoutInflater.from(getContext()).inflate(R.layout.view_service_category, this);
        ButterKnife.bind(this);

        mCard.setPreventCornerOverlap(false); // this doesn't work when specified in the XML
        try
        {
            String serviceMachineName = service.getUniq().toUpperCase();
            ServiceCategoryViewType viewType = ServiceCategoryViewType.valueOf(serviceMachineName);
            mTitle.setText(viewType.getTitleString());
            mSubtitle.setText(viewType.getSubtitleString());
            mIcon.setImageResource(viewType.getIconDrawable());
            mImage.setImageResource(viewType.getImageDrawable());
        } catch (IllegalArgumentException e)
        {
            mTitle.setText(service.getName());
            mImage.setVisibility(View.GONE);
        }
    }

    private enum ServiceCategoryViewType
    {
        HOME_CLEANING(R.string.home_cleaner, R.string.home_cleaner_slogan, R.drawable.ic_cleaner_fill, R.drawable.img_cleaner),
        HANDYMAN(R.string.handyman, R.string.handyman_slogan, R.drawable.ic_handyman_fill, R.drawable.img_handyman),
        PLUMBING(R.string.plumber, R.string.plumber_slogan, R.drawable.ic_plumber_fill, R.drawable.img_plumber),
        ELECTRICIAN(R.string.electrician, R.string.electrician_slogan, R.drawable.ic_electrician_fill, R.drawable.img_electrician),
        PAINTING(R.string.painter, R.string.painter_slogan, R.drawable.ic_painter_fill, R.drawable.img_painter),;

        private final int mTitleString;
        private final int mSubtitleString;
        private final int mIconDrawable;
        private final int mImageDrawable;

        ServiceCategoryViewType(int titleString, int subtitleString, int iconDrawable, int imageDrawable)
        {

            mTitleString = titleString;
            mSubtitleString = subtitleString;
            mIconDrawable = iconDrawable;
            mImageDrawable = imageDrawable;
        }

        public int getTitleString()
        {
            return mTitleString;
        }

        public int getSubtitleString()
        {
            return mSubtitleString;
        }

        public int getIconDrawable()
        {
            return mIconDrawable;
        }

        public int getImageDrawable()
        {
            return mImageDrawable;
        }
    }
}

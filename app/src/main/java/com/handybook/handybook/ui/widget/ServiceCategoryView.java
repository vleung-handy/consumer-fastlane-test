package com.handybook.handybook.ui.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Service;
import com.handybook.handybook.ui.descriptor.ServiceCategoryListDescriptor;
import com.handybook.handybook.ui.transformation.RoundedTransformation;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class ServiceCategoryView extends FrameLayout
{
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

        try
        {
            String serviceMachineName = service.getUniq().toUpperCase();
            ServiceCategoryListDescriptor descriptor = ServiceCategoryListDescriptor.valueOf(serviceMachineName);
            mTitle.setText(descriptor.getTitleString());
            mSubtitle.setText(descriptor.getSubtitleString());
            mIcon.setImageResource(descriptor.getIconDrawable());
            int currentAPIVersion = android.os.Build.VERSION.SDK_INT;
            // Due to expensive nature of rounded corner clipping, on platforms before L, CardView
            // does not clip its children that intersect with rounded corners.
            if (currentAPIVersion >= Build.VERSION_CODES.LOLLIPOP)
            {
                mImage.setImageResource(descriptor.getImageDrawable());
            } else
            {
                Picasso.with(getContext())
                        .load(descriptor.getImageDrawable())
                        .transform(
                                new RoundedTransformation(
                                        getContext().getResources()
                                                .getDimension(R.dimen.default_corner_radius),
                                        0,
                                        true,
                                        true,
                                        false,
                                        false
                                )
                        )
                        .fit()
                        .into(mImage);
            }
        } catch (IllegalArgumentException e)
        {
            mTitle.setText(service.getName());
            mImage.setVisibility(View.GONE);
        }
    }

    public ImageView getIcon()
    {
        return mIcon;
    }

}

package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.ui.descriptor.ServiceCategoryListDescriptor;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


public class ServiceOptionView extends TableRow
{
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.icon)
    ImageView mIcon;

    public ServiceOptionView(final Context context)
    {
        super(context);
    }

    public ServiceOptionView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void init(Service service)
    {
        TableLayout.LayoutParams layoutParams =
                new TableLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0,
                Math.round(getResources().getDimension(R.dimen.default_margin)));
        setLayoutParams(layoutParams);
        setGravity(Gravity.CENTER);
        setVisibility(INVISIBLE);

        LayoutInflater.from(getContext()).inflate(R.layout.view_service_option, this);
        ButterKnife.bind(this);

        ServiceCategoryListDescriptor serviceDescriptor =
                ServiceCategoryListDescriptor.valueOf(service.getUniq().toUpperCase());
        mTitle.setText(serviceDescriptor.getTitleString());
        mIcon.setImageResource(serviceDescriptor.getIconDrawable());
    }

    public ImageView getIcon()
    {
        return mIcon;
    }
}

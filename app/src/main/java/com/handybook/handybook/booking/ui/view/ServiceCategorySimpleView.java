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


public class ServiceCategorySimpleView extends TableRow
{
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.icon)
    ImageView mIcon;

    public ServiceCategorySimpleView(final Context context)
    {
        super(context);
        setProperties();
    }

    public ServiceCategorySimpleView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        setProperties();
    }

    private void setProperties()
    {
        TableLayout.LayoutParams layoutParams =
                new TableLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0,
                Math.round(getResources().getDimension(R.dimen.default_margin)));
        setLayoutParams(layoutParams);
        setGravity(Gravity.CENTER);
        setVisibility(INVISIBLE);
    }

    public void init(Service service)
    {
        LayoutInflater.from(getContext()).inflate(R.layout.view_service_category_simple, this);
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

    public TextView getTitle()
    {
        return mTitle;
    }
}

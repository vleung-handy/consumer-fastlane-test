package com.handybook.handybook.booking.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.ui.descriptor.ServiceDescriptor;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServiceView extends FrameLayout {

    @BindView(R.id.icon)
    ImageView mIcon;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.subtitle)
    TextView mSubtitle;

    public ServiceView(Context context) {
        super(context);
    }

    public ServiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ServiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ServiceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(final ServiceDescriptor serviceDescriptor) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_service, this);
        ButterKnife.bind(this);

        mIcon.setImageResource(serviceDescriptor.getIcon());
        mTitle.setText(serviceDescriptor.getTitle());
        mSubtitle.setText(serviceDescriptor.getDescription());
    }

}

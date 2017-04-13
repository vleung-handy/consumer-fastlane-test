package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProBusyView extends FrameLayout {

    @Bind(R.id.pro_busy_profile_image)
    ImageView mProfileImage;
    @Bind(R.id.pro_busy_text)
    TextView mProBusyText;
    @Bind(R.id.pro_busy_availability_text)
    TextView mAvailabilityText;

    public ProBusyView(final Context context) {
        super(context);
        init();
    }

    public ProBusyView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProBusyView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ProBusyView(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr,
            final int defStyleRes
    ) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_pro_busy, this);
        ButterKnife.bind(this);
    }

    public void setDisplay(
            @NonNull String imageUrl,
            @NonNull String proName,
            @NonNull OnClickListener onAvailabilityClicked
    ) {
        Picasso.with(getContext())
               .load(imageUrl)
               .placeholder(R.drawable.img_pro_placeholder)
               .noFade()
               .into(mProfileImage);
        String message = getResources().getString(
                R.string.pro_is_busy_formatted,
                "<b>" + proName + "</b> "
        );
        mProBusyText.setText(Html.fromHtml(message));
        mAvailabilityText.setOnClickListener(onAvailabilityClicked);
    }

    public String getMessage() {
        return mProBusyText.getText().toString();
    }
}

package com.handybook.handybook.library.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handybook.handybook.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TitleView extends FrameLayout {

    @BindView(R.id.title)
    TextView mTitle;

    public TitleView(final Context context) {
        super(context);
        init();
    }

    public TitleView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TitleView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TitleView(
            final Context context, final AttributeSet attrs, final int defStyleAttr,
            final int defStyleRes
    ) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_title, this);
        ButterKnife.bind(this);
    }

    public void setText(@StringRes final int textResId) {
        mTitle.setText(textResId);
    }
}

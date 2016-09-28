package com.handybook.handybook.account.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.handybook.handybook.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FrequencyOptionView extends FrameLayout
{
    @Bind(R.id.frequency_option_radio)
    RadioButton mRadioButton;
    @Bind(R.id.frequency_option_title_text)
    TextView mTitleText;
    @Bind(R.id.frequency_option_subtitle_text)
    TextView mCurrentText;
    @Bind(R.id.frequency_option_right_title_text)
    TextView mPriceText;

    private List<FrequencyOptionView> mAllFrequencyOptions;
    private int mFrequency;

    public FrequencyOptionView(final Context context)
    {
        super(context);
        init();
    }

    public FrequencyOptionView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public FrequencyOptionView(
            final Context context, final AttributeSet attrs, final int defStyleAttr
    )
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FrequencyOptionView(
            final Context context, final AttributeSet attrs,
            final int defStyleAttr, final int defStyleRes
    )
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.view_frequency_select_option, this);
        ButterKnife.bind(this);
        mRadioButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                setChecked(true);
                unckeckOtherViews();
            }
        });
    }

    public void setProperty(
            List<FrequencyOptionView> radioButtonGroup,
            int frequency,
            String frequencyText,
            String price,
            boolean isCurrent
    )
    {
        mAllFrequencyOptions = radioButtonGroup;
        mFrequency = frequency;
        mTitleText.setText(frequencyText);
        mPriceText.setText(price);
        mCurrentText.setVisibility(isCurrent ? VISIBLE : GONE);
        setChecked(isCurrent);

    }

    public void unckeckOtherViews()
    {
        if (mAllFrequencyOptions == null) { return; }
        for (FrequencyOptionView frequencyOptionView : mAllFrequencyOptions)
        {
            if (frequencyOptionView != this)
            {
                frequencyOptionView.setChecked(false);
            }
        }
    }

    public void setChecked(boolean checked)
    {
        mRadioButton.setChecked(checked);
        int black = ContextCompat.getColor(getContext(), R.color.black);
        int gray = ContextCompat.getColor(getContext(), R.color.black_pressed);
        int blue = ContextCompat.getColor(getContext(), R.color.handy_blue);
        mTitleText.setTextColor(checked ? black : gray);
        mPriceText.setTextColor(checked ? black : gray);
        mCurrentText.setTextColor(checked ? blue : gray);
    }

    public boolean isChecked() { return mRadioButton.isChecked(); }

    public int getFrequency() { return mFrequency; }
}

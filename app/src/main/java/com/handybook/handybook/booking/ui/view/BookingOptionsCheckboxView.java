package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingOptionsCheckboxView extends FrameLayout implements
        CompoundButton.OnCheckedChangeListener {

    @Bind(R.id.booking_select_checkbox_left_title)
    TextView mLeftTitle;
    @Bind(R.id.booking_select_checkbox_left_text)
    TextView mLeftText;
    @Bind(R.id.booking_select_checkbox_right_title)
    TextView mRightTitle;
    @Bind(R.id.booking_select_checkbox_right_text)
    TextView mRightText;
    @Bind(R.id.booking_select_checkbox_checkbox)
    CheckBox mCheckBox;
    @Bind(R.id.booking_select_checkbox_left_indicator)
    ImageView mLeftIndicator;
    @Bind(R.id.booking_select_checkbox_super_text)
    TextView mSuperText;
    private CompoundButton.OnCheckedChangeListener mOutsideListener;

    public BookingOptionsCheckboxView(final Context context) {
        super(context);
        init(null, 0, 0);
    }

    public BookingOptionsCheckboxView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public BookingOptionsCheckboxView(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BookingOptionsCheckboxView(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr,
            final int defStyleRes
    ) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    private void init(final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        setSaveEnabled(true);
        inflate(getContext(), R.layout.view_booking_option_checkbox, this);
        ButterKnife.bind(this);
        mCheckBox.setOnCheckedChangeListener(this);
        if (isInEditMode()) {
            demo();
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.leftTitle = (String) mLeftTitle.getText();
        savedState.leftText = (String) mLeftText.getText();
        savedState.rightTitle = (String) mRightTitle.getText();
        savedState.rightText = (String) mRightText.getText();
        savedState.isChecked = mCheckBox.isChecked();
        savedState.isIndicatorVisible = mLeftIndicator.getVisibility() == VISIBLE;
        savedState.superText = (String) mSuperText.getText();
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(final Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        setChecked(savedState.isChecked);

    }

    private void demo() {
        setLeftTitle("Left Title");
        setLeftText("Left text\non two lines ");
        setRightTitle("Right title");
        setRightText("Right text");
        setChecked(true);
        setLeftIndicatorVisible(true);
        setSuperText("Supertext");
    }

    public void setLeftTitle(final String text) {
        setTextView(mLeftTitle, text);
    }

    public void setLeftText(final String text) {
        setTextView(mLeftText, text);
    }

    public void setRightTitle(final String text) {
        setTextView(mRightTitle, text);
    }

    public void setRightText(final String text) {
        setTextView(mRightText, text);
    }

    public void setChecked(final boolean checked) {
        mCheckBox.setChecked(checked);
    }

    public boolean isChecked() {
        return mCheckBox.isChecked();
    }

    public void setLeftIndicatorVisible(final boolean isVisible) {
        mLeftIndicator.setVisibility(isVisible ? VISIBLE : GONE);
    }

    public void setSuperText(final String text) {
        setTextView(mSuperText, text);
    }

    public void setOnCheckedChangeListener(
            @Nullable CompoundButton.OnCheckedChangeListener listener
    ) {
        mOutsideListener = listener;
    }

    private void setTextView(@NonNull TextView textView, @Nullable String text) {
        textView.setText(text);
        if (text == null) {
            textView.setVisibility(GONE);
        }
        else {
            textView.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
        ((ViewGroup) getRootView()).dispatchSetSelected(isChecked);
        if (mOutsideListener != null) {
            mOutsideListener.onCheckedChanged(buttonView, isChecked);
        }
    }

    private static class SavedState extends BaseSavedState {

        String leftTitle;
        String leftText;
        String rightTitle;
        String rightText;
        boolean isChecked;
        boolean isIndicatorVisible;
        String superText;

        SavedState(final Parcelable superState) {
            super(superState);
        }

        SavedState(final Parcel source) {
            super(source);
            leftTitle = source.readString();
            leftText = source.readString();
            rightTitle = source.readString();
            rightText = source.readString();
            isChecked = source.readByte() == 1;
            isIndicatorVisible = source.readByte() == 1;
            superText = source.readString();
        }

        @Override
        public void writeToParcel(final Parcel out, final int flags) {
            super.writeToParcel(out, flags);
            out.writeString(leftTitle);
            out.writeString(leftText);
            out.writeString(leftTitle);
            out.writeString(leftText);
            out.writeByte((byte) (isChecked ? 1 : 0));
            out.writeByte((byte) (isIndicatorVisible ? 1 : 0));
            out.writeString(superText);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(final Parcel source) {
                return new BookingOptionsCheckboxView.SavedState(source);
            }

            @Override
            public SavedState[] newArray(final int size) {
                return new SavedState[size];
            }
        };
    }


}

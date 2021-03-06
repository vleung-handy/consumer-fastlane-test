package com.handybook.handybook.booking.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingInstruction;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookingInstructionView extends FrameLayout {

    BookingInstruction mBookingInstruction;

    private State mState;
    private OnStateChangedListener mOnStateChangedListener;
    private AppCompatCheckBox.OnCheckedChangeListener mCheckboxCheckedChangeListener;
    private String mTitle;
    private String mText;

    @BindView(R.id.customer_preference_checkbox)
    AppCompatCheckBox mCheckBox;
    @BindView(R.id.customer_preference_text)
    TextView mTextView;

    {
        mCheckboxCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                setState(isChecked ? State.REQUESTED : State.DECLINED);
            }
        };
    }

    public BookingInstructionView(final Context context) {
        super(context);
        init();
    }

    public BookingInstructionView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BookingInstructionView(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public State getState() {
        return mState;
    }

    public void setState(final State state) {
        mState = state;
        if (mBookingInstruction != null) {
            mBookingInstruction.setIsRequested(state == State.REQUESTED);
        }
        mCheckBox.setChecked(state == State.REQUESTED);
        notifyObserver();
    }

    private void notifyObserver() {
        if (mOnStateChangedListener != null) {
            mOnStateChangedListener.onStateChanged(mBookingInstruction);
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(final String title) {
        mTitle = title;
        updateText();
    }

    public String getText() {
        return mText;
    }

    public void setText(final String text) {
        mText = text;
    }

    private void init() {
        inflateAndBind();
    }

    private void init(AttributeSet attrs) {
        inflateAndBind();
        TypedArray typedArray = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.BookingInstructionView
        );
        try {
            mState = State.fromId(typedArray.getInt(
                    R.styleable.BookingInstructionView_prefsState,
                    0
            ));
            mTitle = typedArray.getString(R.styleable.BookingInstructionView_prefsTitle);
            mText = typedArray.getString(R.styleable.BookingInstructionView_prefsText);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            typedArray.recycle();
        }
        updateStateImage();
        updateText();
    }

    public void setOnStateChangedListener(OnStateChangedListener listener) {
        mOnStateChangedListener = listener;
    }

    private void inflateAndBind() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_booking_instruction, this, true);
        ButterKnife.bind(this);
        mCheckBox.setOnCheckedChangeListener(mCheckboxCheckedChangeListener);
    }

    private void updateStateImage() {
        switch (mState) {
            case REQUESTED:
                mCheckBox.setChecked(true);
                return;
            case DECLINED:
                mCheckBox.setChecked(false);
                return;

        }
        invalidate();
        requestLayout();

    }

    private void updateText() {
        mTextView.setText(Html.fromHtml("<b>" + mTitle + ":</b> " + mText));
        invalidate();
        requestLayout();
    }

    public void reflect(BookingInstruction bookingInstruction) {
        mBookingInstruction = bookingInstruction;
        setText(bookingInstruction.getDescription());
        setTitle(bookingInstruction.getTitle());
        setState(bookingInstruction.getIsRequested() ? State.REQUESTED : State.DECLINED);
    }

    public BookingInstruction getBookingInstruction() {
        return mBookingInstruction;
    }

    public enum State {
        REQUESTED(0),
        DECLINED(1);

        int mId;

        State(final int id) {
            mId = id;
        }

        static State fromId(final int id) {
            for (State eachState : values()) {
                if (eachState.mId == id) {
                    return eachState;
                }
            }
            return DECLINED;
        }
    }


    public interface OnStateChangedListener {

        void onStateChanged(BookingInstruction bookingInstruction);
    }
}

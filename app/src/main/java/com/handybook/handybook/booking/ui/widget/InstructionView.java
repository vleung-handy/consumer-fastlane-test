package com.handybook.handybook.booking.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.ChecklistItem;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InstructionView extends FrameLayout
{
    @Bind(R.id.state_image)
    ImageView mStateImage;
    @Bind(R.id.text)
    TextView mTextView;

    private State mState;
    private String mTitle;
    private String mText;


    public InstructionView(final Context context)
    {
        super(context);
        init();
    }

    public InstructionView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs);
    }

    public InstructionView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public State getState()
    {
        return mState;
    }

    public void setState(final State state)
    {
        mState = state;
        updateStateImage();
    }

    public String getTitle()
    {
        return mTitle;
    }

    public void setTitle(final String title)
    {
        mTitle = title;
        updateText();
    }

    public String getText()
    {
        return mText;
    }

    public void setText(final String text)
    {
        mText = text;
    }

    private void init()
    {
        inflateAndBind();
    }

    private void init(AttributeSet attrs)
    {
        inflateAndBind();
        TypedArray typedArray = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.InstructionView
        );
        try
        {
            mState = State.fromId(typedArray.getInt(R.styleable.InstructionView_prefsState, 0));
            mTitle = typedArray.getString(R.styleable.InstructionView_prefsTitle);
            mText = typedArray.getString(R.styleable.InstructionView_prefsText);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            typedArray.recycle();
        }
        updateStateImage();
        updateText();
    }

    private void inflateAndBind()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.view_customer_preference, this, true);
        ButterKnife.bind(this);
    }

    private void updateStateImage()
    {
        switch (mState)
        {
            case DEFAULT:
                mStateImage.setImageResource(R.drawable.ic_check);
                return;
            case DISABLED:
                mStateImage.setImageResource(R.drawable.ic_check_dark);
                return;
            case DONE:
                mStateImage.setImageResource(R.drawable.ic_checkmark);
                return;
            case REQUESTED:
                mStateImage.setImageResource(R.drawable.ic_chevron);
                return;
            case IN_PROGRESS:
                mStateImage.setImageResource(R.drawable.com_mixpanel_android_ic_clipboard_checkmark);
                return;

        }
        invalidate();
        requestLayout();

    }

    private void updateText()
    {
        mTextView.setText(Html.fromHtml("<b>" + mTitle + ":</b> " + mText));
        invalidate();
        requestLayout();
    }

    public void reflect(ChecklistItem checklistItem)
    {
        setText(checklistItem.getText());
        setTitle(checklistItem.getTitle());
    }

    public enum State
    {
        DEFAULT(0),
        DISABLED(1),
        REQUESTED(2),
        IN_PROGRESS(3),
        DONE(4);

        int mId;


        State(final int id)
        {
            mId = id;
        }

        static State fromId(final int id)
        {
            for (State eachState : values())
            {
                if (eachState.mId == id)
                {
                    return eachState;
                }
            }
            return DEFAULT;
        }
    }
}

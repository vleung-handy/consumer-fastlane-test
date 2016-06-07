package com.handybook.handybook.module.proteam.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handybook.handybook.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A dialog that animates in from bottom up, prompting for comfirmation to remove a pro
 */

public class RemoveProDialogFragment extends DialogFragment
{
    @Bind(R.id.remove_pro_title)
    TextView mTextTitle;

    @Bind(R.id.remove_pro_main_container)
    RelativeLayout mMainContainer;

    @Bind(R.id.remove_pro_cancel)
    ImageButton mCancelButton;

    private String mTitle;
    private RemoveProListener mListener;


    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_remove_pro_dialog, container, false);
        ButterKnife.bind(this, view);

        mTextTitle.setText(mTitle);
        extendCancelButtonClickArea();
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.getAttributes().windowAnimations = R.style.dialog_animation_slide_up_down_from_bottom;

        return dialog;
    }

    /**
     * Since the little "X" on the top right corner is so small, this increases it's "clickable" area
     */
    private void extendCancelButtonClickArea()
    {
        mMainContainer.post(new Runnable()
        {
            // Post in the parent's message queue to make sure the parent
            // lays out its children before you call getHitRect()
            @Override
            public void run()
            {
                // The bounds for the delegate view (an ImageButton
                // in this example)
                Rect delegateArea = new Rect();

                // The hit rectangle for the ImageButton
                mCancelButton.getHitRect(delegateArea);

                // Extend the touch area of the ImageButton beyond its bounds
                // on the right and bottom.
                delegateArea.right += 200;
                delegateArea.bottom += 200;
                delegateArea.left -= 200;
                delegateArea.top -= 200;

                // Instantiate a TouchDelegate.
                // "delegateArea" is the bounds in local coordinates of
                // the containing view to be mapped to the delegate view.
                // "myButton" is the child view that should receive motion
                // events.
                TouchDelegate touchDelegate = new TouchDelegate(delegateArea, mCancelButton);

                // Sets the TouchDelegate on the parent view, such that touches
                // within the touch delegate bounds are routed to the child.
                if (View.class.isInstance(mCancelButton.getParent()))
                {
                    ((View) mCancelButton.getParent()).setTouchDelegate(touchDelegate);
                }
            }
        });
    }

    public void setListener(final RemoveProListener listener)
    {
        mListener = listener;
    }

    public void setTitle(final String title)
    {
        mTitle = title;
        if (mTextTitle != null)
        {
            mTextTitle.setText(title);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        wlp.gravity = Gravity.BOTTOM;
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @OnClick(R.id.remove_pro_not_permanent_text)
    public void notPermanentClicked()
    {
        if (mListener != null)
        {
            mListener.onYesNotPermanent();
        }
        dismiss();
    }

    @OnClick(R.id.remove_pro_permanent_text)
    public void permanentClicked()
    {
        if (mListener != null)
        {
            mListener.onYesPermanent();
        }
        dismiss();
    }

    @OnClick(R.id.remove_pro_nevermind_text)
    public void nevermindClicked()
    {
        cancelClicked();
    }

    @OnClick(R.id.remove_pro_cancel)
    public void cancelClicked()
    {
        if (mListener != null)
        {
            mListener.onCancel();
        }

        dismiss();
    }

    @Override
    public void onCancel(final DialogInterface dialog)
    {
        super.onCancel(dialog);
        cancelClicked();
    }

    public interface RemoveProListener
    {
        void onYesNotPermanent();

        void onYesPermanent();

        void onCancel();
    }
}

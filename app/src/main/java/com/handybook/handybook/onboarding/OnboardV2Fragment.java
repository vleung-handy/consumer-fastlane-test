package com.handybook.handybook.onboarding;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewSwitcher;

import com.handybook.handybook.R;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This is the new onboarding fragment that is supposed to
 */
public class OnboardV2Fragment extends InjectedFragment implements AppBarLayout.OnOffsetChangedListener
{
    private static final String TAG = "OnboardV2Fragment";

    // Threshold for minimal keyboard height.
    final int MIN_KEYBOARD_HEIGHT_PX = 150;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.appbar)
    AppBarLayout mAppBar;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @Bind(R.id.collapsed_view)
    View mCollapsedView;

    @Bind(R.id.edit_zip)
    EditText mEditZip;

    @Bind(R.id.edit_email)
    EditText mEditEmail;

    @Bind(R.id.button_signin_1)
    Button mSign1;

    @Bind(R.id.button_signin_2)
    Button mSign2;

    @Bind(R.id.view_switcher)
    ViewSwitcher mViewSwitcher;

    @Bind(R.id.view_email)
    View mEmailView;

    @Bind(R.id.view_zip)
    View mZipView;

    private Animation mSlideInFromRight;
    private Animation mSlideOutToLeft;
    private Animation mSlideOutToRight;
    private Animation mSlideInFromLeft;
    private boolean mExpanded = true;

    public static OnboardV2Fragment newInstance()
    {
        return new OnboardV2Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    )
    {
        View view = inflater.inflate(R.layout.fragment_onboard_v2, container, false);
        ButterKnife.bind(this, view);

        mEditZip.clearFocus();
        mAppBar.addOnOffsetChangedListener(this);
        registerKeyboardListener();

        mSlideInFromRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        mSlideOutToLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);
        mSlideInFromLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
        mSlideOutToRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right);
        return view;

    }

    @OnClick(R.id.button_next)
    public void showNext()
    {
        mViewSwitcher.setInAnimation(mSlideInFromRight);
        mViewSwitcher.setOutAnimation(mSlideOutToLeft);
        mViewSwitcher.showNext();
    }

    private void showPrevious()
    {
        mViewSwitcher.setInAnimation(mSlideInFromLeft);
        mViewSwitcher.setOutAnimation(mSlideOutToRight);
        mViewSwitcher.showPrevious();
    }

    /**
     * Returns true if it's been handled.
     * @return
     */
    public boolean onBackPressed()
    {
        if (mViewSwitcher.getCurrentView() == mEmailView)
        {
            //we just want to go back to zip
            showPrevious();
            return true;
        }
        else
        {
            return false;
        }
    }

    @OnClick(R.id.button_dont_support)
    public void launchNoSupport()
    {
        startActivity(new Intent(getContext(), NotSupportedActivity.class));
    }

    /**
     * Android doesn't have an API for this, this is an approximation
     * https://pspdfkit.com/blog/2016/keyboard-handling-on-android/
     */
    private void registerKeyboardListener()
    {
        // Register global layout listener.
        final View decorView = getActivity().getWindow().getDecorView();
        decorView.getViewTreeObserver()
                 .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
                 {
                     private final Rect windowVisibleDisplayFrame = new Rect();
                     private int lastVisibleDecorViewHeight;

                     @Override
                     public void onGlobalLayout()
                     {
                         // Retrieve visible rectangle inside window.
                         decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame);
                         final int visibleDecorViewHeight = windowVisibleDisplayFrame.height();

                         // Decide whether keyboard is visible from changing decor view height.
                         if (lastVisibleDecorViewHeight != 0)
                         {
                             if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX)
                             {
                                 // Calculate current keyboard height (this includes also navigation bar height when in fullscreen mode).
                                 int currentKeyboardHeight = decorView.getHeight() - windowVisibleDisplayFrame.bottom;
                                 // Notify listener about keyboard being shown.
                                 //                        listener.onKeyboardShown(currentKeyboardHeight);
                                 collapse();
                             }
                             else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight)
                             {
                                 // Notify listener about keyboard being hidden.
                                 expand();
                             }
                         }
                         // Save current decor view height for the next call.
                         lastVisibleDecorViewHeight = visibleDecorViewHeight;
                     }
                 });
    }

    private void expand()
    {
        if (!mExpanded)
        {
            mAppBar.setExpanded(true);
            mExpanded = true;
        }
    }

    private void collapse()
    {
        if (mExpanded)
        {
            mAppBar.setExpanded(false);
            mExpanded = false;
        }
    }

    /**
     * Determines when the toolbar is fully expanded and fully collapsed.
     * @param appBarLayout
     * @param verticalOffset
     */
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset)
    {
        Log.d(TAG, "onOffsetChanged: " + verticalOffset);
        if (verticalOffset == 0)
        {
            //fully expanded
            mCollapsedView.setVisibility(View.GONE);
            mCollapsedView.setAlpha(0);
        }
        else if (mToolbar.getHeight() - mCollapsingToolbar.getHeight() == verticalOffset)
        {
            //fully collapsed
            mCollapsedView.setVisibility(View.VISIBLE);
            mCollapsedView.animate()
                          .alpha(1)
                          .start();
        }
    }
}

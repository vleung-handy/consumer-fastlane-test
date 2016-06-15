package com.handybook.handybook.booking.ui.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.view.ImageToggleButton;
import com.handybook.handybook.booking.ui.view.SwipeableViewPager;
import com.handybook.handybook.module.proteam.model.ProviderMatchPreference;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This is the fragment that holds views for adding / removing / blocking a pro
 */
public class RateProTeamFragment extends Fragment
{

    private static final String RATING_VALUE = "rating-value";
    private static final String MATCH_PREFERENCE = "match-preference";
    private static final String PRO_NAME = "pro-name";
    private static final String TAG_BLOCK_PRO = "block-pro";
    private static final String TAG_ADD_PRO = "add-pro";
    private static final int ANIMATION_WAIT_TIME_MS = 1000;

    @Bind(R.id.toggle_button)
    ImageToggleButton mToggleButton;

    /**
     * Simple container just shows title and then button
     */
    @Bind(R.id.simple_container)
    ViewGroup mSimpleContainer;

    @Bind(R.id.rate_dialog_pro_match_header_txt)
    TextView mTextTitle;

    @Bind(R.id.rate_dialog_pro_match_container)
    ViewGroup mRootContainer;

    @Bind(R.id.pager)
    SwipeableViewPager mPager;

    private ProTeamPagerAdapter mAdapter;

    private String mTitleAddPro;
    private String mButtonTextAddPro;

    private String mTitleBlockPro;
    private String mButtonTextBlockPro;

    private String mTitleRemovePro;
    private String mButtonTextRemovePro;

    private Drawable mActiveAddDrawable;
    private Drawable mInactiveAddDrawable;

    private Drawable mActiveBlockDrawable;
    private Drawable mInactiveBlockDrawable;

    private Drawable mActiveRemoveDrawable;
    private Drawable mInactiveRemoveDrawable;

    private ProviderMatchPreference mInitialMatchPreference;
    private int mRating;
    private String mProName;
    private Handler mHandler = new Handler();

    /**
     * This points to the ImageToggleButton that is clicked from within the view pager
     */
    private ImageToggleButton mCurrentClickedToggleButton;

    public static RateProTeamFragment newInstance(int rating, String proName, ProviderMatchPreference matchPreference)
    {
        final RateProTeamFragment fragment = new RateProTeamFragment();
        final Bundle args = new Bundle();
        args.putInt(RATING_VALUE, rating);
        args.putString(PRO_NAME, proName);
        args.putSerializable(MATCH_PREFERENCE, matchPreference);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_rate_pro_team, container, false);
        ButterKnife.bind(this, v);

        mInitialMatchPreference = (ProviderMatchPreference) getArguments().getSerializable(MATCH_PREFERENCE);
        mRating = getArguments().getInt(RATING_VALUE);
        mProName = getArguments().getString(PRO_NAME);

        mTitleAddPro = getResources().getString(R.string.rate_dialog_pro_match_title_add);
        mButtonTextAddPro = getResources().getString(R.string.rate_dialog_pro_match_add);

        mTitleBlockPro = getResources().getString(R.string.rate_dialog_pro_match_title_block);
        mButtonTextBlockPro = getResources().getString(R.string.rate_dialog_pro_match_block, mProName);

        mTitleRemovePro = getResources().getString(R.string.rate_dialog_pro_match_title_remove);
        mButtonTextRemovePro = getResources().getString(R.string.rate_dialog_pro_match_remove);

        mActiveAddDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_rating_pro_heart_active);
        mInactiveAddDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_rating_pro_heart_inactive);

        mActiveBlockDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_rating_pro_ban_active);
        mInactiveBlockDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_rating_pro_ban_inactive);

        mActiveRemoveDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_rating_pro_remove_active);
        mInactiveRemoveDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_rating_pro_remove_inactive);

        resetLayout();
        return v;
    }

    /**
     * This updates the layout with a new rating, and then sets up the layouts accordingly
     * @param rating
     */
    public void updateWithNewRating(final int rating)
    {
        int previousRating = mRating;
        mRating = rating;

        if (previousRating >= 0) {
            //check to see if we need to do a change. If there is a change from low to high
            //or high to low, then we need to reset the layout. Otherwise we don't do anything.
            if (hasRatingLevelChanged(mRating, previousRating)) {
                resetLayout();
            }
        } else {
            //there was no previous rating, so reset the layout
            resetLayout();
        }
    }

    /**
     * If a rating is 3 stars or lower, then it's considered low
     * If a rating is 4 stars or higher, it's considered high.
     *
     * Remember ratings are 0-indexed
     *
     * @return true if the rating changed from high to low, or vice versa
     */
    private boolean hasRatingLevelChanged(int newRating, int oldRating) {
        return (newRating >= 3 && oldRating < 3) || (newRating < 3 && oldRating >= 3);
    }

    /**
     * This is the main method that must be called to setup the display properly.
     */
    public void resetLayout()
    {
        if (mRating < 0)
        {
            //this view is not ready to initialize. Not enough data. Hide everything
            mPager.setVisibility(View.GONE);
            mSimpleContainer.setVisibility(View.GONE);
            return;
        }

        if (isProAlreadyOnTeam())
        {
            if (mRating <= 2)   //remember this is 0 indexed. This is <= 3 stars
            {
                resetForViewPager();
            }
            else
            {
                //HIDE EVERYTHING!!
                mRootContainer.setVisibility(View.GONE);
            }
        }
        else
        {    //pro is not yet on team
            if (mRating <= 2)   //remember this is 0 indexed. This is <= 3 stars
            {
                resetSimpleViewForBlockPro();
            }
            else
            {
                //show the flow to add to pro team
                resetSimpleViewForAddPro();
            }

        }
    }

    /**
     * This updates the layout to say the things specifically for the case where we want to block this
     * pro.
     */
    private void resetSimpleViewForBlockPro()
    {
        mPager.setVisibility(View.GONE);
        mSimpleContainer.setVisibility(View.VISIBLE);
        resetViewForBlockPro(mToggleButton, mTextTitle);
    }

    /**
     * Takes in views that are associated with the block pro layout, and sets it up accordingly
     */
    private void resetViewForBlockPro(ImageToggleButton button, TextView textView) {
        button.setChecked(false);
        textView.setText(mTitleBlockPro);
        button.setCheckedText(mButtonTextBlockPro);
        button.setUncheckedText(mButtonTextBlockPro);
        button.setCheckedDrawable(mActiveBlockDrawable);
        button.setUncheckedDrawable(mInactiveBlockDrawable);
        button.setTag(TAG_BLOCK_PRO);
        button.updateState();
    }

    /**
     * Takes in views that are associated with the block pro layout, and sets it up accordingly
     */
    private void resetViewForRemovePro(ImageToggleButton button, TextView textView) {
        button.setChecked(false);
        textView.setText(mTitleRemovePro);
        button.setCheckedText(mButtonTextRemovePro);
        button.setUncheckedText(mButtonTextRemovePro);
        button.setCheckedDrawable(mActiveRemoveDrawable);
        button.setUncheckedDrawable(mInactiveRemoveDrawable);
        button.updateState();
    }


    /**
     * This updates the layout to say the things specifically for the case where we want to add this
     * pro to the pro team.
     */
    private void resetSimpleViewForAddPro()
    {
        mPager.setVisibility(View.GONE);
        mSimpleContainer.setVisibility(View.VISIBLE);
        mTextTitle.setText(mTitleAddPro);
        mToggleButton.setChecked(false);
        mToggleButton.setCheckedText(mButtonTextAddPro);
        mToggleButton.setUncheckedText(mButtonTextAddPro);
        mToggleButton.setCheckedDrawable(mActiveAddDrawable);
        mToggleButton.setUncheckedDrawable(mInactiveAddDrawable);
        mToggleButton.setTag(TAG_ADD_PRO);
        mToggleButton.updateState();
    }

    private void resetForViewPager() {
        mRootContainer.setVisibility(View.VISIBLE);
        mPager.setVisibility(View.VISIBLE);
        mSimpleContainer.setVisibility(View.GONE);
        mCurrentClickedToggleButton = null;
        mAdapter = new ProTeamPagerAdapter();
        mPager.setAdapter(mAdapter);
    }

    /**
     * This is the method that should be called to retrieve the user's final decision on what
     * he's selected through the possible combinations of buttons
     * @return
     */
    public ProviderMatchPreference getNewProviderMatchPreference() {
        if (mPager.getVisibility() == View.VISIBLE) {
            /*
                this is the case where the user can remove pro, and then subsequently block the pro.
                There is a trick here, so be very very careful. If the pager is on the first page,
                then the user hasn't clicked anything. If the user is on the second page, that
                IMPLIES the user clicked YES to the first page. If the user is on the second page,
                AND the button is active, then the user has also clicked YES on the second page.
             */
            if (mPager.getCurrentItem() > 0) {  //on the second page
                if (mCurrentClickedToggleButton != null && mCurrentClickedToggleButton.isChecked()) {
                    return ProviderMatchPreference.NEVER;
                } else {
                    //this means to remove the pro.
                    return ProviderMatchPreference.INDIFFERENT;
                }
            }
        } else {
            //this is the straight forward case where use can either add or block a pro
            if (mToggleButton.isChecked()) {
                if (mToggleButton.getTag().equals(TAG_ADD_PRO)) {
                    return ProviderMatchPreference.PREFERRED;
                } else if (mToggleButton.getTag().equals(TAG_BLOCK_PRO)){
                    return ProviderMatchPreference.NEVER;
                } else {
                    Crashlytics.logException(new RuntimeException("This is not supposed to " +
                            "happen, no tag set for toggle button"));
                }
            }
        }

        //defaults to the user not making a selection
        return ProviderMatchPreference.INDIFFERENT;
    }

    /**
     * As of today, a pro is considered already on the team if the match preference says "preferred"
     *
     * @return
     */
    private boolean isProAlreadyOnTeam()
    {
        return mInitialMatchPreference == ProviderMatchPreference.PREFERRED;
    }

    private void animateToPage(final int page) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                mPager.setCurrentItem(page);
            }
        }, ANIMATION_WAIT_TIME_MS);
    }

    /**
     * The adapter that powers the view pager. Page one will show "remove" pro functionality.
     * Page 2 is for "block" pro
     */
    public class ProTeamPagerAdapter extends PagerAdapter
    {
        @Override
        public Object instantiateItem(ViewGroup parent, int position) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.rate_pro_team_item, parent, false);

            TextView textView = (TextView) layout.findViewById(R.id.rate_dialog_pro_match_header_txt);
            ImageToggleButton button = (ImageToggleButton) layout.findViewById(R.id.toggle_button);

            if (position == 0) {
                //on the 1st page, we show option for removing pro
                button.setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v)
                    {
                        animateToPage(1);
                    }
                });
                resetViewForRemovePro(button, textView);
            } else {
                //on the 2nd page, we show option for removing pro
                button.setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v)
                    {
                        mCurrentClickedToggleButton = (ImageToggleButton) v;
                    }
                });
                resetViewForBlockPro(button, textView);
            }

            parent.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}

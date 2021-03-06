package com.handybook.handybook.booking.rating;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.fragment.BaseDialogFragment;
import com.handybook.handybook.library.ui.view.SwipeableViewPager;
import com.handybook.handybook.library.util.FragmentUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This Dialog Fragment will use a view pager to display a workflow to the user for providing
 * feedback about a booking. This viewpager will display fragments. The viewpager is not meant
 * for user interaction
 * <p>
 * Created by jtse on 3/29/16.
 */
public class RateImprovementDialogFragment extends BaseDialogFragment
        implements WizardCallback, ViewPager.OnPageChangeListener {

    static final String EXTRA_REASONS = "reasons";
    static final String EXTRA_FIRST_FRAGMENT = "first_fragment";
    private static final String TAG = RateImprovementDialogFragment.class.getName();

    @BindView(R.id.pager)
    SwipeableViewPager mPager;

    private WizardPagerAdapter mAdapter;

    private RatingsRadioFragment mRatingsRadioFragment;
    private RatingsGridFragment mQualityFragment;

    private String mBookingId;
    private PrerateProInfo mPrerateProInfo;

    /**
     * This holds the user's response
     */
    private RateImprovementFeedback mFeedback;

    /**
     * This is a dynamically generated list of fragments depending on the user's selection.
     * The first fragment will always be the main fragment with list of options.
     * The next series of fragments will be determined depending on the user's selections,
     */
    private List<BaseWizardFragment> mFragmentList;

    public static RateImprovementDialogFragment newInstance(String bookingId) {
        final RateImprovementDialogFragment fragment = new RateImprovementDialogFragment();

        final Bundle args = new Bundle();
        args.putString(BundleKeys.BOOKING_ID, bookingId);
        fragment.setArguments(args);
        return fragment;
    }

    public static RateImprovementDialogFragment newInstance(
            String bookingId,
            PrerateProInfo prerateProInfo
    ) {
        final RateImprovementDialogFragment fragment = new RateImprovementDialogFragment();
        final Bundle args = new Bundle();
        args.putString(BundleKeys.BOOKING_ID, bookingId);
        args.putSerializable(BundleKeys.PRERATE_PRO_INFO, prerateProInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    public void onStop() {
        mBus.unregister(this);
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.dialog_rate_improvement, container, false);

        ButterKnife.bind(this, view);

        mFragmentList = new ArrayList<>();

        mAdapter = new WizardPagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.addOnPageChangeListener(this);

        final Bundle args = getArguments();

        mBookingId = args.getString(BundleKeys.BOOKING_ID);
        mPrerateProInfo = (PrerateProInfo) args.getSerializable(BundleKeys.PRERATE_PRO_INFO);

        if (TextUtils.isEmpty(mBookingId)) {
            Crashlytics.logException(new RuntimeException("No booking ID was passed to " + TAG));
            dismiss();
        }
        if (mPrerateProInfo != null) {
            initialize();
        }
        mPager.setVisibility(View.VISIBLE);
        mFeedback = new RateImprovementFeedback(mBookingId);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //must request after calling onResume, because that guarantees that we are registered onto the bus
        if (mPrerateProInfo == null && !TextUtils.isEmpty(mBookingId)) {
            progressDialog.show();
            mBus.post(new BookingEvent.RequestPrerateProInfo(mBookingId));
        }
    }

    private void initialize() {
        RatingsGridFragment mainFragment
                = RatingsGridFragment.newInstance(mPrerateProInfo.getReasons(), true);
        mFragmentList.add(mainFragment);
        mAdapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }

    /**
     * The request for rating information is successful, create the fragment and display on the view pager.
     *
     * @param event
     */
    @Subscribe
    public void onRequestPrerateProInfoSuccess(BookingEvent.ReceivePrerateProInfoSuccess event) {
        mPrerateProInfo = event.getPrerateProInfo();
        initialize();
    }

    @Subscribe
    public void onRequestPrerateProInfoError(BookingEvent.ReceivePrerateProInfoError error) {
        handleRequestError(error);

        //since there was an issue loading the ratings dialog, then log and dismiss. Pretend nothing happened.
        //Don't worry, the next time an activity is launched, this ratings dialog will automatically re-appear.
        progressDialog.dismiss();
        dismiss();
    }

    @Override
    public void done(BaseWizardFragment callerFragment) {
        if (callerFragment instanceof RatingsGridFragment &&
            ((RatingsGridFragment) callerFragment).isFirstFragment()) {
            //get the user data from the first fragment
            List<Reason> selectedReasons
                    = ((RatingsGridFragment) callerFragment).getSelectedItems();

            //This is the first screen to the user, depending on its selection
            //everything else changes. So get rid of the rest of the pages in this pager
            boolean adapterChanged = false;
            while (mFragmentList.size() > 1) {
                mFragmentList.remove(mFragmentList.size() - 1);
                adapterChanged = true;
            }

            //for each "reason" selected, if there are sub reasons, then add those fragments to
            //the pager adapter.
            for (int i = selectedReasons.size() - 1; i >= 0; i--) {
                Reason r = selectedReasons.get(i);

                if (r.getSubReasons() != null) {
                    switch (r.getKey()) {
                        case Reason.QUALITY_OF_SERVICE:
                            if (mQualityFragment == null) {
                                mQualityFragment
                                        = RatingsGridFragment.newInstance(r.getSubReasons(), false);
                            }

                            //Grids are added to the end of the list.
                            mFragmentList.add(mQualityFragment);
                            adapterChanged = true;
                            break;
                        case Reason.ARRIVED_LATE:
                            if (mRatingsRadioFragment == null) {
                                mRatingsRadioFragment
                                        = RatingsRadioFragment.newInstance(r.getSubReasons());
                            }

                            //radios are added to the front of the list
                            mFragmentList.add(1, mRatingsRadioFragment);
                            adapterChanged = true;
                            break;
                    }
                }
            }

            if (adapterChanged) {
                mAdapter.notifyDataSetChanged();
            }
        }

        //If there are more pages to go, advance the pager to the next page
        if (mFragmentList.size() - 1 > mPager.getCurrentItem()) {
            mPager.setCurrentItem(mPager.getCurrentItem() + 1);
        }
        else {
            submitResponse();
        }
    }

    /**
     * The user is done with the ratings flow, submit the response to the server.
     */
    private void submitResponse() {
        //for every fragment that was part of the wizard, get the results. The reason we do it
        //here instead of getting the results right away when the user clicks "next" is so that
        //we don't have to worry about synching the user's selections, if they hit back and select
        //a different option the second time.
        for (BaseWizardFragment frag : mFragmentList) {
            mFeedback.putAll(frag.getSelectedItemsMap());
        }

        progressDialog.show();
        mBus.post(new BookingEvent.PostLowRatingFeedback(mFeedback));
    }

    @Subscribe
    public void submissionSuccessResponse(BookingEvent.PostLowRatingFeedbackSuccess response) {
        progressDialog.dismiss();

        int bookingId = Integer.parseInt(mBookingId);
        Set<String> subReasons = new HashSet<>();
        for (String key : mFeedback.getSelectedOptions().keySet()) {
            if (!mFeedback.getSelectedOptions().get(key).isEmpty()) {
                for (String s : mFeedback.getSelectedOptions().get(key)) {
                    subReasons.add(s);
                }
            }
        }

        FragmentUtils.safeLaunchDialogFragment(
                RateImprovementConfirmationDialogFragment.newInstance(bookingId),
                getActivity(),
                RateImprovementConfirmationDialogFragment.class.getSimpleName()
        );

        dismiss();
    }

    @Subscribe
    public void submissionFailedResponse(BookingEvent.PostLowRatingFeedbackError error) {
        handleRequestError(error);
        progressDialog.dismiss();

        //since we errored out, allow the customer to exit out if they choose.
        allowDialogDismissable();
    }

    @Override
    public void onPageScrolled(
            final int position,
            final float positionOffset,
            final int positionOffsetPixels
    ) {

    }

    @Override
    public void onPageSelected(final int position) {
        //if we're not on the first page, we want to handle backpresses, to help the user
        //swipe their way back to the first page
        if (mPager.getCurrentItem() > 0) {
            getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(
                        final DialogInterface dialog, final int keyCode,
                        final KeyEvent event
                ) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        //keycodes come in pairs. One with action = ACTION_UP and another one for ACTION_DOWN.
                        //We need to skip one of those actions
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            backOnePage();
                        }
                        return true;
                    }

                    // Otherwise return false, do not consume the event
                    return false;
                }
            });
        }
        else {
            applyDefaultKeyListener();
        }
    }

    @Override
    public void onPageScrollStateChanged(final int state) {

    }

    private void backOnePage() {
        mPager.setCurrentItem(mPager.getCurrentItem() - 1);
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    class WizardPagerAdapter extends FragmentStatePagerAdapter {

        public WizardPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        /**
         * This is needed so the FragmentStatePagerAdapter doesn't hold on to stale fragments
         *
         * @param object
         * @return
         */
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

}

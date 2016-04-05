package com.handybook.handybook.booking.rating;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.ui.fragment.RateServiceConfirmDialogFragment;
import com.handybook.handybook.booking.ui.view.DynamicHeightViewPager;
import com.handybook.handybook.ui.fragment.BaseDialogFragment;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This Dialog Fragment will use a view pager to display a workflow to the user for providing
 * feedback about a booking. This viewpager will display fragments. The viewpager is not meant
 * for user interaction
 * <p>
 * Created by jtse on 3/29/16.
 */
public class RateImprovementDialogFragment extends BaseDialogFragment implements WizardCallback
{
    public static final String EXTRA_BOOKING_ID = "booking_id";
    public static final String EXTRA_REASONS = "reasons";
    public static final String EXTRA_FIRST_FRAGMENT = "first_fragment";
    private static final String TAG = RateImprovementDialogFragment.class.getName();

    @Bind(R.id.pager)
    DynamicHeightViewPager mPager;

    FragmentStatePagerAdapter mAdapter;

    RatingsGridFragment mMainFragment;
    RatingsRadioFragment mRatingsRadioFragment;
    RatingsGridFragment mQualityFragment;

    String mBookingId;
    PrerateProInfo mPrerateProInfo;

    /**
     * This holds the user's response
     */
    RateImprovementFeedback mFeedback;

    /**
     * This is a dynamically generated list of fragments depending on the user's selection.
     * The first fragment will always be the main fragment with list of options.
     * The next series of fragments will be determined depending on the user's selections,
     */
    List<Fragment> mFragments;

    public static RateImprovementDialogFragment newInstance(String bookingId)
    {
        final RateImprovementDialogFragment fragment = new RateImprovementDialogFragment();

        final Bundle args = new Bundle();
        args.putString(RateImprovementDialogFragment.EXTRA_BOOKING_ID, bookingId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.dialog_rate_improvement, container, false);

        ButterKnife.bind(this, view);

        mFragments = new ArrayList<>();

        mAdapter = new WizardPagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mAdapter);

        final Bundle args = getArguments();

        mBookingId = args.getString(EXTRA_BOOKING_ID);

        if (TextUtils.isEmpty(mBookingId))
        {
            Crashlytics.logException(new RuntimeException("No booking ID was passed to " + TAG));
            dismiss();
        }
        mPager.setVisibility(View.VISIBLE);

        mFeedback = new RateImprovementFeedback(mBookingId);

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //must request after calling onResume, because that guarantees that we are registered onto the bus
        if (mPrerateProInfo == null && !TextUtils.isEmpty(mBookingId))
        {
            progressDialog.show();
            loadData();
        }
    }

    /**
     * The request for rating information is successful, create the fragment and display on the view pager.
     *
     * @param event
     */
    @Subscribe
    public void onRequestPrerateProInfoSuccess(BookingEvent.RequestPrerateProInfoSuccess event)
    {
        mPrerateProInfo = event.mPrerateProInfo;
        mMainFragment = RatingsGridFragment.newInstance(mPrerateProInfo.getReasons(), true);
        mFragments.add(mMainFragment);
        mAdapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }

    @Subscribe
    public void onRequestPrerateProInfoError(BookingEvent.RequestPrerateProInfoError error)
    {
        //TODO: JIA: revisit this. We may not even want to show a reload view..., just a toast and move on.
        Crashlytics.logException(new RuntimeException(error.error.getMessage()));
        progressDialog.dismiss();
    }

    public void loadData()
    {
        mBus.post(new BookingEvent.RequestPrerateProInfo(mBookingId));
    }

    @Override
    public void done(BaseWizardFragment callerFragment)
    {
        if (callerFragment instanceof RatingsGridFragment && ((RatingsGridFragment) callerFragment).isFirstFragment())
        {
            //get the user data from the first fragment
            List<Reason> selectedReasons = ((RatingsGridFragment) callerFragment).getSelectedItems();

            //This is the first screen to the user, depending on its selection
            //everything else changes. So get rid of the rest of the pages in this pager
            boolean adapterChanged = false;
            while (mFragments.size() > 1)
            {
                mFragments.remove(mFragments.size() - 1);
                adapterChanged = true;
            }

            //for each "reason" selected, if there are sub reasons, then add those fragments to
            //the pager adapter. Otherwise, record the user's selections. We're looping the list
            //backwards, because iOS displays late arrival options first, and that comes later in the list
            for (int i = selectedReasons.size() - 1; i >= 0; i--)
            {
                Reason r = selectedReasons.get(i);

                if (r.subReasons != null)
                {
                    switch (r.key)
                    {
                        case Reason.QUALITY_OF_SERVICE:
                            if (mQualityFragment == null)
                            {
                                mQualityFragment = RatingsGridFragment.newInstance(r.subReasons, false);
                            }
                            mFragments.add(mQualityFragment);
                            adapterChanged = true;
                            break;
                        case Reason.ARRIVED_LATE:
                            if (mRatingsRadioFragment == null)
                            {
                                mRatingsRadioFragment = RatingsRadioFragment.newInstance(r.subReasons);
                            }
                            mFragments.add(mRatingsRadioFragment);
                            adapterChanged = true;
                            break;
                    }
                }
            }

            if (adapterChanged)
            {
                mAdapter.notifyDataSetChanged();
            }
        }

        mFeedback.putAll(callerFragment.getSelectedItemsMap());

        //If there are more pages to go, advance the pager to the next page
        if (mFragments.size() - 1 > mPager.getCurrentItem())
        {
            mPager.setCurrentItem(mPager.getCurrentItem() + 1);
        }
        else
        {
            submitResponse();
        }
    }

    /**
     * The user is done with the ratings flow, submit the response to the server.
     */
    private void submitResponse()
    {
        Log.d(TAG, "submitResponse: " + mFeedback);
        progressDialog.show();
        mBus.post(new BookingEvent.PostLowRatingFeedback(mFeedback));
    }

    @Subscribe
    public void submissionSuccessResponse(BookingEvent.PostLowRatingFeedbackSuccess response)
    {
        progressDialog.dismiss();
        Log.d(TAG, "submissionSuccessResponse: ");
        dismiss();
        //the rating passed in doesn't matter. Just need to be a number less than 4.
        RateServiceConfirmDialogFragment.newInstance(Integer.parseInt(mBookingId), 1).show(getActivity()
                .getSupportFragmentManager(), "RateServiceConfirmDialogFragment");
    }

    @Subscribe
    public void submissionFailedResponse(BookingEvent.PostLowRatingFeedbackError response)
    {
        Toast.makeText(getContext(), R.string.default_error_string, Toast.LENGTH_LONG).show();
        progressDialog.dismiss();
        //The error has been shown by the BaseDataManagerErrorHandler, so we don't have to do anything here.
        Log.d(TAG, "submissionFailedResponse: ");
        //since we errored out, allow the customer to exit out if they choose.
        allowDialogDismissable();
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    class WizardPagerAdapter extends FragmentStatePagerAdapter
    {
        public WizardPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            return mFragments.get(position);
        }

        @Override
        public int getCount()
        {
            /**
             * We only have about 3 fragments, but setting this to a much bigger number so we
             * don't run into issues where we can't advance to the next page.
             */
            return mFragments.size();
        }
    }

}

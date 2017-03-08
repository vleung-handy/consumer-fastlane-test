package com.handybook.handybook.ratingflow.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.rating.GridDisplayItem;
import com.handybook.handybook.booking.rating.RateImprovementFeedback;
import com.handybook.handybook.booking.rating.Reason;
import com.handybook.handybook.booking.rating.Reasons;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.VoidDataManagerCallback;
import com.handybook.handybook.ratingflow.ui.view.TextOptionsLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RatingFlowImprovementFragment extends RatingFlowFeedbackChildFragment {

    private ArrayList<Reasons> mReasonsQueue;
    private List<GridDisplayItem> mDisplayItems;
    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener =
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(
                        final CompoundButton buttonView,
                        final boolean isChecked
                ) {
                    setSubmissionEnabled(hasSelectedItems());
                }
            };
    private Reasons mReasons;
    private RateImprovementFeedback mImprovementFeedback;

    @NonNull
    public static RatingFlowImprovementFragment newInstance(
            @NonNull final ArrayList<Reasons> reasonsQueue,
            @NonNull RateImprovementFeedback improvementFeedback
    ) {
        final RatingFlowImprovementFragment fragment = new RatingFlowImprovementFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.PRO_RATING_REASONS_QUEUE, reasonsQueue);
        arguments.putSerializable(BundleKeys.IMPROVEMENT_FEEDBACK, improvementFeedback);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReasonsQueue = (ArrayList<Reasons>) getArguments()
                .getSerializable(BundleKeys.PRO_RATING_REASONS_QUEUE);
        mImprovementFeedback = (RateImprovementFeedback) getArguments()
                .getSerializable(BundleKeys.IMPROVEMENT_FEEDBACK);
        mReasons = mReasonsQueue.remove(0);
    }

    @Override
    public void onViewCreated(
            final View view, @Nullable final Bundle savedInstanceState
    ) {
        mSectionTitle.setText(mReasons.getTitle());
        mSectionSubtitle.setVisibility(View.VISIBLE);
        mSectionSubtitle.setText(R.string.select_all_that_apply);
        initSelectionItems();
        setSubmissionEnabled(false);
    }

    private void initSelectionItems() {
        mDisplayItems = new ArrayList<>();
        for (final Reason reason : mReasons.getReasons()) {
            mDisplayItems.add(new GridDisplayItem(reason, reason.getDrawableRes(), false));
        }
        mSectionContainer.addView(new TextOptionsLayout(
                getActivity(),
                mDisplayItems,
                mOnCheckedChangeListener
        ));
    }

    private boolean hasSelectedItems() {
        return !getSelectedItems().isEmpty();
    }

    @NonNull
    private List<GridDisplayItem> getSelectedItems() {
        final List<GridDisplayItem> selectedItems = new ArrayList<>();
        for (final GridDisplayItem displayItem : mDisplayItems) {
            if (displayItem.isSelected()) {
                selectedItems.add(displayItem);
            }
        }
        return selectedItems;
    }

    /**
     * This returns the selected item in the form of a Map<String, ArrayList<String>>. This is what
     * is needed to send back to the server
     *
     * @return
     */
    @NonNull
    private Map<String, ArrayList<String>> getSelectedItemsMap() {
        final HashMap<String, ArrayList<String>> selectedItemsMap = new HashMap<>();

        if (TextUtils.isEmpty(mReasons.getKey())) {
            /**
             * If there is no key, we build the map like so:
             "professionalism": {},
             "left_early": {},
             */
            for (final GridDisplayItem item : getSelectedItems()) {
                selectedItemsMap.put(item.getReason().getKey(), new ArrayList<String>());
            }
        }
        else {
            /**
             * If there IS a key, we build the map like so:
             "quality_of_service": ["living_room", "kitchen", "bedroom"]
             */
            final ArrayList<String> values = new ArrayList<>();
            for (final GridDisplayItem item : getSelectedItems()) {
                values.add(item.getReason().getKey());
            }
            selectedItemsMap.put(mReasons.getKey(), values);
        }

        return selectedItemsMap;
    }

    @Override
    void onSubmit() {
        for (final GridDisplayItem gridDisplayItem : getSelectedItems()) {
            final Reasons subReasons = gridDisplayItem.getReason().getSubReasons();
            if (subReasons != null && !mReasonsQueue.contains(subReasons)) {
                mReasonsQueue.add(subReasons);
            }
        }
        mImprovementFeedback.putAll(getSelectedItemsMap());
        if (mReasonsQueue.isEmpty()) {
            dataManager.postLowRatingFeedback(mImprovementFeedback, new VoidDataManagerCallback());
            finishStep();
        }
        else {
            showFragment(RatingFlowImprovementFragment.newInstance(
                    mReasonsQueue,
                    mImprovementFeedback
            ));
        }
    }
}

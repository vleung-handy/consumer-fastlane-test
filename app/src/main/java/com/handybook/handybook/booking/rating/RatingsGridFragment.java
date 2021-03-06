package com.handybook.handybook.booking.rating;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jtse on 3/30/16.
 */
public class RatingsGridFragment extends BaseWizardFragment {

    private static final String EXTRA_SELECTED_ITEMS = "selected_items";

    @BindView(R.id.grid_view)
    GridView mGridView;

    private List<GridDisplayItem> mDisplayedItems;
    private GridAdapter mAdapter;

    private int mDefaultBGColor;
    private int mDefaultFGColor;
    private int mSelectedBGColor;
    private int mSelectedFGColor;
    private boolean mIsFirstFragment;
    private Reasons mReasons;
    ArrayList<Reason> mSelectedItems;

    public static RatingsGridFragment newInstance(Reasons displayItems, boolean isFirstFragment) {
        final RatingsGridFragment fragment = new RatingsGridFragment();
        final Bundle args = new Bundle();

        args.putSerializable(RateImprovementDialogFragment.EXTRA_REASONS, displayItems);
        args.putBoolean(RateImprovementDialogFragment.EXTRA_FIRST_FRAGMENT, isFirstFragment);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_ratings_grid, container, false);
        ButterKnife.bind(this, view);

        mDefaultBGColor = ContextCompat.getColor(getContext(), R.color.handy_white);
        mSelectedBGColor = ContextCompat.getColor(getContext(), R.color.handy_blue);
        mSelectedFGColor = ContextCompat.getColor(getContext(), R.color.handy_white);
        mDefaultFGColor = ContextCompat.getColor(getContext(), R.color.handy_tertiary_gray);

        if (savedInstanceState != null) {
            mSelectedItems = (ArrayList<Reason>) savedInstanceState.getSerializable(
                    EXTRA_SELECTED_ITEMS);
        }

        if (mSelectedItems == null) {
            mSelectedItems = new ArrayList<>();
        }

        mReasons
                = (Reasons) getArguments().getSerializable(RateImprovementDialogFragment.EXTRA_REASONS);
        mIsFirstFragment = getArguments().getBoolean(
                RateImprovementDialogFragment.EXTRA_FIRST_FRAGMENT,
                false
        );

        convertToDisplayItems(mReasons, mSelectedItems);

        mTvTitle.setText(mReasons.getTitle());
        mTvAllApply.setVisibility(View.VISIBLE);

        if (mIsFirstFragment) {
            mTvAnonymous.setVisibility(View.VISIBLE);
        }
        else {
            mTvAnonymous.setVisibility(View.GONE);
        }

        mAdapter = new GridAdapter();
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GridDisplayItem item = mDisplayedItems.get(position);
                item.setSelected(!item.isSelected());
                mAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    /**
     * This is super hacky, since we have to manually pair the mReasons with a drawable.
     */
    private void convertToDisplayItems(
            @NonNull Reasons reasons,
            @NonNull List<Reason> selectedReasons
    ) {
        mDisplayedItems = new ArrayList<>();
        for (Reason reason : reasons.getReasons()) {
            boolean mSelected = false;
            for (Reason r : selectedReasons) {
                if (reason.getKey().equals(r.getKey())) {
                    mSelected = true;
                    break;
                }
            }

            mDisplayedItems.add(new GridDisplayItem(reason, reason.getDrawableRes(), mSelected));
        }
    }

    public List<Reason> getSelectedItems() {
        mSelectedItems.clear();
        for (GridDisplayItem item : mDisplayedItems) {
            if (item.isSelected()) {
                mSelectedItems.add(item.getReason());
            }
        }

        return mSelectedItems;
    }

    /**
     * This returns the selected item in the form of a Map<String, ArrayList<String>>. This is what
     * is needed to send back to the server
     *
     * @return
     */
    @Override
    HashMap<String, ArrayList<String>> getSelectedItemsMap() {
        HashMap<String, ArrayList<String>> rval = new HashMap<>();

        if (TextUtils.isEmpty(mReasons.getKey())) {
            /**
             * If there is no key, we build the map like so:
             "professionalism": {},
             "left_early": {},
             */

            for (GridDisplayItem item : mDisplayedItems) {
                if (item.isSelected()) {
                    rval.put(item.getReason().getKey(), new ArrayList<String>());
                }
            }
        }
        else {
            /**
             * If there IS a key, we build the map like so:
             "quality_of_service": ["living_room", "kitchen", "bedroom"]
             */

            ArrayList<String> values = new ArrayList<>();
            for (GridDisplayItem item : mDisplayedItems) {
                if (item.isSelected()) {
                    values.add(item.getReason().getKey());
                }
            }
            rval.put(mReasons.getKey(), values);
        }

        return rval;
    }

    public boolean isFirstFragment() {
        return mIsFirstFragment;
    }

    @OnClick(R.id.ratings_improvement_submit_button)
    void submit() {
        if (getSelectedItems().isEmpty()) {
            showToast(R.string.improvement_selection_required);
        }
        else {
            mCallback.done(this);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_SELECTED_ITEMS, (ArrayList) getSelectedItems());
    }

    public class GridAdapter extends BaseAdapter implements Serializable {

        public int getCount() {
            return mDisplayedItems == null ? 0 : mDisplayedItems.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity())
                                            .inflate(R.layout.rating_grid_item, parent, false);
            }

            GridDisplayItem item = mDisplayedItems.get(position);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view);

            TextView mTitle = (TextView) convertView.findViewById(R.id.tv_label);
            mTitle.setText(item.getReason().getValue());

            if (item.isSelected()) {
                mTitle.setTextColor(mSelectedFGColor);
                imageView.setImageDrawable(getTintedDrawable(
                        item.getDrawableId(),
                        mSelectedFGColor
                ));
                convertView.setBackgroundColor(mSelectedBGColor);
            }
            else {
                mTitle.setTextColor(mDefaultFGColor);
                imageView.setImageDrawable(getTintedDrawable(
                        item.getDrawableId(),
                        mDefaultFGColor
                ));
                convertView.setBackgroundColor(mDefaultBGColor);
            }
            return convertView;
        }

        public Drawable getTintedDrawable(@DrawableRes int drawableResId, int color) {
            Drawable drawable = ContextCompat.getDrawable(getContext(), drawableResId);
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            return drawable;
        }
    }
}

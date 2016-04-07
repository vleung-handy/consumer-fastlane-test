package com.handybook.handybook.booking.rating;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.handybook.handybook.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jtse on 3/30/16.
 */
public class RatingsRadioFragment extends BaseWizardFragment
{
    private static final String TAG = RatingsRadioFragment.class.getName();
    public static final String SELECTED_KEY = "selected_key";

    @Bind(R.id.rating_radio_group)
    RadioGroup mRadioGroup;

    @Bind(R.id.ratings_late_submit_button)
    Button mSubmitButton;

    Map<String, String> mValuesToKeys;
    Reasons mReasons;

    String mSelectedKey;

    private String mSubmitText;
    private String mNextText;

    public static RatingsRadioFragment newInstance(Reasons displayItems)
    {
        final RatingsRadioFragment fragment = new RatingsRadioFragment();
        final Bundle args = new Bundle();
        args.putSerializable(RateImprovementDialogFragment.EXTRA_REASONS, displayItems);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_ratings_radio, container, false);
        ButterKnife.bind(this, view);

        mReasons = (Reasons) getArguments().getSerializable(RateImprovementDialogFragment.EXTRA_REASONS);
        mValuesToKeys = new HashMap<>();

        mTvTitle.setText(mReasons.mTitle);
        mTvAllApply.setVisibility(View.GONE);
        mTvAnonymous.setVisibility(View.GONE);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(final RadioGroup group, final int checkedId)
            {
                String radiovalue = ((RadioButton) group.findViewById(checkedId)).getText().toString();
                mSelectedKey = mValuesToKeys.get(radiovalue);
                Log.d(TAG, "onCheckedChanged: checked: " + mSelectedKey);
            }
        });

        for (int i = 0; i < mReasons.mReasons.size(); i++)
        {
            Reason r = mReasons.mReasons.get(i);
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.rating_radio_button, mRadioGroup, false);
            rb.setText(r.value);

            mValuesToKeys.put(r.value, r.key);
            mRadioGroup.addView(rb);

            //defaults to the first element as selected
            if ((i == 0 && TextUtils.isEmpty(mSelectedKey)) || mSelectedKey.equals(r.key))
            {
                mRadioGroup.check(rb.getId());
            }
        }

        mSubmitText = getResources().getString(R.string.submit);
        mNextText = getResources().getString(R.string.next);

        if (((RateImprovementDialogFragment) getParentFragment()).haveMorePages())
        {
            mSubmitButton.setText(mNextText);
        }
        else
        {
            mSubmitButton.setText(mSubmitText);
        }

        return view;
    }

    @OnClick(R.id.ratings_late_submit_button)
    public void submit()
    {
        mCallback.done(this);
    }

    /**
     * We build the map like so:
     * "arrived_late": ["more_than_30_minutes_late"]
     */
    @Override
    HashMap<String, ArrayList<String>> getSelectedItemsMap()
    {
        HashMap<String, ArrayList<String>> rval = new HashMap<>();

        ArrayList<String> values = new ArrayList<>();
        String radiovalue = ((RadioButton) mRadioGroup.findViewById(mRadioGroup.getCheckedRadioButtonId())).getText().toString();
        values.add(mValuesToKeys.get(radiovalue));
        rval.put(mReasons.mKey, values);
        return rval;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(SELECTED_KEY, mSelectedKey);
    }
}


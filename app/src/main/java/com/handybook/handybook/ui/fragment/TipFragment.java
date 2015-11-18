package com.handybook.handybook.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.core.LocalizedMonetaryAmount;
import com.handybook.handybook.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TipFragment extends Fragment
{
    static final String EXTRA_DEFAULT_TIP_AMOUNTS = "com.handy.handy.EXTRA_DEFAULT_TIP_AMOUNTS";

    @Bind(R.id.tip_amount_radio_group)
    RadioGroup mTipAmountRadioGroup;
    @Bind(R.id.custom_tip_amount_wrapper)
    LinearLayout mCustomTipAmountWrapperLayout;
    @Bind(R.id.custom_tip_amount)
    EditText mCustomTipAmountText;

    static final int MAX_CUSTOM_TIP_VALUES = 3; //our UI currently supports None, Custom, and 3 pre-defined tip amounts coming from the server

    private int mTipAmount;
    private Map<RadioButton, Integer> mRadioButtonToTipAmount = new HashMap<>();
    private boolean mSendTipAmount = false;
    private boolean mCustomTipSelected = false;

    public static TipFragment newInstance(final ArrayList<LocalizedMonetaryAmount> defaultTipAmounts)
    {
        TipFragment tipFragment = new TipFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(EXTRA_DEFAULT_TIP_AMOUNTS, defaultTipAmounts);
        tipFragment.setArguments(bundle);
        return tipFragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_tip, container, false);
        ButterKnife.bind(this, view);

        ArrayList<LocalizedMonetaryAmount> defaultTipAmounts = getArguments().getParcelableArrayList(EXTRA_DEFAULT_TIP_AMOUNTS);
        updateTipAmountDisplay(defaultTipAmounts);

        initTipListeners();

        return view;
    }

    public Integer getTipAmount()
    {
        if (mSendTipAmount)
        {
            return mCustomTipSelected ? getCustomTipAmount() : mTipAmount;
        }
        else
        {
            return null;
        }
    }

    private Integer getCustomTipAmount()
    {
        return Utils.convertToCents(Float.parseFloat(mCustomTipAmountText.getText().toString()));
    }

    private void setTipAmount(final int tipAmount)
    {
        mTipAmount = tipAmount;
    }

    private void setSendTipAmount(final boolean sendTipAmount)
    {
        mSendTipAmount = sendTipAmount;
    }

    private void setCustomTipSelected(final boolean customTipSelected)
    {
        mCustomTipSelected = customTipSelected;
    }

    private void updateTipAmountDisplay(final List<LocalizedMonetaryAmount> defaultTipAmounts)
    {
        if (defaultTipAmounts != null && !defaultTipAmounts.isEmpty())
        {
            int maxEntriesToDisplay = Math.min(defaultTipAmounts.size(), MAX_CUSTOM_TIP_VALUES);

            for (int i = 0; i < maxEntriesToDisplay; i++)
            {
                int radioButtonGroupIndex = i + 1;
                if (mTipAmountRadioGroup.getChildCount() > radioButtonGroupIndex)
                {
                    RadioButton childRadioButton = (RadioButton) mTipAmountRadioGroup.getChildAt(radioButtonGroupIndex);
                    childRadioButton.setText(defaultTipAmounts.get(i).getDisplayAmount());

                    // Create a mapping of the child radio button to the tip amount
                    mRadioButtonToTipAmount.put(childRadioButton, defaultTipAmounts.get(i).getAmountInCents());
                }
            }
        }
    }

    private void initTipListeners()
    {
        mTipAmountRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup rGroup, int checkedId)
            {
                RadioButton checkedRadioButton = (RadioButton) rGroup.findViewById(checkedId);

                if (mRadioButtonToTipAmount.containsKey(checkedRadioButton))
                {
                    setTipAmount(mRadioButtonToTipAmount.get(checkedRadioButton));
                    setSendTipAmount(true);
                    setCustomTipSelected(false);
                    mCustomTipAmountWrapperLayout.setVisibility(View.GONE);
                }
                else if (pickedOtherAmount(checkedRadioButton, rGroup))
                {
                    setTipAmount(0);
                    setSendTipAmount(true);
                    setCustomTipSelected(true);
                    mCustomTipAmountWrapperLayout.setVisibility(View.VISIBLE);
                    mCustomTipAmountWrapperLayout.requestFocus();
                }
                else
                {
                    setCustomTipSelected(false);
                    setSendTipAmount(false);
                    mCustomTipAmountWrapperLayout.setVisibility(View.GONE);
                }
            }

            private boolean pickedOtherAmount(final RadioButton checkedRadioButton, final RadioGroup radioGroup)
            {
                return checkedRadioButton.getId() == radioGroup.getChildAt(radioGroup.getChildCount() - 1).getId();
            }
        });

        mCustomTipAmountText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                {
                    Utils.showSoftKeyboard(getActivity(), v);
                }
                else
                {
                    Utils.hideSoftKeyboard(getActivity(), v);
                }
            }
        });
    }

}

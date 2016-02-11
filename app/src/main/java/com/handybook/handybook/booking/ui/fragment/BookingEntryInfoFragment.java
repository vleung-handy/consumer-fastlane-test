package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.BookingPostInfo;
import com.handybook.handybook.booking.model.Instructions;
import com.handybook.handybook.booking.ui.activity.BookingFinalizeActivity;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.ui.activity.BaseActivity;
import com.handybook.handybook.ui.widget.BasicInputTextView;
import com.handybook.handybook.util.TextUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;


public final class BookingEntryInfoFragment extends BookingFlowFragment
        implements BaseActivity.OnBackPressedListener
{
    static final String EXTRA_NEW_USER = "com.handy.handy.EXTRA_NEW_USER";
    static final String EXTRA_INSTRUCTIONS = "com.handy.handy.EXTRA_INSTRUCTIONS";
    private static final String STATE_KEY_HIGHLIGHT = "KEY_HIGHLIGHT";

    private BookingOptionsView mOptionsView;
    private BookingPostInfo mPostInfo;
    private boolean mIsNewUser;
    private Instructions mInstructions;

    @Bind(R.id.options_layout)
    LinearLayout mOptionsLayout;
    @Bind(R.id.header_text)
    TextView mHeaderText;
    @Bind(R.id.next_button)
    Button mNextButton;
    @Bind(R.id.keys_text)
    BasicInputTextView mKeysText;

    public static BookingEntryInfoFragment newInstance(
            final boolean isNewUser,
            final Instructions instructions
    )
    {
        final BookingEntryInfoFragment fragment = new BookingEntryInfoFragment();
        final Bundle args = new Bundle();

        args.putBoolean(EXTRA_NEW_USER, isNewUser);
        args.putParcelable(EXTRA_INSTRUCTIONS, instructions);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mIsNewUser = getArguments().getBoolean(EXTRA_NEW_USER, false);
        mInstructions = getArguments().getParcelable(EXTRA_INSTRUCTIONS);
        mixpanel.trackEventAppTrackEntryInfo();
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_entry_info, container, false);

        ButterKnife.bind(this, view);

        mKeysText.setMinLength(2);
        mKeysText.setHint(getString(R.string.where_hide_key));
        mKeysText.addTextChangedListener(keyTextWatcher);
        mPostInfo = bookingManager.getCurrentPostInfo();
        final String text = getString(R.string.payment_confirmed);
        final SpannableString spanText = new SpannableString(text);

        spanText.setSpan(new CalligraphyTypefaceSpan(TextUtils.get(getActivity(),
                        "CircularStd-Medium.otf")), 0, text.indexOf("\n"),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mHeaderText.setText(spanText, TextView.BufferType.SPANNABLE);
        final BookingOption option = new BookingOption();
        option.setType(BookingOption.TYPE_OPTION);
        option.setDefaultValue("0");

        option.setOptions(new String[]{getString(R.string.will_be_home),
                getString(R.string.doorman), getString(R.string.will_hide_key)});
        mOptionsView = new BookingOptionsSelectView(getActivity(), option, optionUpdated);
        ((BookingOptionsSelectView) mOptionsView).hideTitle();
        final String indexStr = mPostInfo.getGetInId();
        final int index = indexStr == null ? 0 : Integer.parseInt(indexStr);
        ((BookingOptionsSelectView) mOptionsView).setCurrentIndex(index);
        mOptionsLayout.addView(mOptionsView, 0);
        mNextButton.setOnClickListener(nextClicked);
        return view;
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null)
        {
            if (savedInstanceState.getBoolean(STATE_KEY_HIGHLIGHT))
            {
                mKeysText.highlight();
            }
        }
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_KEY_HIGHLIGHT, mKeysText.isHighlighted());
    }

    @Override
    public final void onStart()
    {
        super.onStart();
        ((BaseActivity) getActivity()).setOnBackPressedListener(this);
    }

    @Override
    public final void onStop()
    {
        super.onStop();
        ((BaseActivity) getActivity()).setOnBackPressedListener(null);
    }

    @Override
    protected final void disableInputs()
    {
        super.disableInputs();
        mNextButton.setClickable(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        mNextButton.setClickable(true);
    }

    private boolean validateFields()
    {
        boolean validate = true;

        if (((BookingOptionsSelectView) mOptionsView).getCurrentIndex() == 2 && !mKeysText.validate())
        {
            validate = false;
        }
        return validate;
    }

    @Override
    public final void onBack()
    {
        showBookings();
    }

    private final View.OnClickListener nextClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View view)
        {
            if (!validateFields() ||
                    bookingManager.getCurrentTransaction() == null)
                    /*
                    hot fix to prevent NPE caused by rapid multi-click
                    of the next button
                     */
            {
                return;
            }

            //discourage user from pressing button twice
            //note that this doesn't prevent super fast clicks
            disableInputs();
            progressDialog.show();

            final Intent intent = new Intent(getActivity(), BookingFinalizeActivity.class);
            intent.putExtra(
                    BookingFinalizeActivity.EXTRA_PAGE,
                    BookingFinalizeActivity.PAGE_PREFERENCES
            );
            intent.putExtra(
                    BookingFinalizeActivity.EXTRA_NEW_USER,
                    mIsNewUser
            );
            intent.putExtra(BookingFinalizeActivity.EXTRA_INSTRUCTIONS, mInstructions);
            startActivity(intent);
        }
    };

    private void showBookings()
    {
        bookingManager.clearAll();

        final Intent intent = new Intent(getActivity(), BookingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    private final BookingOptionsView.OnUpdatedListener optionUpdated;

    {
        optionUpdated = new BookingOptionsView.OnUpdatedListener()
        {
            @Override
            public void onUpdate(final BookingOptionsView view)
            {
                final int index = ((BookingOptionsSelectView) view).getCurrentIndex();
                mPostInfo.setGetInId(Integer.toString(index));

                if (index == 2)
                {
                    mKeysText.setVisibility(View.VISIBLE);
                }
                else
                {
                    mKeysText.unHighlight();
                    mKeysText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onShowChildren(
                    final BookingOptionsView view,
                    final String[] items
            )
            {
            }

            @Override
            public void onHideChildren(
                    final BookingOptionsView view,
                    final String[] items
            )
            {
            }
        };
    }

    private final TextWatcher keyTextWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(
                final CharSequence charSequence, final int start,
                final int count, final int after
        )
        {

        }

        @Override
        public void onTextChanged(
                final CharSequence charSequence, final int start,
                final int before, final int count
        )
        {
        }

        @Override
        public void afterTextChanged(final Editable editable)
        {
            mPostInfo.setGetInText(mKeysText.getInput());
        }
    };
}

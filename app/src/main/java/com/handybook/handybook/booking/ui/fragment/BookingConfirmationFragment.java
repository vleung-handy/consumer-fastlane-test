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
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.BookingPostInfo;
import com.handybook.handybook.booking.model.Instructions;
import com.handybook.handybook.booking.ui.activity.BookingConfirmationActivity;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsTextView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.booking.ui.widget.InstructionsLayout;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.ui.activity.BaseActivity;
import com.handybook.handybook.ui.widget.BasicInputTextView;
import com.handybook.handybook.ui.widget.PasswordInputTextView;
import com.handybook.handybook.util.TextUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;


//THIS CLASS HAS BEEN BROKEN UP INTO SUB FRAGMENT TYPES BUT WE ARE STILL USING THE ORIGINAL FRAGMENT
// IN THE BOOKING FLOW, SEE NOTE
//TODO: Break this into distinct fragments, see BookingEditNoteToPro, BookingEditEntryInformation,
// still need to do the password prompt
public final class BookingConfirmationFragment extends BookingFlowFragment
        implements BaseActivity.OnBackPressedListener
{
    private static final int PAGE_ENTRY_INFORMATION = 0;
    private static final int PAGE_PREFERENCES = 1;
    private static final int PAGE_PASSWORD_PROMPT = 2;

    static final String EXTRA_PAGE = "com.handy.handy.EXTRA_PAGE";
    static final String EXTRA_NEW_USER = "com.handy.handy.EXTRA_NEW_USER";
    static final String EXTRA_INSTRUCTIONS = "com.handy.handy.EXTRA_INSTRUCTIONS";
    private static final String STATE_KEY_HIGHLIGHT = "KEY_HIGHLIGHT";
    private static final String STATE_PWD_HIGHLIGHT = "PWD_HIGHLIGHT";

    private BookingOptionsView mOptionsView;
    private BookingPostInfo mPostInfo;
    private int mPage;
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
    @Bind(R.id.password_text)
    PasswordInputTextView mPasswordText;
    @Bind(R.id.instructions_layout)
    InstructionsLayout mInstructionsLayout;

    public static BookingConfirmationFragment newInstance(
            final int page,
            final boolean isNewUser,
            final Instructions instructions
    )
    {
        final BookingConfirmationFragment fragment = new BookingConfirmationFragment();
        final Bundle args = new Bundle();

        args.putInt(EXTRA_PAGE, page);
        args.putBoolean(EXTRA_NEW_USER, isNewUser);
        args.putParcelable(EXTRA_INSTRUCTIONS, instructions);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(EXTRA_PAGE, 0);
        mIsNewUser = getArguments().getBoolean(EXTRA_NEW_USER, false);
        mInstructions = getArguments().getParcelable(EXTRA_INSTRUCTIONS);
        mixpanel.trackEventAppTrackConfirmation();
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_confirmation, container, false);

        ButterKnife.bind(this, view);

        mKeysText.setMinLength(2);
        mKeysText.setHint(getString(R.string.where_hide_key));
        mKeysText.addTextChangedListener(keyTextWatcher);

        mPostInfo = bookingManager.getCurrentPostInfo();

        if (mPage == PAGE_ENTRY_INFORMATION)
        {
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
        }
        else if (mPage == PAGE_PREFERENCES)
        {
            mHeaderText.setText(getString(R.string.booking_edit_preferences_subtitle));
            if (!mIsNewUser)
            {
                mNextButton.setText(getString(R.string.finish));
            }

            final BookingOption option = new BookingOption();
            option.setType(BookingOption.TYPE_TEXT);
            option.setDefaultValue(getString(R.string.additional_pro_info_hint));

            mOptionsView = new BookingOptionsTextView(getActivity(), option, textUpdated);
            ((BookingOptionsTextView) mOptionsView).setValue(mPostInfo.getExtraMessage());

            mInstructionsLayout.init(mInstructions);
        }
        else if (mPage == PAGE_PASSWORD_PROMPT)
        {
            mHeaderText.setText(getString(R.string.use_your_pwd));
            mNextButton.setText(getString(R.string.finish));
            mPasswordText.setVisibility(View.VISIBLE);
        }

        if (mPage == PAGE_ENTRY_INFORMATION || mPage == PAGE_PREFERENCES)
        {
            mOptionsLayout.addView(mOptionsView, 0);
        }

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
            if (savedInstanceState.getBoolean(STATE_PWD_HIGHLIGHT))
            {
                mPasswordText.highlight();
            }
        }
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_KEY_HIGHLIGHT, mKeysText.isHighlighted());
        outState.putBoolean(STATE_PWD_HIGHLIGHT, mPasswordText.isHighlighted());
    }

    @Override
    public final void onStart()
    {
        super.onStart();
        if (mPage == PAGE_ENTRY_INFORMATION)
        {
            ((BaseActivity) getActivity()).setOnBackPressedListener(this);
        }
    }

    @Override
    public final void onStop()
    {
        super.onStop();
        if (mPage == PAGE_ENTRY_INFORMATION)
        {
            ((BaseActivity) getActivity()).setOnBackPressedListener(null);
        }
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

        if (mPage == PAGE_ENTRY_INFORMATION && ((BookingOptionsSelectView) mOptionsView).getCurrentIndex() == 2
                && !mKeysText.validate())
        {
            validate = false;
        }

        if (mPage == PAGE_PASSWORD_PROMPT)
        {
            if (!mPasswordText.validate())
            {
                validate = false;
            }
            if (mPasswordText.getPassword().length() < 8)
            {
                validate = false;
                mPasswordText.highlight();
                toast.setText(getString(R.string.pwd_length_error));
                toast.show();
            }
        }

        return validate;
    }

    @Override
    public final void onBack()
    {
        if (mPage == PAGE_ENTRY_INFORMATION)
        {
            showBookings();
        }
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

            //TODO: Finish breaking up booking confirmation fragment/activity and then call the
            // specific fragment instead of passing along an EXTRA_PAGE

            if (mPage == PAGE_ENTRY_INFORMATION)
            {
                //goto the next page / activity
                final Intent intent = new Intent(getActivity(), BookingConfirmationActivity.class);
                intent.putExtra(BookingConfirmationActivity.EXTRA_PAGE, PAGE_PREFERENCES);
                intent.putExtra(BookingConfirmationActivity.EXTRA_NEW_USER, mIsNewUser);
                intent.putExtra(BookingConfirmationActivity.EXTRA_INSTRUCTIONS, mInstructions);
                startActivity(intent);
            }
            else if (mPage == PAGE_PREFERENCES && mIsNewUser)
            {
                //goto the next page / activity
                final Intent intent = new Intent(getActivity(), BookingConfirmationActivity.class);
                intent.putExtra(BookingConfirmationActivity.EXTRA_PAGE, PAGE_PASSWORD_PROMPT);
                intent.putExtra(BookingConfirmationActivity.EXTRA_NEW_USER, mIsNewUser);
                startActivity(intent);
            }
            else
            {
                //submit the information
                if (mPage == PAGE_PASSWORD_PROMPT)
                {
                    mPostInfo.setPassword(mPasswordText.getPassword());
                }

                dataManager.addBookingPostInfo(bookingManager.getCurrentTransaction().getBookingId(),
                        mPostInfo, new DataManager.Callback<Void>()
                        {
                            @Override
                            public void onSuccess(final Void response)
                            {
                                if (!allowCallbacks ||
                                        bookingManager.getCurrentTransaction() == null)
                                    /*
                                    hot fix to prevent NPE caused by rapid multi-click
                                    of the next button
                                     */
                                {
                                    return;
                                }

                                String bookingId = Integer.toString(bookingManager.getCurrentTransaction().getBookingId());
                                showBookingDetails(bookingId);
                                enableInputs();
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onError(final DataManager.DataManagerError error)
                            {
                                if (!allowCallbacks)
                                {
                                    return;
                                }

                                enableInputs();
                                progressDialog.dismiss();
                                dataManagerErrorHandler.handleError(getActivity(), error);
                            }
                        });
            }
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

    private void showBookingDetails(String bookingId)
    {
        bookingManager.clearAll();
        User user = userManager.getCurrentUser();
        dataManager.getBooking(bookingId,
                new DataManager.Callback<Booking>()
                {
                    @Override
                    public void onSuccess(final Booking booking)
                    {
                        final Intent intent = new Intent(getActivity(), BookingDetailActivity.class);
                        intent.putExtra(BundleKeys.BOOKING, booking);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        dataManagerErrorHandler.handleError(getActivity(), error);
                        startActivity(new Intent(getActivity(), ServiceCategoriesActivity.class));
                    }
                });
    }

    private final BookingOptionsView.OnUpdatedListener textUpdated
            = new BookingOptionsView.OnUpdatedListener()
    {
        @Override
        public void onUpdate(final BookingOptionsView view)
        {
            mPostInfo.setExtraMessage(view.getCurrentValue());
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
